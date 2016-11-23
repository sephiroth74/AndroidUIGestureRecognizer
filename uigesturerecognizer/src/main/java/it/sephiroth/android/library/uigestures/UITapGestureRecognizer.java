package it.sephiroth.android.library.uigestures;

import android.content.Context;
import android.graphics.PointF;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/**
 * UITapGestureRecognizer looks for single or multiple taps.
 * For the gesture to be recognized, the specified number of fingers must tap the view a specified number of times.
 *
 * @author alessandro crugnola
 * @see <a href='https://developer.apple.com/reference/uikit/uitapgesturerecognizer'>
 * https://developer.apple.com/reference/uikit/uitapgesturerecognizer</a>
 */
@SuppressWarnings ("unused")
public final class UITapGestureRecognizer extends UIGestureRecognizer implements UIDiscreteGestureRecognizer {

    // request to change the current state to Failed
    private static final int MESSAGE_FAILED = 1;
    // request to change the current state to Possible
    private static final int MESSAGE_RESET = 2;
    // we handle the action_pointer_up received in the onTouchEvent with a delay
    // in order to check how many fingers were actually down when we're checking them
    // in the action_up.
    private static final int MESSAGE_POINTER_UP = 3;
    // a long press will make this gesture to fail
    private static final int MESSAGE_LONG_PRESS = 4;

    private static final long LONG_PRESS_TIMEOUT = 1500;

    private static final String TAG = UITapGestureRecognizer.class.getSimpleName();

    private final int mDoubleTapTouchSlopSquare;

    // number of required fingers (default is 1)
    private int mTouchesRequired = 1;
    // number of required taps (default is 1)
    private int mTapsRequired = 1;

    private boolean mAlwaysInTapRegion;
    private float mDownFocusX;
    private float mDownFocusY;
    private int mTouchSlopSquare;
    private boolean mStarted;

    private int mNumTaps = 0;
    private int mNumTouches = 0;
    private final PointF mCurrentLocation;

    /**
     * UITapGestureRecognizer looks for single or multiple taps. For the
     * gesture to be recognized, the specified number of fingers must tap the view a specified number of times.
     */
    public UITapGestureRecognizer(@Nullable final Context context) {
        super(context);

        mStarted = false;
        mCurrentLocation = new PointF();

        int touchSlop, doubleTapTouchSlop;

        if (context == null) {
            //noinspection deprecation
            touchSlop = ViewConfiguration.getTouchSlop();
            doubleTapTouchSlop = touchSlop;
        } else {
            final ViewConfiguration configuration = ViewConfiguration.get(context);
            touchSlop = configuration.getScaledTouchSlop();
            doubleTapTouchSlop = DOUBLE_TAP_TOUCH_SLOP;
        }

        mTouchSlopSquare = touchSlop * touchSlop;
        mDoubleTapTouchSlopSquare = doubleTapTouchSlop * doubleTapTouchSlop;
    }

    @Override
    protected void handleMessage(final Message msg) {
        switch (msg.what) {
            case MESSAGE_RESET:
                logMessage(Log.INFO, "handleMessage(MESSAGE_RESET)");
                handleReset();
                break;

            case MESSAGE_FAILED:
                logMessage(Log.INFO, "handleMessage(MESSAGE_FAILED)");
                handleFailed();
                break;

            case MESSAGE_POINTER_UP:
                logMessage(Log.INFO, "handleMessage(MESSAGE_POINTER_UP)");
                mNumTouches = msg.arg1;
                break;

            case MESSAGE_LONG_PRESS:
                logMessage(Log.INFO, "handleMessage(MESSAGE_LONG_PRESS)");
                handleFailed();
                break;

            default:
                break;

        }
    }

    /**
     * Change the number of required taps for this recognizer to succeed.<br />
     * Default value is 1
     *
     * @since 1.0.0
     */
    public void setNumberOfTapsRequired(int value) {
        mTapsRequired = value;
    }

    /**
     * Change the number of required touches for this recognizer to succeed.<br />
     * Default value is 1
     *
     * @since 1.0.0
     */
    public void setNumberOfTouchesRequired(int value) {
        mTouchesRequired = value;
    }

    @Override
    public void onStateChanged(@NonNull final UIGestureRecognizer recognizer) {
        logMessage(Log.VERBOSE, "onStateChanged(%s): %s", recognizer, recognizer.getState());
        logMessage(Log.VERBOSE, "this.state: %s", getState());
        logMessage(Log.VERBOSE, "mStarted: %s", mStarted);

        if (recognizer.getState() == State.Failed && getState() == State.Ended) {
            stopListenForOtherStateChanges();
            fireActionEvent();
            postReset();
        } else if (recognizer.inState(State.Began, State.Ended) && mStarted && inState(State.Possible, State.Ended)) {
            stopListenForOtherStateChanges();
            removeMessages();
            setState(State.Failed);
            mStarted = false;
        }
    }

    @Override
    public int getNumberOfTouches() {
        return mNumTouches;
    }

    @SuppressWarnings ({"checkstyle:cyclomaticcomplexity", "checkstyle:innerassignment"})
    @Override
    protected boolean onTouchEvent(final MotionEvent ev) {
        if (!isEnabled()) {
            return false;
        }

        final int action = ev.getAction();
        final int count = ev.getPointerCount();

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
        mCurrentLocation.set(focusX, focusY);

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                removeMessages(MESSAGE_FAILED, MESSAGE_RESET, MESSAGE_POINTER_UP);
                mAlwaysInTapRegion = true;
                mNumTouches = count;

                if (!mStarted) {
                    stopListenForOtherStateChanges();
                    setState(State.Possible);
                    mNumTaps = 0;
                    mStarted = true;
                }

                mHandler.sendEmptyMessageDelayed(MESSAGE_LONG_PRESS, LONG_PRESS_TIMEOUT);

                mNumTaps++;
                mDownFocusX = focusX;
                mDownFocusY = focusY;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (getState() == State.Possible && mStarted) {
                    removeMessages(MESSAGE_POINTER_UP);

                    mNumTouches = count;

                    if (mNumTouches > 1) {
                        if (mNumTouches > mTouchesRequired) {
                            setState(State.Failed);
                        }
                    }
                    mDownFocusX = focusX;
                    mDownFocusY = focusY;
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (getState() == State.Possible && mStarted) {
                    removeMessages(MESSAGE_FAILED, MESSAGE_RESET, MESSAGE_POINTER_UP);

                    mDownFocusX = focusX;
                    mDownFocusY = focusY;

                    Message message = mHandler.obtainMessage(MESSAGE_POINTER_UP);
                    message.arg1 = mNumTouches - 1;
                    mHandler.sendMessageDelayed(message, TAP_TIMEOUT);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (getState() == State.Possible && mStarted) {
                    if (mAlwaysInTapRegion) {
                        final int deltaX = (int) (focusX - mDownFocusX);
                        final int deltaY = (int) (focusY - mDownFocusY);
                        int distance = (deltaX * deltaX) + (deltaY * deltaY);

                        final int slop = mTapsRequired > 1 ? mDoubleTapTouchSlopSquare : mTouchSlopSquare;

                        if (distance > slop) {
                            logMessage(Log.WARN, "moved too much!");
                            mAlwaysInTapRegion = false;

                            removeMessages();
                            setState(State.Failed);
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                removeMessages(MESSAGE_RESET, MESSAGE_POINTER_UP, MESSAGE_LONG_PRESS);

                if (getState() == State.Possible && mStarted) {
                    if (mNumTouches != mTouchesRequired) {
                        mStarted = false;
                        removeMessages();
                        setState(State.Failed);
                    } else {
                        if (mNumTaps < mTapsRequired) {
                            delayedFail();
                        } else {
                            // nailed!
                            if (getDelegate().shouldBegin(this)) {
                                setState(State.Ended);

                                if (null == getRequireFailureOf()) {
                                    fireActionEvent();
                                    postReset();
                                } else {
                                    if (getRequireFailureOf().getState() == State.Failed) {
                                        fireActionEvent();
                                        postReset();
                                    } else if (getRequireFailureOf().inState(State.Began, State.Ended, State.Changed)) {
                                        setState(State.Failed);
                                        postReset();
                                    } else {
                                        listenForOtherStateChanges();
                                        logMessage(Log.DEBUG, "waiting...");
                                    }
                                }
                            } else {
                                setState(State.Failed);
                                postReset();
                            }
                            mStarted = false;
                        }
                    }
                } else {
                    mStarted = false;
                    setState(State.Possible);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                removeMessages();
                mStarted = false;
                setState(State.Cancelled);
                postReset();
                break;

            default:
                break;
        }
        return getCancelsTouchesInView();
    }

    @Override
    protected void removeMessages() {
        removeMessages(MESSAGE_FAILED, MESSAGE_RESET, MESSAGE_POINTER_UP, MESSAGE_LONG_PRESS);
    }

    private void postReset() {
        mHandler.sendEmptyMessage(MESSAGE_RESET);
    }

    private void delayedFail() {
        mHandler.sendEmptyMessageDelayed(MESSAGE_FAILED, DOUBLE_TAP_TIMEOUT);
    }

    private void handleFailed() {
        setState(State.Failed);
        removeMessages();
        postReset();
        mStarted = false;
    }

    private void handleReset() {
        setState(State.Possible);
        mStarted = false;
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
