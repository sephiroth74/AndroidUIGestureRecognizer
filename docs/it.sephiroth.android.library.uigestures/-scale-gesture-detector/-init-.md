[uigesturerecognizer](../../index.md) / [it.sephiroth.android.library.uigestures](../index.md) / [ScaleGestureDetector](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

`ScaleGestureDetector(mContext: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`, mListener: `[`OnScaleGestureListener`](-on-scale-gesture-listener/index.md)`, mHandler: `[`Handler`](https://developer.android.com/reference/android/os/Handler.html)`? = null)`

This is a slightly modified version of the Android's ScaleGestureDetector which
doesn't trigger onScaleEnd when the scale ratio is back to 0.

Detects scaling transformation gestures using the supplied [MotionEvent](https://developer.android.com/reference/android/view/MotionEvent.html)s.
The [OnScaleGestureListener](-on-scale-gesture-listener/index.md) callback will notify users when a particular
gesture event has occurred.

This class should only be used with [MotionEvent](https://developer.android.com/reference/android/view/MotionEvent.html)s reported via touch.

To use this class:

* Create an instance of the `ScaleGestureDetector` for your
[View](https://developer.android.com/reference/android/view/View.html)
* In the [View.onTouchEvent](https://developer.android.com/reference/android/view/View.html#onTouchEvent(android.view.MotionEvent)) method ensure you call
[.onTouchEvent](#). The methods defined in your
callback will be executed when the events occur.
