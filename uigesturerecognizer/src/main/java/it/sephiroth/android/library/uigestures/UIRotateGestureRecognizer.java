package it.sephiroth.android.library.uigestures;

import android.content.Context;
import android.graphics.PointF;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;

/**
 * UIRotationGestureRecognizer is a subclass of UIGestureRecognizer that looks for rotation gestures involving two
 * touches. When the user moves the fingers opposite each other in a circular motion, the underlying view should rotate in a
 * corresponding direction and speed.
 *
 * @author alessandro crugnola
 * @see <a href='https://developer.apple.com/reference/uikit/uirotationgesturerecognizer'>
 * https://developer.apple.com/reference/uikit/uirotationgesturerecognizer</a>
 */

@SuppressWarnings ("unused")
public class UIRotateGestureRecognizer extends UIGestureRecognizer implements UIContinuousRecognizer {

    private static final int MESSAGE_RESET = 1;

    private static final int INVALID_POINTER_ID = -1;
    private static final double ROTATION_SLOP = 0.008;

    private double mRotationSlop;
    private float mAngle;
    private float x1, y1, x2, y2;
    private float mPreviousAngle;
    private float mVelocity;
    private boolean mValid;
    private boolean mStarted;
    private int mPtrID1;
    private int mPtrID2;
    private MotionEvent mPreviousEvent;
    private final PointF mCurrentLocation = new PointF();
    private int mTouches;

    @Override
    protected void handleMessage(final Message msg) {
        switch (msg.what) {
            case MESSAGE_RESET:
                stopListenForOtherStateChanges();
                setState(State.Possible);
                setBeginFiringEvents(false);
                break;
            default:
                break;
        }
    }

    public UIRotateGestureRecognizer(@Nullable final Context context) {
        super(context);
        mRotationSlop = ROTATION_SLOP;
        mPtrID1 = INVALID_POINTER_ID;
        mPtrID2 = INVALID_POINTER_ID;
        mVelocity = 0;
        mTouches = 0;
    }

    @Override
    public int getNumberOfTouches() {
        return mTouches;
    }

    /**
     * Change the minimum rotation threshold (in radians)
     *
     * @param value threshold in radians
     * @since 1.0.0
     */
    public void setRotationThreshold(final double value) {
        mRotationSlop = value;
    }

    /**
     * @return the rotation threshold
     * @since 1.0.0
     */
    public double getRotationThreshold() {
        return mRotationSlop;
    }

    @Override
    public void onStateChanged(@NonNull final UIGestureRecognizer recognizer) {
        logMessage(Log.VERBOSE, "onStateChanged(%s, %s)", recognizer, recognizer.getState());
        logMessage(Log.VERBOSE, "state: %s", getState());

        if (recognizer.getState() == State.Failed && getState() == State.Began) {
            stopListenForOtherStateChanges();
            fireActionEventIfCanRecognizeSimultaneously();

        } else if (recognizer.inState(State.Began, State.Ended) && inState(State.Began, State.Possible) && mStarted) {
            stopListenForOtherStateChanges();
            removeMessages();
            setState(State.Failed);
        }
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

    @SuppressWarnings ({"checkstyle:cyclomaticcomplexity", "checkstyle:innerassignment"})
    @Override
    protected boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);

        if (!isEnabled()) {
            return false;
        }

        final int action = ev.getAction();
        int count = ev.getPointerCount();

        final boolean pointerUp = action == MotionEvent.ACTION_POINTER_UP;
        final int skipIndex = pointerUp ? ev.getActionIndex() : -1;

        // Determine focal point
        float sumX = 0, sumY = 0;
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
        mTouches = pointerUp ? count - 1 : count;
        mCurrentLocation.set(focusX, focusY);

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mValid = false;
                mStarted = false;
                stopListenForOtherStateChanges();
                setState(State.Possible);
                setBeginFiringEvents(false);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (count == 2 && getState() != State.Failed) {
                    mPtrID1 = ev.getPointerId(0);
                    mPtrID2 = ev.getPointerId(1);

                    x1 = ev.getX(ev.findPointerIndex(mPtrID1));
                    y1 = ev.getY(ev.findPointerIndex(mPtrID1));
                    x2 = ev.getX(ev.findPointerIndex(mPtrID2));
                    y2 = ev.getY(ev.findPointerIndex(mPtrID2));
                    mValid = true;
                } else {
                    mValid = false;
                }

                mStarted = false;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                count -= 1;

                if (count == 2 && getState() != State.Failed) {

                    final int pointerIndex =
                        (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    final int pointerId = ev.getPointerId(pointerIndex);

                    boolean found = false;
                    for (int i = 0; i < ev.getPointerCount(); i++) {
                        final int id = ev.getPointerId(i);
                        if (id == pointerId) {
                            continue;
                        } else {
                            if (!found) {
                                mPtrID1 = id;
                            } else {
                                mPtrID2 = id;
                            }
                            found = true;
                        }
                    }

                    x1 = ev.getX(ev.findPointerIndex(mPtrID1));
                    y1 = ev.getY(ev.findPointerIndex(mPtrID1));
                    x2 = ev.getX(ev.findPointerIndex(mPtrID2));
                    y2 = ev.getY(ev.findPointerIndex(mPtrID2));
                    mValid = true;
                } else {
                    mValid = false;
                }
                mStarted = false;
                break;

            case MotionEvent.ACTION_UP:
                mValid = false;
                mStarted = false;
                mPreviousAngle = 0;
                mVelocity = 0;

                if (null != mPreviousEvent) {
                    mPreviousEvent.recycle();
                    mPreviousEvent = null;
                }

                if (inState(State.Began, State.Changed)) {
                    setState(State.Ended);
                    if (hasBeganFiringEvents()) {
                        fireActionEvent();
                    }
                    mHandler.sendEmptyMessage(MESSAGE_RESET);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mValid && getState() != State.Failed) {
                    final float nx1 = ev.getX(ev.findPointerIndex(mPtrID1));
                    final float ny1 = ev.getY(ev.findPointerIndex(mPtrID1));
                    final float nx2 = ev.getX(ev.findPointerIndex(mPtrID2));
                    final float ny2 = ev.getY(ev.findPointerIndex(mPtrID2));

                    mAngle = angleBetweenLines(x2, y2, x1, y1, nx2, ny2, nx1, ny1);

                    if (!mStarted) {
                        if (Math.abs(mAngle) > mRotationSlop) {
                            mStarted = true;

                            if (getDelegate().shouldBegin(this)) {
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
                    } else {
                        if (getState() == State.Began) {
                            if (hasBeganFiringEvents()) {
                                setState(State.Changed);
                                fireActionEvent();
                            }
                        } else if (getState() == State.Changed) {
                            setState(State.Changed);
                            fireActionEvent();
                        }
                    }

                    if (null != mPreviousEvent) {
                        final float diff = Math.max(mAngle, mPreviousAngle) - Math.min(mAngle, mPreviousAngle);
                        final long time = ev.getEventTime() - mPreviousEvent.getEventTime();
                        if (time > 0) {
                            mVelocity = (1000 / time) * diff;
                        } else {
                            mVelocity = 0;
                        }
                        mPreviousEvent.recycle();
                    }

                    x1 = ev.getX(ev.findPointerIndex(mPtrID1));
                    y1 = ev.getY(ev.findPointerIndex(mPtrID1));
                    x2 = ev.getX(ev.findPointerIndex(mPtrID2));
                    y2 = ev.getY(ev.findPointerIndex(mPtrID2));

                    mPreviousEvent = MotionEvent.obtain(ev);
                    mPreviousAngle = mAngle;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                mPtrID1 = INVALID_POINTER_ID;
                mPtrID2 = INVALID_POINTER_ID;

                mValid = false;
                mStarted = false;
                mPreviousAngle = 0;
                mVelocity = 0;

                if (null != mPreviousEvent) {
                    mPreviousEvent.recycle();
                    mPreviousEvent = null;
                }

                setState(State.Cancelled);
                setBeginFiringEvents(false);
                mHandler.sendEmptyMessage(MESSAGE_RESET);

                break;

            default:
                break;
        }

        return getCancelsTouchesInView();
    }

    /**
     * Returns the rotation in radians
     *
     * @return the current rotation in radians
     * @see #getRotationInDegrees() for angle in degrees
     * @since 1.0.0
     */
    public float getRotationInRadians() {
        return mAngle;
    }

    /**
     * Returns the rotation in degrees
     *
     * @return the current rotation in degrees
     * @see #getRotationInRadians() for angle in radians
     * @since 1.0.0
     */
    @SuppressWarnings ("checkstyle:magicnumber")
    public float getRotationInDegrees() {
        float angle = ((float) Math.toDegrees(mAngle)) % 360;
        if (angle < -180.f) {
            angle += 360.0f;
        }
        if (angle > 180.f) {
            angle -= 360.0f;
        }
        return angle;
    }

    /**
     * @return The velocity of the rotation gesture in radians per second.
     * @since 1.0.0
     */
    public float getVelocity() {
        return mVelocity;
    }

    private float angleBetweenLines(float fX, float fY, float sX, float sY, float nfX, float nfY, float nsX, float nsY) {
        float angle1 = (float) Math.atan2((fY - sY), (fX - sX));
        float angle2 = (float) Math.atan2((nfY - nsY), (nfX - nsX));
        return angle1 - angle2;
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
}
