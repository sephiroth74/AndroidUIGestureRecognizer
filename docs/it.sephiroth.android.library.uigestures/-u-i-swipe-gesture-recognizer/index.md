[uigesturerecognizer](../../index.md) / [it.sephiroth.android.library.uigestures](../index.md) / [UISwipeGestureRecognizer](./index.md)

# UISwipeGestureRecognizer

`open class UISwipeGestureRecognizer : `[`UIGestureRecognizer`](../-u-i-gesture-recognizer/index.md)`, `[`UIDiscreteGestureRecognizer`](../-u-i-discrete-gesture-recognizer.md)

UISwipeGestureRecognizer is a subclass of UIGestureRecognizer that looks for swiping gestures in one or more
directions. A swipe is a discrete gesture, and thus the associated action message is sent only once per gesture.

**Author**
alessandro crugnola

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `UISwipeGestureRecognizer(context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`)`<br>UISwipeGestureRecognizer is a subclass of UIGestureRecognizer that looks for swiping gestures in one or more directions. A swipe is a discrete gesture, and thus the associated action message is sent only once per gesture. |

### Properties

| Name | Summary |
|---|---|
| [direction](direction.md) | `var direction: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Direction of the swipe gesture. Can be one of RIGHT, LEFT, UP, DOWN |
| [maximumTouchFlingTime](maximum-touch-fling-time.md) | `var maximumTouchFlingTime: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>During a move event, the maximum time between touches before the gesture will fail |
| [maximumTouchSlopTime](maximum-touch-slop-time.md) | `var maximumTouchSlopTime: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Maximum amount of time allowed between a touch down and a touch move before the gesture will fail |
| [minimumSwipeDistance](minimum-swipe-distance.md) | `var minimumSwipeDistance: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Minimum total distance before the gesture will begin |
| [numberOfTouchesRequired](number-of-touches-required.md) | `var numberOfTouchesRequired: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Number of touches required for the gesture to be accepted |
| [relativeScrollX](relative-scroll-x.md) | `val relativeScrollX: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |
| [relativeScrollY](relative-scroll-y.md) | `val relativeScrollY: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |
| [scaledMaximumFlingVelocity](scaled-maximum-fling-velocity.md) | `var scaledMaximumFlingVelocity: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [scaledMinimumFlingVelocity](scaled-minimum-fling-velocity.md) | `var scaledMinimumFlingVelocity: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Minimum fling velocity before the touch can be accepted |
| [scaledTouchSlop](scaled-touch-slop.md) | `var scaledTouchSlop: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Minimum distance in pixel before the touch can be considered as a scroll |
| [scrollX](scroll-x.md) | `var scrollX: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |
| [scrollY](scroll-y.md) | `var scrollY: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |
| [translationX](translation-x.md) | `var translationX: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |
| [translationY](translation-y.md) | `var translationY: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |
| [xVelocity](x-velocity.md) | `var xVelocity: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |
| [yVelocity](y-velocity.md) | `var yVelocity: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |

### Inherited Properties

| Name | Summary |
|---|---|
| [actionListener](../-u-i-gesture-recognizer/action-listener.md) | `var actionListener: (`[`UIGestureRecognizer`](../-u-i-gesture-recognizer/index.md)`) -> `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?`<br>UIGestureRecognizer callback |
| [cancelsTouchesInView](../-u-i-gesture-recognizer/cancels-touches-in-view.md) | `var cancelsTouchesInView: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>A Boolean value affecting whether touches are delivered to a view when a gesture is recognized |
| [context](../-u-i-gesture-recognizer/context.md) | `val context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`?` |
| [currentLocationX](../-u-i-gesture-recognizer/current-location-x.md) | `open val currentLocationX: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |
| [currentLocationY](../-u-i-gesture-recognizer/current-location-y.md) | `open val currentLocationY: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |
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
| [numberOfTouches](../-u-i-gesture-recognizer/number-of-touches.md) | `open val numberOfTouches: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [requireFailureOf](../-u-i-gesture-recognizer/require-failure-of.md) | `var requireFailureOf: `[`UIGestureRecognizer`](../-u-i-gesture-recognizer/index.md)`?`<br>Creates a dependency relationship between the receiver and another gesture recognizer when the objects are created |
| [state](../-u-i-gesture-recognizer/state.md) | `var state: `[`State`](../-u-i-gesture-recognizer/-state/index.md)`?` |
| [stateListener](../-u-i-gesture-recognizer/state-listener.md) | `var stateListener: (`[`UIGestureRecognizer`](../-u-i-gesture-recognizer/index.md)`, `[`State`](../-u-i-gesture-recognizer/-state/index.md)`?, `[`State`](../-u-i-gesture-recognizer/-state/index.md)`?) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>UIGestureRecognizer's state change callback |
| [tag](../-u-i-gesture-recognizer/tag.md) | `var tag: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?`<br>custom object the instance should keep |

### Functions

| Name | Summary |
|---|---|
| [handleMessage](handle-message.md) | `open fun handleMessage(msg: `[`Message`](https://developer.android.com/reference/android/os/Message.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
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
| [hasBeganFiringEvents](../-u-i-gesture-recognizer/has-began-firing-events.md) | `open fun hasBeganFiringEvents(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
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

### Companion Object Properties

| Name | Summary |
|---|---|
| [DOWN](-d-o-w-n.md) | `const val DOWN: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [LEFT](-l-e-f-t.md) | `const val LEFT: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [MAXIMUM_TOUCH_FLING_TIME](-m-a-x-i-m-u-m_-t-o-u-c-h_-f-l-i-n-g_-t-i-m-e.md) | `const val MAXIMUM_TOUCH_FLING_TIME: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [MAXIMUM_TOUCH_SLOP_TIME](-m-a-x-i-m-u-m_-t-o-u-c-h_-s-l-o-p_-t-i-m-e.md) | `const val MAXIMUM_TOUCH_SLOP_TIME: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [RIGHT](-r-i-g-h-t.md) | `const val RIGHT: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [UP](-u-p.md) | `const val UP: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
