[uigesturerecognizer](../../index.md) / [it.sephiroth.android.library.uigestures](../index.md) / [UIPinchGestureRecognizer](index.md) / [onScaleEnd](./on-scale-end.md)

# onScaleEnd

`open fun onScaleEnd(detector: `[`ScaleGestureDetector`](../-scale-gesture-detector/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Overrides [OnScaleGestureListener.onScaleEnd](../-scale-gesture-detector/-on-scale-gesture-listener/on-scale-end.md)

Responds to the end of a scale gesture. Reported by existing
pointers going up.

Once a scale has ended, [ScaleGestureDetector.getFocusX](#)
and [ScaleGestureDetector.getFocusY](#) will return focal point
of the pointers remaining on the screen.

### Parameters

`detector` - The detector reporting the event - use this to
retrieve extended info about event state.