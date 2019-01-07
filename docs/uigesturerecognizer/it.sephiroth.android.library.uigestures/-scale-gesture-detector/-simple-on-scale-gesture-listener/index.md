[uigesturerecognizer](../../../index.md) / [it.sephiroth.android.library.uigestures](../../index.md) / [ScaleGestureDetector](../index.md) / [SimpleOnScaleGestureListener](./index.md)

# SimpleOnScaleGestureListener

`class SimpleOnScaleGestureListener : `[`OnScaleGestureListener`](../-on-scale-gesture-listener/index.md)

A convenience class to extend when you only want to listen for a subset
of scaling-related events. This implements all methods in
[OnScaleGestureListener](../-on-scale-gesture-listener/index.md) but does nothing.
[OnScaleGestureListener.onScale](../-on-scale-gesture-listener/on-scale.md) returns
`false` so that a subclass can retrieve the accumulated scale
factor in an overridden onScaleEnd.
[OnScaleGestureListener.onScaleBegin](../-on-scale-gesture-listener/on-scale-begin.md) returns
`true`.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `SimpleOnScaleGestureListener()`<br>A convenience class to extend when you only want to listen for a subset of scaling-related events. This implements all methods in [OnScaleGestureListener](../-on-scale-gesture-listener/index.md) but does nothing. [OnScaleGestureListener.onScale](../-on-scale-gesture-listener/on-scale.md) returns `false` so that a subclass can retrieve the accumulated scale factor in an overridden onScaleEnd. [OnScaleGestureListener.onScaleBegin](../-on-scale-gesture-listener/on-scale-begin.md) returns `true`. |

### Functions

| Name | Summary |
|---|---|
| [onScale](on-scale.md) | `fun onScale(detector: `[`ScaleGestureDetector`](../index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Responds to scaling events for a gesture in progress. Reported by pointer motion. |
| [onScaleBegin](on-scale-begin.md) | `fun onScaleBegin(detector: `[`ScaleGestureDetector`](../index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Responds to the beginning of a scaling gesture. Reported by new pointers going down. |
| [onScaleEnd](on-scale-end.md) | `fun onScaleEnd(detector: `[`ScaleGestureDetector`](../index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Responds to the end of a scale gesture. Reported by existing pointers going up. |
