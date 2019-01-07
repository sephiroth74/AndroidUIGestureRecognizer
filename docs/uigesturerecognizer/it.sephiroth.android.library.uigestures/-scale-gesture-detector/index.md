[uigesturerecognizer](../../index.md) / [it.sephiroth.android.library.uigestures](../index.md) / [ScaleGestureDetector](./index.md)

# ScaleGestureDetector

`class ScaleGestureDetector`

This is a slightly modified version of the Android's ScaleGestureDetector which
doesn't trigger onScaleEnd when the scale ratio is back to 0.

Detects scaling transformation gestures using the supplied [MotionEvent](https://developer.android.com/reference/android/view/MotionEvent.html)s.
The [OnScaleGestureListener](-on-scale-gesture-listener/index.md) callback will notify users when a particular
gesture event has occurred.

This class should only be used with [MotionEvent](https://developer.android.com/reference/android/view/MotionEvent.html)s reported via touch.

To use this class:

* Create an instance of the `ScaleGestureDetector` for your
[View](https://developer.android.com/reference/android/view/View.html)
* In the [View.onTouchEvent](https://developer.android.com/reference/android/view/View.html#onTouchEvent(android.view.MotionEvent)) method ensure you call
[.onTouchEvent](#). The methods defined in your
callback will be executed when the events occur.

### Types

| Name | Summary |
|---|---|
| [OnScaleGestureListener](-on-scale-gesture-listener/index.md) | `interface OnScaleGestureListener`<br>The listener for receiving notifications when gestures occur. If you want to listen for all the different gestures then implement this interface. If you only want to listen for a subset it might be easier to extend [SimpleOnScaleGestureListener](-simple-on-scale-gesture-listener/index.md). |
| [SimpleOnScaleGestureListener](-simple-on-scale-gesture-listener/index.md) | `class SimpleOnScaleGestureListener : `[`OnScaleGestureListener`](-on-scale-gesture-listener/index.md)<br>A convenience class to extend when you only want to listen for a subset of scaling-related events. This implements all methods in [OnScaleGestureListener](-on-scale-gesture-listener/index.md) but does nothing. [OnScaleGestureListener.onScale](-on-scale-gesture-listener/on-scale.md) returns `false` so that a subclass can retrieve the accumulated scale factor in an overridden onScaleEnd. [OnScaleGestureListener.onScaleBegin](-on-scale-gesture-listener/on-scale-begin.md) returns `true`. |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ScaleGestureDetector(mContext: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`, mListener: `[`OnScaleGestureListener`](-on-scale-gesture-listener/index.md)`, mHandler: `[`Handler`](https://developer.android.com/reference/android/os/Handler.html)`? = null)`<br>This is a slightly modified version of the Android's ScaleGestureDetector which doesn't trigger onScaleEnd when the scale ratio is back to 0. |

### Properties

| Name | Summary |
|---|---|
| [currentSpan](current-span.md) | `var currentSpan: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)<br>Return the average distance between each of the pointers forming the gesture in progress through the focal point. |
| [currentSpanX](current-span-x.md) | `var currentSpanX: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)<br>Return the average X distance between each of the pointers forming the gesture in progress through the focal point. |
| [currentSpanY](current-span-y.md) | `var currentSpanY: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)<br>Return the average Y distance between each of the pointers forming the gesture in progress through the focal point. |
| [eventTime](event-time.md) | `var eventTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)<br>Return the event time of the current event being processed. |
| [focusX](focus-x.md) | `var focusX: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)<br>Get the X coordinate of the current gesture's focal point. If a gesture is in progress, the focal point is between each of the pointers forming the gesture. |
| [focusY](focus-y.md) | `var focusY: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)<br>Get the Y coordinate of the current gesture's focal point. If a gesture is in progress, the focal point is between each of the pointers forming the gesture. |
| [isInProgress](is-in-progress.md) | `var isInProgress: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Returns `true` if a scale gesture is in progress. |
| [isQuickScaleEnabled](is-quick-scale-enabled.md) | `var isQuickScaleEnabled: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Set whether the associated [OnScaleGestureListener](-on-scale-gesture-listener/index.md) should receive onScale callbacks when the user performs a doubleTap followed by a swipe. Note that this is enabled by default if the app targets API 19 and newer. |
| [isStylusScaleEnabled](is-stylus-scale-enabled.md) | `var isStylusScaleEnabled: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Sets whether the associates [OnScaleGestureListener](-on-scale-gesture-listener/index.md) should receive onScale callbacks when the user uses a stylus and presses the button. Note that this is enabled by default if the app targets API 23 and newer. |
| [numberOfTouches](number-of-touches.md) | `var numberOfTouches: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [previousSpan](previous-span.md) | `var previousSpan: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)<br>Return the previous average distance between each of the pointers forming the gesture in progress through the focal point. |
| [previousSpanX](previous-span-x.md) | `var previousSpanX: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)<br>Return the previous average X distance between each of the pointers forming the gesture in progress through the focal point. |
| [previousSpanY](previous-span-y.md) | `var previousSpanY: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)<br>Return the previous average Y distance between each of the pointers forming the gesture in progress through the focal point. |
| [scaleFactor](scale-factor.md) | `val scaleFactor: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html)<br>Return the scaling factor from the previous scale event to the current event. This value is defined as ([.getCurrentSpan](#) / [.getPreviousSpan](#)). |
| [timeDelta](time-delta.md) | `val timeDelta: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)<br>Return the time difference in milliseconds between the previous accepted scaling event and the current scaling event. |

### Functions

| Name | Summary |
|---|---|
| [onTouchEvent](on-touch-event.md) | `fun onTouchEvent(event: `[`MotionEvent`](https://developer.android.com/reference/android/view/MotionEvent.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Accepts MotionEvents and dispatches events to a [OnScaleGestureListener](-on-scale-gesture-listener/index.md) when appropriate. |
