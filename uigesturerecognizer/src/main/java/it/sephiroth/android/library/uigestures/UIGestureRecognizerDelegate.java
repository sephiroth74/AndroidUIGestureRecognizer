package it.sephiroth.android.library.uigestures;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * @author alessandro crugnola
 */
@SuppressWarnings ("unused")
public class UIGestureRecognizerDelegate {

    private View mView;
    private boolean mEnabled = true;

    public interface Callback {
        /**
         * Asks the delegate if a gesture recognizer should begin interpreting touches.
         *
         * @param recognizer the current recognizer
         * @return true if the recognizer should begin interpreting touches.
         * @see
         * <a href='https://developer.apple.com/reference/uikit/uigesturerecognizerdelegate/1624213-gesturerecognizershouldbegin'>
         * https://developer.apple.com/reference/uikit/uigesturerecognizerdelegate/1624213-gesturerecognizershouldbegin</a>
         */
        boolean shouldBegin(UIGestureRecognizer recognizer);

        /**
         * Asks the delegate if two gesture recognizers should be allowed to recognize gestures simultaneously.<br />
         * true to allow both gestureRecognizer and otherGestureRecognizer to recognize their gestures simultaneously. The
         * default implementation returns false.
         *
         * @param recognizer the first recognizer
         * @param other      the second recognizer
         * @return true if both recognizers shouls be recognized simultaneously
         * @see <a href='https://developer.apple.com/reference/uikit/uigesturerecognizerdelegate/1624208-gesturerecognizer'>
         * https://developer.apple.com/reference/uikit/uigesturerecognizerdelegate/1624208-gesturerecognizer</a>
         */
        boolean shouldRecognizeSimultaneouslyWithGestureRecognizer(UIGestureRecognizer recognizer, UIGestureRecognizer other);

        /**
         * Ask the delegate if a gesture recognizer should receive an object representing a touch.
         *
         * @param recognizer the recognizer that should receive the touch
         * @return true if the recognizer should receive the motion event
         * @see <a href='https://developer.apple.com/reference/uikit/uigesturerecognizerdelegate/1624214-gesturerecognizer'>
         * https://developer.apple.com/reference/uikit/uigesturerecognizerdelegate/1624214-gesturerecognizer</a>
         */
        boolean shouldReceiveTouch(final UIGestureRecognizer recognizer);
    }

    private final HashSet<UIGestureRecognizer> mSet = new LinkedHashSet<>();
    private Callback mCallback;

    public UIGestureRecognizerDelegate(@Nullable Callback callback) {
        mCallback = callback;
    }

    /**
     * @param callback set the optional callback
     * @since 1.0.0
     */
    public void setCallback(final Callback callback) {
        this.mCallback = callback;
    }

    /**
     * @param recognizer add a new gesture recognizer to the chain
     * @since 1.0.0
     */
    public void addGestureRecognizer(@NonNull final UIGestureRecognizer recognizer) {
        recognizer.setDelegate(this);
        mSet.add(recognizer);
    }

    /**
     * @param recognizer remove a previously added gesture recognizer
     * @return true if succesfully removed from the list
     * @since 1.0.0
     */
    public boolean removeGestureRecognizer(@NonNull final UIGestureRecognizer recognizer) {
        if (mSet.remove(recognizer)) {
            recognizer.setDelegate(null);
            recognizer.clearStateListeners();
            return true;
        }
        return false;
    }

    /**
     * Remove all the gesture recognizers currently associated with the delegate
     *
     * @since 1.0.0
     */
    public void clear() {
        for (UIGestureRecognizer uiGestureRecognizer : mSet) {
            uiGestureRecognizer.setDelegate(null);
            uiGestureRecognizer.clearStateListeners();
        }
        mSet.clear();
    }

    /**
     * Forward the view's touchEvent
     *
     * @param view  the view that generated the event
     * @param event the motion event
     * @return true if handled
     * @since 1.0.0
     */
    public boolean onTouchEvent(final View view, final MotionEvent event) {
        boolean handled = false;

        // TODO: each recognizer should prepare its internal status here
        // but don't execute any action
        for (UIGestureRecognizer recognizer : mSet) {
            if (shouldReceiveTouch(recognizer)) {
                handled |= recognizer.onTouchEvent(event);
            } else {
                handled |= recognizer.onTouchEvent(event);
            }
        }

        // TODO: here we need another loop to tell each recognizer to execute its action

        return handled;
    }

    /**
     * Helper method to start listening for touch events. Use this instead
     * of {@link #onTouchEvent(View, MotionEvent)}
     *
     * @param view
     */
    public void startListeningView(@NonNull final View view) {
        stopListeningView();

        mView = view;
        mView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint ("ClickableViewAccessibility")
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                return onTouchEvent(v, event);
            }
        });
    }

    /**
     * Stop listening for touch events on the associated view
     */
    public void stopListeningView() {
        if (null != mView) {
            mView.setOnTouchListener(null);
            mView = null;
        }
    }

    /**
     * Enable/Disable any registered gestures
     *
     * @param enabled
     */
    public void setEnabled(final boolean enabled) {
        mEnabled = enabled;
        if (!mEnabled) {
            stopListeningView();
        } else {
            startListeningView(mView);
        }
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public boolean shouldRecognizeSimultaneouslyWithGestureRecognizer(final UIGestureRecognizer recognizer) {
        Log.i(getClass().getSimpleName(), "shouldRecognizeSimultaneouslyWithGestureRecognizer(" + recognizer + ")");
        if (mSet.size() == 1) {
            return true;
        }

        boolean result = true;
        for (UIGestureRecognizer other : mSet) {
            if (other != recognizer) {
                Log.v(getClass().getSimpleName(), "other: " + other + ", other.began: " + other.hasBeganFiringEvents());
                if (other.hasBeganFiringEvents()) {
                    result &= null != mCallback && mCallback.shouldRecognizeSimultaneouslyWithGestureRecognizer(recognizer, other);
                }
            }
        }
        Log.v(getClass().getSimpleName(), "result: " + result);
        return result;
    }

    public boolean shouldBegin(UIGestureRecognizer recognizer) {
        return null == mCallback || mCallback.shouldBegin(recognizer);
    }

    private boolean shouldRecognizeSimultaneouslyWithGestureRecognizer(
        final UIGestureRecognizer current, final UIGestureRecognizer recognizer) {
        return null == mCallback || mCallback.shouldRecognizeSimultaneouslyWithGestureRecognizer(current, recognizer);
    }

    private boolean shouldReceiveTouch(final UIGestureRecognizer recognizer) {
        return null == mCallback || mCallback.shouldReceiveTouch(recognizer);
    }
}
