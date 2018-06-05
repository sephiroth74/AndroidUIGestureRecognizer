package it.sephiroth.android.library.uigestures;

import android.content.Context;
import android.os.Build;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.MotionEvent;

/**
 * UIPinchGestureRecognizer is a subclass of UIGestureRecognizer that looks for pinching gestures involving two touches.
 * When the user moves the two fingers toward each other, the conventional meaning is zoom-out; when the user moves the two
 * fingers away from each other, the conventional meaning is zoom-in.
 *
 * @author alessandro crugnola
 * @see <a href='https://developer.apple.com/reference/uikit/uipinchgesturerecognizer'>
 * https://developer.apple.com/reference/uikit/uipinchgesturerecognizer</a>
 */

@SuppressWarnings ("unused")
public class UIPinchGestureRecognizer extends UIGestureRecognizer
    implements UIContinuousRecognizer, ScaleGestureDetector.OnScaleGestureListener {

    private static final int MESSAGE_RESET = 1;
    private final ScaleGestureDetector mScaleGestureDetector;
    private float mTotalScale;

    @Override
    protected void handleMessage(final Message msg) {
        switch (msg.what) {
            case MESSAGE_RESET:
                setState(State.Possible);
                break;
            default:
                break;
        }
    }

    public UIPinchGestureRecognizer(@Nullable final Context context) {
        super(context);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        setQuickScaleEnabled(false);
    }

    /**
     * Set whether the associated {@link ScaleGestureDetector.OnScaleGestureListener} should receive onScale callbacks
     * when the user performs a doubleTap followed by a swipe. Note that this is enabled by default
     * if the app targets API 19 and newer.<br/>
     * Default value is false.
     *
     * @param enabled true to enable quick scaling, false to disable
     * @since 1.0.0
     */
    public void setQuickScaleEnabled(boolean enabled) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mScaleGestureDetector.setQuickScaleEnabled(enabled);
        }
    }

    /**
     * @param enabled enable/disable stylus scaling. Note: it is only available for android 23 and above
     * @since 1.0.0
     */
    @RequiresApi (api = Build.VERSION_CODES.M)
    public void setStylusScaleEnabled(boolean enabled) {
        mScaleGestureDetector.setStylusScaleEnabled(enabled);
    }

    @Override
    protected boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);

        if (isEnabled()) {
            mScaleGestureDetector.onTouchEvent(ev);
            return getCancelsTouchesInView();
        }
        return false;
    }

    @Override
    public int getNumberOfTouches() {
        return mScaleGestureDetector.getNumberOfTouches();
    }

    /**
     * @return The total scale factor since the gesture began
     */
    public float getScale() {
        return mTotalScale;
    }

    /**
     * Returns the current scale factor
     *
     * @return
     * @since 1.0.0
     */
    public float getScaleFactor() {
        return mScaleGestureDetector.getScaleFactor();
    }

    @Override
    public void onStateChanged(@NonNull final UIGestureRecognizer recognizer) {
        logMessage(Log.VERBOSE, "onStateChanged(%s, %s)", recognizer, recognizer.getState());
        logMessage(Log.VERBOSE, "state: %s", getState());

        if (recognizer.getState() == State.Failed && getState() == State.Began) {
            stopListenForOtherStateChanges();
            fireActionEventIfCanRecognizeSimultaneously();

        } else if (recognizer.inState(State.Began, State.Ended) && inState(State.Possible, State.Began)) {
            stopListenForOtherStateChanges();
            removeMessages();
            setState(State.Failed);
        }
    }

    @Override
    public boolean onScale(final ScaleGestureDetector scaleGestureDetector) {
        if (isEnabled() && inState(State.Began, State.Changed)) {
            mTotalScale += scaleGestureDetector.getScaleFactor() - 1;
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
        return true;
    }

    @Override
    public boolean onScaleBegin(final ScaleGestureDetector scaleGestureDetector) {
        setState(State.Possible);

        if (isEnabled() && getState() == State.Possible) {
            mTotalScale = scaleGestureDetector.getScaleFactor();
            removeMessages(MESSAGE_RESET);

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
            return true;
        }
        return true;
    }

    @Override
    public void onScaleEnd(final ScaleGestureDetector scaleGestureDetector) {
        if (inState(State.Began, State.Changed)) {
            final boolean began = hasBeganFiringEvents();
            setState(State.Ended);
            if (began) {
                fireActionEvent();
            }
            mHandler.sendEmptyMessage(MESSAGE_RESET);
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

    @Override
    public float getCurrentLocationX() {
        return mScaleGestureDetector.getFocusX();
    }

    @Override
    public float getCurrentLocationY() {
        return mScaleGestureDetector.getFocusY();
    }

    /**
     * @see ScaleGestureDetector#getCurrentSpan()
     * @since 1.0.0
     */
    public float getCurrentSpan() {
        return mScaleGestureDetector.getCurrentSpan();
    }

    /**
     * @see ScaleGestureDetector#getCurrentSpanX()
     * @since 1.0.0
     */
    public float getCurrentSpanX() {
        return mScaleGestureDetector.getCurrentSpanX();
    }

    /**
     * @see ScaleGestureDetector#getCurrentSpanY()
     * @since 1.0.0
     */
    public float getCurrentSpanY() {
        return mScaleGestureDetector.getCurrentSpanY();
    }

    /**
     * @see ScaleGestureDetector#getPreviousSpan()
     * @since 1.0.0
     */
    public float getPreviousSpan() {
        return mScaleGestureDetector.getPreviousSpan();
    }

    /**
     * @see ScaleGestureDetector#getPreviousSpanX()
     * @since 1.0.0
     */
    public float getPreviousSpanX() {
        return mScaleGestureDetector.getPreviousSpanX();
    }

    /**
     * @see ScaleGestureDetector#getPreviousSpanY()
     * @since 1.0.0
     */
    public float getPreviousSpanY() {
        return mScaleGestureDetector.getPreviousSpanY();
    }

    /**
     * @see ScaleGestureDetector#getTimeDelta()
     * @since 1.0.0
     */
    public long getTimeDelta() {
        return mScaleGestureDetector.getTimeDelta();
    }

    @Override
    protected void removeMessages() {
        removeMessages(MESSAGE_RESET);
    }
}
