[uigesturerecognizer](../../index.md) / [it.sephiroth.android.library.uigestures](../index.md) / [ScaleGestureDetector](index.md) / [onTouchEvent](./on-touch-event.md)

# onTouchEvent

`fun onTouchEvent(event: `[`MotionEvent`](https://developer.android.com/reference/android/view/MotionEvent.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Accepts MotionEvents and dispatches events to a [OnScaleGestureListener](-on-scale-gesture-listener/index.md)
when appropriate.

Applications should pass a complete and consistent event stream to this method.
A complete and consistent event stream involves all MotionEvents from the initial
ACTION_DOWN to the final ACTION_UP or ACTION_CANCEL.

### Parameters

`event` - The event to process

**Return**
true if the event was processed and the detector wants to receive the
rest of the MotionEvents in this event stream.

