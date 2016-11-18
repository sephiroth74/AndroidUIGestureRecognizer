package it.sephiroth.android.library.uigestures;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

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
public abstract class UIGestureRecognizer {

    public enum State {
        Possible,
        Began,
        Changed,
        Failed,
        Cancelled,
        Ended
    }

    public static final String VERSION = BuildConfig.VERSION_NAME;

    private State mState;
    private boolean mEnabled;
    private boolean mCancelsTouchesInView;

    public UIGestureRecognizer(@Nullable Context context) {
        mCancelsTouchesInView = true;
        mEnabled = true;
    }

    protected abstract boolean onTouchEvent(MotionEvent event);

    /**
     * @return Returns the number of touches involved in the gesture represented by the receiver.
     * @since 1.0.0
     */
    public abstract int getNumberOfTouches();

    /**
     * @return The current recognizer internal state
     * @since 1.0.0
     */
    public final State getState() {
        return mState;
    }

    protected final void setState(State state) {
        mState = state;
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
     * @param recognizer Creates a dependency relationship between the receiver and another gesture recognizer when the objects
     *                   are created
     * @see <a href='https://developer.apple.com/reference/uikit/uigesturerecognizer/1624203-require'>
     * https://developer.apple.com/reference/uikit/uigesturerecognizer/1624203-require</a>
     * @since 1.0.0
     */
    public void requireFailureOf(@Nullable final UIGestureRecognizer recognizer) {
        throw new RuntimeException("Not Implemented");
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
}
