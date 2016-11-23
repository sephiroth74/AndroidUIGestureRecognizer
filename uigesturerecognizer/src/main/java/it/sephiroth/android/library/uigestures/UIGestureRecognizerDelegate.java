package it.sephiroth.android.library.uigestures;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @author alessandro crugnola
 */
@SuppressWarnings ("unused")
public class UIGestureRecognizerDelegate {

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
         * Asks the delegate if two gesture recognizers should be allowed to recognize gestures simultaneously.
         *
         * @param current    the first recognizer
         * @param recognizer the second recognizer
         * @return true if both recognizers shouls be recognized simultaneously
         * @see <a href='https://developer.apple.com/reference/uikit/uigesturerecognizerdelegate/1624208-gesturerecognizer'>
         * https://developer.apple.com/reference/uikit/uigesturerecognizerdelegate/1624208-gesturerecognizer</a>
         */
        boolean shouldRecognizeSimultaneouslyWithGestureRecognizer(UIGestureRecognizer current, UIGestureRecognizer recognizer);

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
     * @return
     * @since 1.0.0
     */
    public boolean removeGestureRecognizer(@NonNull final UIGestureRecognizer recognizer) {
        if (mSet.remove(recognizer)) {
            recognizer.setDelegate(null);
            return true;
        }
        return false;
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
        boolean h;
        List<UIGestureRecognizer> list = new LinkedList<>();

        for (UIGestureRecognizer recognizer : mSet) {
            if (shouldReceiveTouch(recognizer)) {
                boolean pass = true;
                for (UIGestureRecognizer current : list) {
                    if (!shouldRecognizeSimultaneouslyWithGestureRecognizer(current, recognizer)) {
                        pass = false;
                        break;
                    }
                }
                h = pass && recognizer.onTouchEvent(event);
                list.add(recognizer);
                handled |= h;
            } else {
                handled |= recognizer.onTouchEvent(event);
            }
        }
        return handled;
    }

    protected boolean shouldBegin(UIGestureRecognizer recognizer) {
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
