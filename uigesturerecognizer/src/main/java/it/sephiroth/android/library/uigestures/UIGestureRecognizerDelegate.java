package it.sephiroth.android.library.uigestures;

import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by alessandro crugnola on 11/20/16.
 * UIGestureRecognizer
 */
@SuppressWarnings ("unused")
public class UIGestureRecognizerDelegate implements View.OnTouchListener {
    private final HashSet<UIGestureRecognizer> mSet = new HashSet<>();
    private View mView;

    public void addGestureRecognizer(@NonNull final UIGestureRecognizer recognizer) {
        recognizer.setDelegate(this);
        mSet.add(recognizer);
    }

    public boolean removeGestureRecognizer(@NonNull final UIGestureRecognizer recognizer) {
        if (mSet.remove(recognizer)) {
            recognizer.setDelegate(null);
            return true;
        }
        return false;
    }

    public void start(@NonNull final View view) {
        mView = view;
        mView.setOnTouchListener(this);
    }

    public void stop() {
        if (null != mView) {
            mView.setOnTouchListener(null);
            mView = null;
        }
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent event) {
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
        return true;
    }

    private boolean shouldRecognizeSimultaneouslyWithGestureRecognizer(
        final UIGestureRecognizer current, final UIGestureRecognizer recognizer) {
        return true;
    }

    private boolean shouldReceiveTouch(final UIGestureRecognizer recognizer) {
        return true;
    }
}
