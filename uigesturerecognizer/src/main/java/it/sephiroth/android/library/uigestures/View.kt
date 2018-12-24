package it.sephiroth.android.library.uigestures

import android.view.View

fun View.setGestureDelegate(delegate: UIGestureRecognizerDelegate?) {
    delegate?.let {
        setOnTouchListener { v, event -> it.onTouchEvent(v, event) }
    } ?: run {
        setOnTouchListener(null)
    }
}