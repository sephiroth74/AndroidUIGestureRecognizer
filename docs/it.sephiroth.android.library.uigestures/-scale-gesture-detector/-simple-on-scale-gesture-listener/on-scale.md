[uigesturerecognizer](../../../index.md) / [it.sephiroth.android.library.uigestures](../../index.md) / [ScaleGestureDetector](../index.md) / [SimpleOnScaleGestureListener](index.md) / [onScale](./on-scale.md)

# onScale

`fun onScale(detector: `[`ScaleGestureDetector`](../index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Overrides [OnScaleGestureListener.onScale](../-on-scale-gesture-listener/on-scale.md)

Responds to scaling events for a gesture in progress.
Reported by pointer motion.

### Parameters

`detector` - The detector reporting the event - use this to
retrieve extended info about event state.

**Return**
Whether or not the detector should consider this event
as handled. If an event was not handled, the detector
will continue to accumulate movement until an event is
handled. This can be useful if an application, for example,
only wants to update scaling factors if the change is
greater than 0.01.

