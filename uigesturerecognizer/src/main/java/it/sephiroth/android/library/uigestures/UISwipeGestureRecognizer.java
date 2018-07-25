package it.sephiroth.android.library.uigestures;

import android.content.Context;
import android.graphics.PointF;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

/**
 * UISwipeGestureRecognizer is a subclass of UIGestureRecognizer that looks for swiping gestures in one or more
 * directions. A swipe is a discrete gesture, and thus the associated action message is sent only once per gesture.
 *
 * @author alessandro crugnola
 * @see <a href='https://developer.apple.com/reference/uikit/uiswipegesturerecognizer'>
 * https://developer.apple.com/reference/uikit/uiswipegesturerecognizer</a>
 */

@SuppressWarnings ("unused")
public class UISwipeGestureRecognizer extends UIGestureRecognizer implements UIDiscreteGestureRecognizer {
    private static final int MESSAGE_RESET = 4;

    public static final int RIGHT = 1 << 1;
    public static final int LEFT = 1 << 2;
    public static final int UP = 1 << 3;
    public static final int DOWN = 1 << 4;

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    public static final int MAXIMUM_TOUCH_SLOP_TIME = 150;
    public static final int MAXIMUM_TOUCH_FLING_TIME = 300;

    private int mTouchSlopSquare;
    private int mMaximumFlingVelocity;

    private boolean mStarted;
    private int mDirection;
    private int mNumberOfTouchesRequired;

    private float mLastFocusX;
    private float mLastFocusY;
    private float mDownFocusX;
    private float mDownFocusY;

    private VelocityTracker mVelocityTracker;
    private float scrollX, scrollY;
    private float mTranslationX;
    private float mTranslationY;
    private float mVelocityY, mVelocityX;
    private final PointF mCurrentLocation;
    private long mDownTime;
    private int mTouches;
    private boolean mDown;

    public UISwipeGestureRecognizer(@NonNull final Context context) {
        super(context);
        mNumberOfTouchesRequired = 1;
        mDirection = RIGHT;
        mStarted = false;
        mTouches = 0;

        int touchSlop;
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = configuration.getScaledTouchSlop();
        mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
        mTouchSlopSquare = touchSlop * touchSlop;
        mCurrentLocation = new PointF();
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
    protected void removeMessages() {
        removeMessages(MESSAGE_RESET);
    }

    @Override
    public int getNumberOfTouches() {
        return mTouches;
    }

    @Override
    public void onStateChanged(@NonNull final UIGestureRecognizer recognizer) {
        logMessage(Log.VERBOSE, "onStateChanged(%s, %s)", recognizer, recognizer.getState());
        logMessage(Log.VERBOSE, "started: %b, state: %s", mStarted, getState());

        if (recognizer.getState() == State.Failed && getState() == State.Ended) {
            removeMessages();
            stopListenForOtherStateChanges();
            fireActionEventIfCanRecognizeSimultaneously();

            if (!mDown) {
                mStarted = false;
                setState(State.Possible);
            }

        } else if (recognizer.inState(State.Began, State.Ended) && mStarted && inState(State.Possible, State.Ended)) {
            mStarted = false;
            setBeginFiringEvents(false);
            stopListenForOtherStateChanges();
            removeMessages();
            setState(State.Failed);
        }
    }

    private void fireActionEventIfCanRecognizeSimultaneously() {
        if (getDelegate().shouldRecognizeSimultaneouslyWithGestureRecognizer(this)) {
            setBeginFiringEvents(true);
            fireActionEvent();
        }
    }

    /**
     * @param direction
     * @since 1.0.0
     */
    public void setDirection(final int direction) {
        this.mDirection = direction;
    }

    /**
     * @return
     * @since 1.0.0
     */
    public int getDirection() {
        return mDirection;
    }

    /**
     * @param value
     * @since 1.0.0
     */
    public void setNumberOfTouchesRequired(final int value) {
        this.mNumberOfTouchesRequired = value;
    }

    /**
     * @return
     * @since 1.0.0
     */
    public int getNumberOfTouchesRequired() {
        return mNumberOfTouchesRequired;
    }

    @SuppressWarnings ({"checkstyle:cyclomaticcomplexity", "checkstyle:innerassignment"})
    @Override
    public boolean onTouchEvent(final MotionEvent ev) {
        super.onTouchEvent(ev);

        if (!isEnabled()) {
            return false;
        }

        final int action = ev.getAction();

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(ev);

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

        mTouches = pointerUp ? count - 1 : count;

        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_POINTER_DOWN:
                mLastFocusX = focusX;
                mLastFocusY = focusY;

                if ((getState() == State.Possible) && !mStarted) {
                    if (count > mNumberOfTouchesRequired) {
                        setState(State.Failed);
                        removeMessages(MESSAGE_RESET);
                    }
                }

                break;

            case MotionEvent.ACTION_POINTER_UP:
                mLastFocusX = focusX;
                mLastFocusY = focusY;

                mVelocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
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

                if (getState() == State.Possible && !mStarted) {
                    if ((count - 1) < mNumberOfTouchesRequired) {
                        setState(State.Failed);
                        removeMessages(MESSAGE_RESET);
                    }
                }

                break;

            case MotionEvent.ACTION_DOWN:
                mStarted = false;
                mDown = true;

                mDownFocusX = mLastFocusX = focusX;
                mDownFocusY = mLastFocusY = focusY;
                mDownTime = ev.getEventTime();

                mVelocityTracker.clear();

                setBeginFiringEvents(false);
                removeMessages(MESSAGE_RESET);
                setState(State.Possible);
                break;

            case MotionEvent.ACTION_MOVE:
                scrollX = mLastFocusX - focusX;
                scrollY = mLastFocusY - focusY;

                if (getState() == State.Possible) {
                    final int deltaX = (int) (focusX - mDownFocusX);
                    final int deltaY = (int) (focusY - mDownFocusY);
                    int distance = (deltaX * deltaX) + (deltaY * deltaY);
                    if (!mStarted) {
                        if (distance > mTouchSlopSquare) {
                            mVelocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                            mVelocityY = mVelocityTracker.getYVelocity();
                            mVelocityX = mVelocityTracker.getXVelocity();

                            mTranslationX -= scrollX;
                            mTranslationY -= scrollY;

                            mLastFocusX = focusX;
                            mLastFocusY = focusY;
                            mStarted = true;

                            if (count == mNumberOfTouchesRequired) {
                                final long time = ev.getEventTime() - ev.getDownTime();
                                if (time > MAXIMUM_TOUCH_SLOP_TIME) {
                                    logMessage(Log.WARN, "passed too much time");
                                    mStarted = false;
                                    setBeginFiringEvents(false);
                                    setState(State.Failed);
                                } else {
                                    int direction =
                                        getTouchDirection(mDownFocusX, mDownFocusY, focusX, focusY, mVelocityX, mVelocityY, 0);
                                    logMessage(
                                        Log.VERBOSE,
                                        "time: " + (ev.getEventTime() - mDownTime) + " or " + (ev.getEventTime() - ev.getDownTime())
                                    );
                                    logMessage(Log.VERBOSE, "direction: " + direction);

                                    if (direction == -1 || (mDirection & direction) == 0) {
                                        mStarted = false;
                                        setBeginFiringEvents(false);
                                        setState(State.Failed);
                                    } else {
                                        logMessage(Log.DEBUG, "direction accepted: " + (mDirection & direction));
                                        mStarted = true;
                                    }
                                }
                            } else {
                                mStarted = false;
                                setBeginFiringEvents(false);
                                setState(State.Failed);
                            }
                        }
                    } else {
                        // touch has been recognized. now let's track the movement
                        mVelocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                        mVelocityY = mVelocityTracker.getYVelocity();
                        mVelocityX = mVelocityTracker.getXVelocity();
                        final long time = ev.getEventTime() - ev.getDownTime();

                        if (time > MAXIMUM_TOUCH_FLING_TIME) {
                            mStarted = false;
                            setState(State.Failed);
                        } else {
                            int direction =
                                getTouchDirection(
                                    mDownFocusX, mDownFocusY, focusX, focusY, mVelocityX, mVelocityY, SWIPE_THRESHOLD);

                            if (direction != -1) {
                                if ((mDirection & direction) != 0) {
                                    if (getDelegate().shouldBegin(this)) {
                                        setState(State.Ended);
                                        if (null == getRequireFailureOf()) {
                                            fireActionEventIfCanRecognizeSimultaneously();
                                        } else {
                                            if (getRequireFailureOf().getState() == State.Failed) {
                                                fireActionEventIfCanRecognizeSimultaneously();
                                            } else if (getRequireFailureOf().inState(State.Began, State.Ended, State.Changed)) {
                                                mStarted = false;
                                                setBeginFiringEvents(false);
                                                setState(State.Failed);
                                            } else {
                                                logMessage(Log.DEBUG, "waiting...");
                                                listenForOtherStateChanges();
                                                setBeginFiringEvents(false);
                                            }
                                        }
                                    } else {
                                        setState(State.Failed);
                                        mStarted = false;
                                        setBeginFiringEvents(false);
                                    }
                                } else {
                                    mStarted = false;
                                    setBeginFiringEvents(false);
                                    setState(State.Failed);
                                }
                            }
                        }

                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }

                // TODO: should we fail if the gesture didn't actually start?

                mDown = false;
                removeMessages(MESSAGE_RESET);
                break;

            case MotionEvent.ACTION_CANCEL:
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }

                mDown = false;
                removeMessages(MESSAGE_RESET);
                setState(State.Cancelled);
                getMHandler().sendEmptyMessage(MESSAGE_RESET);
                break;

            default:
                break;
        }

        return getCancelsTouchesInView();
    }

    private int getTouchDirection(
        float x1, float y1, float x2, float y2, float velocityX, float velocityY, final float distanceThreshold) {
        float diffY = y2 - y1;
        float diffX = x2 - x1;
        logMessage(Log.VERBOSE, "diff: %gx%g", diffX, diffY);
        logMessage(Log.VERBOSE, "velocity: %gx%g", velocityX, velocityY);

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > distanceThreshold && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    return RIGHT;
                } else {
                    return LEFT;
                }
            }
        } else if (Math.abs(diffY) > distanceThreshold && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
            if (diffY > 0) {
                return DOWN;
            } else {
                return UP;
            }
        }
        return -1;
    }

    /**
     * @return
     * @since 1.0.0
     */
    public float getXVelocity() {
        return mVelocityX;
    }

    /**
     * @return
     * @since 1.0.0
     */
    public float getYVelocity() {
        return mVelocityY;
    }

    /**
     * @param mTranslationX
     * @since 1.0.0
     */
    public void setTranslationX(final float mTranslationX) {
        this.mTranslationX = mTranslationX;
    }

    /**
     * @param mTranslationY
     * @since 1.0.0
     */
    public void setTranslationY(final float mTranslationY) {
        this.mTranslationY = mTranslationY;
    }

    /**
     * @return
     * @since 1.0.0
     */
    public float getTranslationX() {
        return mTranslationX;
    }

    /**
     * @return
     * @since 1.0.0
     */
    public float getTranslationY() {
        return mTranslationY;
    }

    /**
     * @return
     * @since 1.0.0
     */
    public float getScrollX() {
        return -scrollX;
    }

    /**
     * @return
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
}