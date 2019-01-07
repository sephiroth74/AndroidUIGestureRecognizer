[uigesturerecognizer](../../../index.md) / [it.sephiroth.android.library.uigestures](../../index.md) / [ScaleGestureDetector](../index.md) / [OnScaleGestureListener](index.md) / [onScaleBegin](./on-scale-begin.md)

# onScaleBegin

`abstract fun onScaleBegin(detector: `[`ScaleGestureDetector`](../index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

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

