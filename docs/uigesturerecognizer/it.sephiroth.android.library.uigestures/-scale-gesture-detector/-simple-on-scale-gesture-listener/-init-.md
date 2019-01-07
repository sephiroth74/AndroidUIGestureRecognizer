[uigesturerecognizer](../../../index.md) / [it.sephiroth.android.library.uigestures](../../index.md) / [ScaleGestureDetector](../index.md) / [SimpleOnScaleGestureListener](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

`SimpleOnScaleGestureListener()`

A convenience class to extend when you only want to listen for a subset
of scaling-related events. This implements all methods in
[OnScaleGestureListener](../-on-scale-gesture-listener/index.md) but does nothing.
[OnScaleGestureListener.onScale](../-on-scale-gesture-listener/on-scale.md) returns
`false` so that a subclass can retrieve the accumulated scale
factor in an overridden onScaleEnd.
[OnScaleGestureListener.onScaleBegin](../-on-scale-gesture-listener/on-scale-begin.md) returns
`true`.

