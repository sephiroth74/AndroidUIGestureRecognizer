[uigesturerecognizer](../../index.md) / [it.sephiroth.android.library.uigestures](../index.md) / [ScaleGestureDetector](index.md) / [focusX](./focus-x.md)

# focusX

`var focusX: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)

Get the X coordinate of the current gesture's focal point.
If a gesture is in progress, the focal point is between
each of the pointers forming the gesture.

If [.isInProgress](#) would return false, the result of this
function is undefined.

**Return**
X coordinate of the focal point in pixels.

