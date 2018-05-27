package it.sephiroth.android.library.uigestures;

import android.content.Context;
import android.graphics.PointF;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

/**
 * UIPanGestureRecognizer is a subclass of UIGestureRecognizer that looks for panning (dragging) gestures. The user must
 * be pressing one or more fingers on a view while they pan it. Clients implementing the action method for this gesture
 * recognizer can ask it for the current translation and velocity of the gesture.
 *
 * @author alessandro crugnola
 * @see <a href='https://developer.apple.com/reference/uikit/uipangesturerecognizer'>
 * https://developer.apple.com/reference/uikit/uipangesturerecognizer</a>
 */
@SuppressWarnings ("unused")
public class UIScreenEdgePanGestureRecognizer extends UIGestureRecognizer implements UIContinuousRecognizer {
    private static final int MESSAGE_RESET = 4;
    private static final int SCREEN_EDGE_LIMIT_DP = 20;
    private final float mEdgeLimit;
    private int mTouchSlopSquare;

    private boolean mStarted;

    private float mLastFocusX;
    private float mLastFocusY;
    private float mDownFocusX;
    private float mDownFocusY;

    private VelocityTracker mVelocityTracker;
    private int mMinimumNumberOfTouches;
    private int mMaximumNumberOfTouches;
    private float scrollX, scrollY;
    private float mTranslationX;
    private float mTranslationY;
    private float mVelocityY, mVelocityX;
    private final PointF mCurrentLocation;
    private int mTouches;
    private UIRectEdge mEdge = UIRectEdge.LEFT;

    public UIScreenEdgePanGestureRecognizer(@NonNull final Context context) {
        super(context);
        mMinimumNumberOfTouches = 1;
        mMaximumNumberOfTouches = Integer.MAX_VALUE;

        int touchSlop;
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = configuration.getScaledTouchSlop();
        mTouchSlopSquare = touchSlop * touchSlop;
        mCurrentLocation = new PointF();

        mEdgeLimit = TypedValue
            .applyDimension(TypedValue.COMPLEX_UNIT_DIP, SCREEN_EDGE_LIMIT_DP, context.getResources().getDisplayMetrics());
    }

    public void setEdge(UIRectEdge direction) {
        this.mEdge = direction;
    }

    public UIRectEdge getEdge() {
        return mEdge;
    }

    @Override
    protected void handleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_RESET:
                mStarted = false;
                setBeginFiringEvents(false);
                setState(State.Possible);
                break;
            default:
                break;
        }
    }

    @Override
    public int getNumberOfTouches() {
        return mTouches;
    }

    @Override
    public void onStateChanged(@NonNull final UIGestureRecognizer recognizer) {
        logMessage(Log.VERBOSE, "onStateChanged(%s, %s)", recognizer, recognizer.getState());
        logMessage(Log.VERBOSE, "started: %b, state: %s", mStarted, getState());

        if (recognizer.getState() == State.Failed && getState() == State.Began) {
            stopListenForOtherStateChanges();
            fireActionEventIfCanRecognizeSimultaneously();

        } else if (recognizer.inState(State.Began, State.Ended) && mStarted && inState(State.Possible, State.Began)) {
            stopListenForOtherStateChanges();
            removeMessages();
            setState(State.Failed);
            setBeginFiringEvents(false);
            mStarted = false;
        }
    }

    /**
     * The minimum number of fingers that can be touching the view for this gesture to be recognized.
     * The default value is 1
     *
     * @since 1.0.0
     */
    public void setMinimumNumberOfTouches(int touches) {
        mMinimumNumberOfTouches = touches;
    }

    /**
     * @since 1.0.0
     */
    public int getMinimumNumberOfTouches() {
        return mMinimumNumberOfTouches;
    }

    /**
     * @param touches The maximum number of fingers that can be touching the view for this gesture to be recognized.
     * @since 1.0.0
     */
    public void setMaximumNumberOfTouches(final int touches) {
        this.mMaximumNumberOfTouches = touches;
    }

    /**
     * @since 1.0.0
     */
    public int getMaximumNumberOfTouches() {
        return mMaximumNumberOfTouches;
    }

    @SuppressWarnings ({"checkstyle:cyclomaticcomplexity", "checkstyle:innerassignment"})
    @Override
    protected boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);

        if (!isEnabled()) {
            return false;
        }

        final int action = ev.getAction();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        final boolean pointerUp =
            (action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP;
        final int skipIndex = pointerUp ? ev.getActionIndex() : -1;

        // Determine focal point
        float sumX = 0, sumY = 0;
        int count = ev.getPointerCount();
        for (int i = 0; i < count; i++) {
            if (skipIndex == i) {
                continue;
            }
            sumX += ev.getX(i);
            sumY += ev.getY(i);
        }
        final int div = pointerUp ? count - 1 : count;
        final float focusX = sumX / div;
        final float focusY = sumY / div;

        mCurrentLocation.x = focusX;
        mCurrentLocation.y = focusY;

        final float rawX = ev.getRawX();
        final float rawY = ev.getRawY();

        mTouches = count;

        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_POINTER_DOWN:
                mDownFocusX = mLastFocusX = focusX;
                mDownFocusY = mLastFocusY = focusY;

                if ((getState() == State.Possible)) {
                    if (count > mMaximumNumberOfTouches) {
                        setState(State.Failed);
                        removeMessages(MESSAGE_RESET);
                    }
                }

                break;

            case MotionEvent.ACTION_POINTER_UP:
                mDownFocusX = mLastFocusX = focusX;
                mDownFocusY = mLastFocusY = focusY;
                mTouches = count - 1;

                mVelocityTracker.computeCurrentVelocity(1000, 0);
                final int upIndex = ev.getActionIndex();

                final int id1 = ev.getPointerId(upIndex);
                final float x1 = mVelocityTracker.getXVelocity(id1);
                final float y1 = mVelocityTracker.getYVelocity(id1);
                for (int i = 0; i < count; i++) {
                    if (i == upIndex) {
                        continue;
                    }

                    final int id2 = ev.getPointerId(i);
                    final float x = x1 * mVelocityTracker.getXVelocity(id2);
                    final float y = y1 * mVelocityTracker.getYVelocity(id2);

                    final float dot = x + y;

                    if (dot < 0) {
                        mVelocityTracker.clear();
                        break;
                    }
                }

                if (getState() == State.Possible) {
                    if ((count - 1) < mMinimumNumberOfTouches) {
                        setState(State.Failed);
                        removeMessages(MESSAGE_RESET);
                    }
                }

                break;

            case MotionEvent.ACTION_DOWN:
                mDownFocusX = mLastFocusX = focusX;
                mDownFocusY = mLastFocusY = focusY;

                mVelocityTracker.clear();
                mVelocityTracker.addMovement(ev);

                mStarted = false;

                stopListenForOtherStateChanges();
                removeMessages(MESSAGE_RESET);

                if (!computeState(rawX, rawY)) {
                    setState(State.Failed);
                } else {
                    setState(State.Possible);
                }

                setBeginFiringEvents(false);

                break;

            case MotionEvent.ACTION_MOVE:
                scrollX = mLastFocusX - focusX;
                scrollY = mLastFocusY - focusY;

                mVelocityTracker.addMovement(ev);

                if (getState() == State.Possible && !mStarted) {
                    final int deltaX = (int) (focusX - mDownFocusX);
                    final int deltaY = (int) (focusY - mDownFocusY);
                    int distance = (deltaX * deltaX) + (deltaY * deltaY);
                    if (distance > mTouchSlopSquare) {

                        mVelocityTracker.computeCurrentVelocity(1000, Float.MAX_VALUE);
                        mVelocityY = mVelocityTracker.getYVelocity();
                        mVelocityX = mVelocityTracker.getXVelocity();

                        mTranslationX -= scrollX;
                        mTranslationY -= scrollY;

                        mLastFocusX = focusX;
                        mLastFocusY = focusY;
                        mStarted = true;

                        if ((count >= mMinimumNumberOfTouches
                            && count <= mMaximumNumberOfTouches)
                            && getDelegate().shouldBegin(this)
                            && getTouchDirection(mDownFocusX, mDownFocusY, focusX, focusY, mVelocityX, mVelocityY) == mEdge) {

                            setState(State.Began);

                            if (null == getRequireFailureOf()) {
                                fireActionEventIfCanRecognizeSimultaneously();
                            } else {
                                if (getRequireFailureOf().getState() == State.Failed) {
                                    fireActionEventIfCanRecognizeSimultaneously();
                                } else if (getRequireFailureOf().inState(State.Began, State.Ended, State.Changed)) {
                                    setState(State.Failed);
                                } else {
                                    listenForOtherStateChanges();
                                    setBeginFiringEvents(false);
                                    logMessage(Log.DEBUG, "waiting...");
                                }
                            }
                        } else {
                            setState(State.Failed);
                        }
                    }
                } else if (inState(State.Began, State.Changed)) {
                    //if ((Math.abs(scrollX) >= 1 || Math.abs(scrollY) >= 1)) {
                    mTranslationX -= scrollX;
                    mTranslationY -= scrollY;

                    final int pointerId = ev.getPointerId(0);
                    mVelocityTracker.computeCurrentVelocity(1000, Float.MAX_VALUE);
                    mVelocityY = mVelocityTracker.getYVelocity(pointerId);
                    mVelocityX = mVelocityTracker.getXVelocity(pointerId);

                    if (hasBeganFiringEvents()) {
                        setState(State.Changed);
                        fireActionEvent();
                    }

                    mLastFocusX = focusX;
                    mLastFocusY = focusY;
                }
                break;

            case MotionEvent.ACTION_UP:

                if (inState(State.Began, State.Changed)) {
                    final boolean began = hasBeganFiringEvents();
                    setState(State.Ended);
                    if (began) {
                        fireActionEvent();
                    }
                }

                if (getState() == State.Possible || !mStarted) {
                    mVelocityX = mVelocityY = 0;
                } else {
                    // TODO: verify this. it seems to send random values here
                    // VelocityTracker velocityTracker = mVelocityTracker;
                    // final int pointerId = ev.getPointerId(0);
                    // velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                    // mVelocityY = velocityTracker.getYVelocity(pointerId);
                    // mVelocityX = velocityTracker.getXVelocity(pointerId);
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }

                mHandler.sendEmptyMessage(MESSAGE_RESET);
                break;

            case MotionEvent.ACTION_CANCEL:
                removeMessages(MESSAGE_RESET);
                setState(State.Cancelled);
                setBeginFiringEvents(false);
                mHandler.sendEmptyMessage(MESSAGE_RESET);
                break;

            default:
                break;
        }

        return getCancelsTouchesInView();
    }

    private void fireActionEventIfCanRecognizeSimultaneously() {
        if (inState(State.Changed, State.Ended)) {
            setBeginFiringEvents(true);
            fireActionEvent();
        } else {
            if (getDelegate().shouldRecognizeSimultaneouslyWithGestureRecognizer(this)) {
                setBeginFiringEvents(true);
                fireActionEvent();
            }
        }
    }

    @Override
    protected boolean hasBeganFiringEvents() {
        return super.hasBeganFiringEvents() && inState(State.Began, State.Changed);
    }

    /**
     * @return The x velocity of the pan gesture
     * @since 1.0.0
     */
    public float getXVelocity() {
        return mVelocityX;
    }

    /**
     * @return The y velocity of the pan gesture
     * @since 1.0.0
     */
    public float getYVelocity() {
        return mVelocityY;
    }

    /**
     * @param mTranslationX Change the current translation x
     * @since 1.0.0
     */
    public void setTranslationX(final float mTranslationX) {
        this.mTranslationX = mTranslationX;
    }

    /**
     * @param mTranslationY Change the current translation y
     * @since 1.0.0
     */
    public void setTranslationY(final float mTranslationY) {
        this.mTranslationY = mTranslationY;
    }

    /**
     * @return The translation X of the pan gesture
     * @since 1.0.0
     */
    public float getTranslationX() {
        return mTranslationX;
    }

    /**
     * @return The translation Y of the pan gesture
     * @since 1.0.0
     */
    public float getTranslationY() {
        return mTranslationY;
    }

    /**
     * @return the relative scroll x between gestures
     * @since 1.0.0
     */
    public float getScrollX() {
        return -scrollX;
    }

    /**
     * @return the relative scroll y between gestures
     * @since 1.0.0
     */
    public float getScrollY() {
        return -scrollY;
    }

    @Override
    public float getCurrentLocationX() {
        return mCurrentLocation.x;
    }

    @Override
    public float getCurrentLocationY() {
        return mCurrentLocation.y;
    }

    @Override
    protected void removeMessages() {
        removeMessages(MESSAGE_RESET);
    }

    private UIRectEdge getTouchDirection(
        float x1, float y1, float x2, float y2, float velocityX, float velocityY) {
        float diffY = y2 - y1;
        float diffX = x2 - x1;

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > (float) 0) {
                if (diffX > 0) {
                    return UIRectEdge.LEFT;
                } else {
                    return UIRectEdge.RIGTH;
                }
            }
        } else if (Math.abs(diffY) > (float) 0) {
            if (diffY > 0) {
                return UIRectEdge.TOP;
            } else {
                return UIRectEdge.BOTTOM;
            }
        }
        return UIRectEdge.NONE;
    }

    private boolean computeState(float x, float y) {
        final Context context = getContext();
        if (null == context) {
            return false;
        }

        if (mEdge == UIRectEdge.LEFT && x > mEdgeLimit) {
            return false;
        } else if (mEdge == UIRectEdge.RIGTH) {
            final int w = context.getResources().getDisplayMetrics().widthPixels;
            return !(x < (w - mEdgeLimit));
        } else if (mEdge == UIRectEdge.TOP && y > mEdgeLimit) {
            return false;
        } else if (mEdge == UIRectEdge.BOTTOM) {
            final int h = context.getResources().getDisplayMetrics().heightPixels;
            return !(y < (h - mEdgeLimit));
        } else if (mEdge == UIRectEdge.NONE) {
            return false;
        }
        return true;
    }
}