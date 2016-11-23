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
 * Created by alessandro crugnola on 11/20/16.
 * UIGestureRecognizer
 */
@SuppressWarnings ("unused")
public class UIGestureRecognizerDelegate {

    public interface Callback {
        boolean shouldBegin(UIGestureRecognizer recognizer);

        boolean shouldRecognizeSimultaneouslyWithGestureRecognizer(UIGestureRecognizer current, UIGestureRecognizer recognizer);

        boolean shouldReceiveTouch(final UIGestureRecognizer recognizer);
    }

    private final HashSet<UIGestureRecognizer> mSet = new LinkedHashSet<>();
    private Callback mCallback;

    public UIGestureRecognizerDelegate(@Nullable Callback callback) {
        mCallback = callback;
    }

    /**
     * @param callback
     * @since 1.0.0
     */
    public void setCallback(final Callback callback) {
        this.mCallback = callback;
    }

    /**
     * @param recognizer
     * @since 1.0.0
     */
    public void addGestureRecognizer(@NonNull final UIGestureRecognizer recognizer) {
        recognizer.setDelegate(this);
        mSet.add(recognizer);
    }

    /**
     * @param recognizer
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
     * @param view
     * @param event
     * @return
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
