package it.sephiroth.android.library.uigestures;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.simplelogger.LoggerFactory;

/**
 * Created by <b>Alessandro Crugnola</b>
 * <p>AndroidGestureRecognizer is an Android implementation
 * of the Apple's UIGestureRecognizer framework. There's not guarantee, however, that
 * this library works 100% in the same way as the Apple version.</p>
 *
 * @author sephiroth
 * @version 1.0.0
 * @see <a href='https://developer.apple.com/reference/uikit/uigesturerecognizer'>
 * https://developer.apple.com/reference/uikit/uigesturerecognizer</a>
 */

@SuppressWarnings ("unused")
public abstract class UIGestureRecognizer implements OnGestureRecognizerStateChangeListener {

    public enum State {
        Possible,
        Began,
        Changed,
        Failed,
        Cancelled,
        Ended
    }

    public interface OnActionListener {
        void onGestureRecognized(@NonNull final UIGestureRecognizer recognizer);
    }

    public static final String VERSION = BuildConfig.VERSION_NAME;
    private static int sId = 0;
    private static boolean sDebug = false;

    static final long LONG_PRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
    static final long TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
    static final long DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
    static final int TOUCH_SLOP = 8;
    static final int DOUBLE_TAP_SLOP = 100;
    static final int DOUBLE_TAP_TOUCH_SLOP = TOUCH_SLOP;

    private final List<OnGestureRecognizerStateChangeListener> mStateListeners = new ArrayList<>();
    private OnActionListener mListener;
    private State mState;
    private boolean mEnabled;
    private boolean mCancelsTouchesInView;
    private UIGestureRecognizerDelegate mDelegate;
    private Object mTag;
    private long mId;
    private UIGestureRecognizer mOtherRecognizer;
    private final LoggerFactory.Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    protected final GestureHandler mHandler;

    public UIGestureRecognizer(@Nullable Context context) {
        mHandler = new GestureHandler();
        mCancelsTouchesInView = true;
        mEnabled = true;
        mId = generateId();
    }

    private long generateId() {
        return sId++;
    }

    @SuppressLint ("HandlerLeak")
    protected final class GestureHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {
            UIGestureRecognizer.this.handleMessage(msg);
        }
    }

    protected abstract void removeMessages();

    protected final void removeMessages(int... messages) {
        for (int message : messages) {
            mHandler.removeMessages(message);
        }
    }

    protected final boolean hasMessages(int... messages) {
        for (int message : messages) {
            if (mHandler.hasMessages(message)) {
                return true;
            }
        }
        return false;
    }

    protected final void setDelegate(final UIGestureRecognizerDelegate delegate) {
        mDelegate = delegate;
    }

    protected final UIGestureRecognizerDelegate getDelegate() {
        return mDelegate;
    }

    protected final void fireActionEvent() {
        if (null != mListener) {
            mListener.onGestureRecognized(this);
        }
    }

    protected void addOnStateChangeListenerListener(final OnGestureRecognizerStateChangeListener listener) {
        if (!mStateListeners.contains(listener)) {
            mStateListeners.add(listener);
        }
    }

    protected boolean removeOnStateChangeListenerListener(final OnGestureRecognizerStateChangeListener listener) {
        return mStateListeners.remove(listener);
    }

    protected abstract boolean onTouchEvent(MotionEvent event);

    protected abstract void handleMessage(final Message msg);

    /**
     * @param mTag custom object the instance should keep
     * @since 1.0.0
     */
    public void setTag(final Object mTag) {
        this.mTag = mTag;

        if (sDebug) {
            logger.setTag(String.valueOf(mTag));
        }
    }

    /**
     * @return current tag assigned to this instance
     * @since 1.0.0
     */
    public Object getTag() {
        return mTag;
    }

    /**
     * @param mId change the instance id
     */
    public void setId(final long mId) {
        this.mId = mId;
    }

    /**
     * @return the instance id
     * @since 1.0.0
     */
    public static int getId() {
        return sId;
    }

    public void setActionListener(final OnActionListener listener) {
        this.mListener = listener;
    }

    /**
     * @return Returns the number of touches involved in the gesture represented by the receiver.
     * @since 1.0.0
     */
    public abstract int getNumberOfTouches();

    /**
     * @return Returns the X computed as the location in a given view of the gesture represented by the receiver.
     * @since 1.0.0
     */
    public abstract float getCurrentLocationX();

    /**
     * @return Returns the Y computed as the location in a given view of the gesture represented by the receiver.
     * @since 1.0.0
     */
    public abstract float getCurrentLocationY();

    /**
     * @return The current recognizer internal state
     * @since 1.0.0
     */
    public final State getState() {
        return mState;
    }

    protected final void setState(State state) {
        logMessage(Log.INFO, "setState: %s", state);

        final boolean changed = mState != state || state == State.Changed;
        mState = state;

        if (changed) {
            for (OnGestureRecognizerStateChangeListener listener : mStateListeners) {
                listener.onStateChanged(this);
            }
        }
    }

    protected boolean inState(State... states) {
        if (null == states || states.length < 1) {
            return false;
        }
        for (State state : states) {
            if (mState == state) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return True if the recognizer is enabled
     * @since 1.0.0
     */
    public final boolean isEnabled() {
        return mEnabled;
    }

    /**
     * Toggle the recognizer enabled state.
     *
     * @param enabled Set to false to prevent any motion event
     *                to be intercepted by this recognizer
     * @since 1.0.0
     */
    public final void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    /**
     * @param other Creates a dependency relationship between the receiver and another gesture recognizer when the objects
     *              are created
     * @see <a href='https://developer.apple.com/reference/uikit/uigesturerecognizer/1624203-require'>
     * https://developer.apple.com/reference/uikit/uigesturerecognizer/1624203-require</a>
     * @since 1.0.0
     */
    public final void requireFailureOf(@Nullable final UIGestureRecognizer other) {
        if (null != mOtherRecognizer) {
            mOtherRecognizer.removeOnStateChangeListenerListener(this);
        }
        this.mOtherRecognizer = other;
    }

    protected final UIGestureRecognizer getRequireFailureOf() {
        return mOtherRecognizer;
    }

    protected final void stopListenForOtherStateChanges() {
        if (null != getRequireFailureOf()) {
            getRequireFailureOf().removeOnStateChangeListenerListener(this);
        }
    }

    protected final void listenForOtherStateChanges() {
        if (null != getRequireFailureOf()) {
            getRequireFailureOf().addOnStateChangeListenerListener(this);
        }
    }

    /**
     * @param value A Boolean value affecting whether touches are delivered to a view when a gesture is recognized
     * @see <a href='https://developer.apple.com/reference/uikit/uigesturerecognizer/1624218-cancelstouchesinview'>
     * https://developer.apple.com/reference/uikit/uigesturerecognizer/1624218-cancelstouchesinview</a>
     * @since 1.0.0
     */
    public final void setCancelsTouchesInView(boolean value) {
        mCancelsTouchesInView = value;
    }

    /**
     * @see UIGestureRecognizer#setCancelsTouchesInView(boolean)
     * @since 1.0.0
     */
    public boolean getCancelsTouchesInView() {
        return mCancelsTouchesInView;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[state: " + getState() + ", tag:" + getTag() + "]";
    }

    public static void setLogEnabled(boolean enabled) {
        sDebug = enabled;
    }

    void logMessage(int level, String fmt, Object... args) {
        if (!sDebug) {
            return;
        }

        switch (level) {
            case Log.INFO:
                logger.info(fmt, args);
                break;
            case Log.DEBUG:
                logger.debug(fmt, args);
                break;
            case Log.ASSERT:
            case Log.ERROR:
                logger.error(fmt, args);
                break;
            case Log.WARN:
                logger.warn(fmt, args);
                break;
            case Log.VERBOSE:
                logger.verbose(fmt, args);
                break;
            default:
                break;
        }
    }
}
