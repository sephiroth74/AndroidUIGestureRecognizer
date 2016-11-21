package it.sephiroth.android.library.uigestures;

import android.support.annotation.NonNull;

/**
 * Created by alessandro crugnola on 11/20/16.
 */
public interface OnGestureRecognizerStateChangeListener {
    void onStateChanged(@NonNull final UIGestureRecognizer recognizer);
}
