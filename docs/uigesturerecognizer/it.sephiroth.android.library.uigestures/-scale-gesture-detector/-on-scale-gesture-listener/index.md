[uigesturerecognizer](../../../index.md) / [it.sephiroth.android.library.uigestures](../../index.md) / [ScaleGestureDetector](../index.md) / [OnScaleGestureListener](./index.md)

# OnScaleGestureListener

`interface OnScaleGestureListener`

The listener for receiving notifications when gestures occur.
If you want to listen for all the different gestures then implement
this interface. If you only want to listen for a subset it might
be easier to extend [SimpleOnScaleGestureListener](../-simple-on-scale-gesture-listener/index.md).

An application will receive events in the following order:

* One [OnScaleGestureListener.onScaleBegin](on-scale-begin.md)
* Zero or more [OnScaleGestureListener.onScale](on-scale.md)
* One [OnScaleGestureListener.onScaleEnd](on-scale-end.md)

### Functions

| Name | Summary |
|---|---|
| [onScale](on-scale.md) | `abstract fun onScale(detector: `[`ScaleGestureDetector`](../index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Responds to scaling events for a gesture in progress. Reported by pointer motion. |
| [onScaleBegin](on-scale-begin.md) | `abstract fun onScaleBegin(detector: `[`ScaleGestureDetector`](../index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Responds to the beginning of a scaling gesture. Reported by new pointers going down. |
| [onScaleEnd](on-scale-end.md) | `abstract fun onScaleEnd(detector: `[`ScaleGestureDetector`](../index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Responds to the end of a scale gesture. Reported by existing pointers going up. |

### Inheritors

| Name | Summary |
|---|---|
| [SimpleOnScaleGestureListener](../-simple-on-scale-gesture-listener/index.md) | `class SimpleOnScaleGestureListener : `[`OnScaleGestureListener`](./index.md)<br>A convenience class to extend when you only want to listen for a subset of scaling-related events. This implements all methods in [OnScaleGestureListener](./index.md) but does nothing. [OnScaleGestureListener.onScale](on-scale.md) returns `false` so that a subclass can retrieve the accumulated scale factor in an overridden onScaleEnd. [OnScaleGestureListener.onScaleBegin](on-scale-begin.md) returns `true`. |
| [UIPinchGestureRecognizer](../../-u-i-pinch-gesture-recognizer/index.md) | `open class UIPinchGestureRecognizer : `[`UIGestureRecognizer`](../../-u-i-gesture-recognizer/index.md)`, `[`UIContinuousRecognizer`](../../-u-i-continuous-recognizer.md)`, `[`OnScaleGestureListener`](./index.md)<br>UIPinchGestureRecognizer is a subclass of UIGestureRecognizer that looks for pinching gestures involving two touches. When the user moves the two fingers toward each other, the conventional meaning is zoom-out; when the user moves the two fingers away from each other, the conventional meaning is zoom-in. |
