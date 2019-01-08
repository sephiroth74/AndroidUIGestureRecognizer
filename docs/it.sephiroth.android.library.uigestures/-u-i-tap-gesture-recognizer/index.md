[uigesturerecognizer](../../index.md) / [it.sephiroth.android.library.uigestures](../index.md) / [UITapGestureRecognizer](./index.md)

# UITapGestureRecognizer

`open class UITapGestureRecognizer : `[`UIGestureRecognizer`](../-u-i-gesture-recognizer/index.md)`, `[`UIDiscreteGestureRecognizer`](../-u-i-discrete-gesture-recognizer.md)

UITapGestureRecognizer looks for single or multiple taps.
For the gesture to be recognized, the specified number of fingers must tap the view a specified number of times.

**Author**
alessandro crugnola

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `UITapGestureRecognizer(context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`)`<br>UITapGestureRecognizer looks for single or multiple taps. For the gesture to be recognized, the specified number of fingers must tap the view a specified number of times. |

### Properties

| Name | Summary |
|---|---|
| [currentLocationX](current-location-x.md) | `open val currentLocationX: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |
| [currentLocationY](current-location-y.md) | `open val currentLocationY: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |
| [doubleTapTimeout](double-tap-timeout.md) | `var doubleTapTimeout: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)<br>the duration in milliseconds between the first tap's up event and the second tap's down event for an interaction to be considered a double-tap. |
| [numberOfTouches](number-of-touches.md) | `open var numberOfTouches: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [scaledDoubleTapSlop](scaled-double-tap-slop.md) | `var scaledDoubleTapSlop: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Distance in pixels between the first touch and second touch to still be considered a double tap |
| [scaledTouchSlop](scaled-touch-slop.md) | `var scaledTouchSlop: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Distance in pixels a touch can wander before we think the user is scrolling |
| [tapTimeout](tap-timeout.md) | `var tapTimeout: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)<br>the duration in milliseconds we will wait to see if a touch event is a tap or a scroll. If the user does not move within this interval, it is considered to be a tap. |
| [tapsRequired](taps-required.md) | `var tapsRequired: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Change the number of required taps for this recognizer to succeed. Default value is 1 |
| [touchesRequired](touches-required.md) | `var touchesRequired: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Change the number of required touches for this recognizer to succeed. Default value is 1 |

### Inherited Properties

| Name | Summary |
|---|---|
| [actionListener](../-u-i-gesture-recognizer/action-listener.md) | `var actionListener: (`[`UIGestureRecognizer`](../-u-i-gesture-recognizer/index.md)`) -> `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?`<br>UIGestureRecognizer callback |
| [cancelsTouchesInView](../-u-i-gesture-recognizer/cancels-touches-in-view.md) | `var cancelsTouchesInView: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>A Boolean value affecting whether touches are delivered to a view when a gesture is recognized |
| [context](../-u-i-gesture-recognizer/context.md) | `val context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`?` |
| [downLocationX](../-u-i-gesture-recognizer/down-location-x.md) | `val downLocationX: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |
| [downLocationY](../-u-i-gesture-recognizer/down-location-y.md) | `val downLocationY: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |
| [downTime](../-u-i-gesture-recognizer/down-time.md) | `val downTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)<br>Returns the time (in ms) when the user originally pressed down to start a stream of position events |
| [id](../-u-i-gesture-recognizer/id.md) | `var id: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [isEnabled](../-u-i-gesture-recognizer/is-enabled.md) | `var isEnabled: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Toggle the recognizer enabled state. |
| [isListeningForOtherStateChanges](../-u-i-gesture-recognizer/is-listening-for-other-state-changes.md) | `val isListeningForOtherStateChanges: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [lastEvent](../-u-i-gesture-recognizer/last-event.md) | `var lastEvent: `[`MotionEvent`](https://developer.android.com/reference/android/view/MotionEvent.html)`?`<br>Returns the last recorded event |
| [mCurrentLocation](../-u-i-gesture-recognizer/m-current-location.md) | `val mCurrentLocation: `[`PointF`](https://developer.android.com/reference/android/graphics/PointF.html) |
| [mDownLocation](../-u-i-gesture-recognizer/m-down-location.md) | `val mDownLocation: `[`PointF`](https://developer.android.com/reference/android/graphics/PointF.html) |
| [mDownTime](../-u-i-gesture-recognizer/m-down-time.md) | `var mDownTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [mHandler](../-u-i-gesture-recognizer/m-handler.md) | `val mHandler: `[`GestureHandler`](../-u-i-gesture-recognizer/-gesture-handler/index.md) |
| [mPreviousDownLocation](../-u-i-gesture-recognizer/m-previous-down-location.md) | `val mPreviousDownLocation: `[`PointF`](https://developer.android.com/reference/android/graphics/PointF.html) |
| [mPreviousDownTime](../-u-i-gesture-recognizer/m-previous-down-time.md) | `var mPreviousDownTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [requireFailureOf](../-u-i-gesture-recognizer/require-failure-of.md) | `var requireFailureOf: `[`UIGestureRecognizer`](../-u-i-gesture-recognizer/index.md)`?`<br>Creates a dependency relationship between the receiver and another gesture recognizer when the objects are created |
| [state](../-u-i-gesture-recognizer/state.md) | `var state: `[`State`](../-u-i-gesture-recognizer/-state/index.md)`?` |
| [stateListener](../-u-i-gesture-recognizer/state-listener.md) | `var stateListener: (`[`UIGestureRecognizer`](../-u-i-gesture-recognizer/index.md)`, `[`State`](../-u-i-gesture-recognizer/-state/index.md)`?, `[`State`](../-u-i-gesture-recognizer/-state/index.md)`?) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>UIGestureRecognizer's state change callback |
| [tag](../-u-i-gesture-recognizer/tag.md) | `var tag: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?`<br>custom object the instance should keep |

### Functions

| Name | Summary |
|---|---|
| [handleMessage](handle-message.md) | `open fun handleMessage(msg: `[`Message`](https://developer.android.com/reference/android/os/Message.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [hasBeganFiringEvents](has-began-firing-events.md) | `open fun hasBeganFiringEvents(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [onStateChanged](on-state-changed.md) | `open fun onStateChanged(recognizer: `[`UIGestureRecognizer`](../-u-i-gesture-recognizer/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onTouchEvent](on-touch-event.md) | `open fun onTouchEvent(event: `[`MotionEvent`](https://developer.android.com/reference/android/view/MotionEvent.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [removeMessages](remove-messages.md) | `open fun removeMessages(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [reset](reset.md) | `open fun reset(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Inherited Functions

| Name | Summary |
|---|---|
| [addOnStateChangeListenerListener](../-u-i-gesture-recognizer/add-on-state-change-listener-listener.md) | `fun addOnStateChangeListenerListener(listener: `[`OnGestureRecognizerStateChangeListener`](../-on-gesture-recognizer-state-change-listener/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [computeFocusPoint](../-u-i-gesture-recognizer/compute-focus-point.md) | `fun computeFocusPoint(event: `[`MotionEvent`](https://developer.android.com/reference/android/view/MotionEvent.html)`, out: `[`PointF`](https://developer.android.com/reference/android/graphics/PointF.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [finalize](../-u-i-gesture-recognizer/finalize.md) | `fun finalize(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [fireActionEvent](../-u-i-gesture-recognizer/fire-action-event.md) | `fun fireActionEvent(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [hasMessages](../-u-i-gesture-recognizer/has-messages.md) | `fun hasMessages(vararg messages: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [hasOnStateChangeListenerListener](../-u-i-gesture-recognizer/has-on-state-change-listener-listener.md) | `fun hasOnStateChangeListenerListener(listener: `[`OnGestureRecognizerStateChangeListener`](../-on-gesture-recognizer-state-change-listener/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [inState](../-u-i-gesture-recognizer/in-state.md) | `fun inState(vararg states: `[`State`](../-u-i-gesture-recognizer/-state/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [listenForOtherStateChanges](../-u-i-gesture-recognizer/listen-for-other-state-changes.md) | `fun listenForOtherStateChanges(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [logMessage](../-u-i-gesture-recognizer/log-message.md) | `fun logMessage(level: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, fmt: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [removeMessages](../-u-i-gesture-recognizer/remove-messages.md) | `fun removeMessages(vararg messages: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [removeOnStateChangeListenerListener](../-u-i-gesture-recognizer/remove-on-state-change-listener-listener.md) | `fun removeOnStateChangeListenerListener(listener: `[`OnGestureRecognizerStateChangeListener`](../-on-gesture-recognizer-state-change-listener/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [setBeginFiringEvents](../-u-i-gesture-recognizer/set-begin-firing-events.md) | `fun setBeginFiringEvents(value: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [stopListenForOtherStateChanges](../-u-i-gesture-recognizer/stop-listen-for-other-state-changes.md) | `fun stopListenForOtherStateChanges(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [toString](../-u-i-gesture-recognizer/to-string.md) | `open fun toString(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
