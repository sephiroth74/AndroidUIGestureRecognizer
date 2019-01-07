[uigesturerecognizer](../../index.md) / [it.sephiroth.android.library.uigestures](../index.md) / [ScaleGestureDetector](index.md) / [focusY](./focus-y.md)

# focusY

`var focusY: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)

Get the Y coordinate of the current gesture's focal point.
If a gesture is in progress, the focal point is between
each of the pointers forming the gesture.

If [.isInProgress](#) would return false, the result of this
function is undefined.

**Return**
Y coordinate of the focal point in pixels.

