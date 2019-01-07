[uigesturerecognizer](../../index.md) / [it.sephiroth.android.library.uigestures](../index.md) / [UIPinchGestureRecognizer](index.md) / [onScaleBegin](./on-scale-begin.md)

# onScaleBegin

`open fun onScaleBegin(detector: `[`ScaleGestureDetector`](../-scale-gesture-detector/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Overrides [OnScaleGestureListener.onScaleBegin](../-scale-gesture-detector/-on-scale-gesture-listener/on-scale-begin.md)

Responds to the beginning of a scaling gesture. Reported by
new pointers going down.

### Parameters

`detector` - The detector reporting the event - use this to
retrieve extended info about event state.

**Return**
Whether or not the detector should continue recognizing
this gesture. For example, if a gesture is beginning
with a focal point outside of a region where it makes
sense, onScaleBegin() may return false to ignore the
rest of the gesture.

