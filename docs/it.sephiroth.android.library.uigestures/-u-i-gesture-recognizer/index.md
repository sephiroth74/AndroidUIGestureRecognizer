[uigesturerecognizer](../../index.md) / [it.sephiroth.android.library.uigestures](../index.md) / [UIGestureRecognizer](./index.md)

# UIGestureRecognizer

`abstract class UIGestureRecognizer : `[`OnGestureRecognizerStateChangeListener`](../-on-gesture-recognizer-state-change-listener/index.md)

AndroidGestureRecognizer is an Android implementation
of the Apple's UIGestureRecognizer framework. There's not guarantee, however, that
this library works 100% in the same way as the Apple version.
This is the base class for all the UI gesture implementations.

**Author**
alessandro crugnola

**Version**
1.0.0

### Types

| Name | Summary |
|---|---|
| [GestureHandler](-gesture-handler/index.md) | `inner class GestureHandler : `[`Handler`](https://developer.android.com/reference/android/os/Handler.html) |
| [State](-state/index.md) | `enum class State` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `UIGestureRecognizer(context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`)`<br>AndroidGestureRecognizer is an Android implementation of the Apple's UIGestureRecognizer framework. There's not guarantee, however, that this library works 100% in the same way as the Apple version. This is the base class for all the UI gesture implementations. |

### Properties

| Name | Summary |
|---|---|
| [actionListener](action-listener.md) | `var actionListener: (`[`UIGestureRecognizer`](./index.md)`) -> `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?`<br>UIGestureRecognizer callback |
| [cancelsTouchesInView](cancels-touches-in-view.md) | `var cancelsTouchesInView: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>A Boolean value affecting whether touches are delivered to a view when a gesture is recognized |
| [context](context.md) | `val context: `[`Context`](https://developer.android.com/reference/android/content/Context.html)`?` |
| [currentLocationX](current-location-x.md) | `open val currentLocationX: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |
| [currentLocationY](current-location-y.md) | `open val currentLocationY: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |
| [downLocationX](down-location-x.md) | `val downLocationX: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |
| [downLocationY](down-location-y.md) | `val downLocationY: `[`Float`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-float/index.html) |
| [downTime](down-time.md) | `val downTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)<br>Returns the time (in ms) when the user originally pressed down to start a stream of position events |
| [id](id.md) | `var id: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [isEnabled](is-enabled.md) | `var isEnabled: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Toggle the recognizer enabled state. |
| [isListeningForOtherStateChanges](is-listening-for-other-state-changes.md) | `val isListeningForOtherStateChanges: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [lastEvent](last-event.md) | `var lastEvent: `[`MotionEvent`](https://developer.android.com/reference/android/view/MotionEvent.html)`?`<br>Returns the last recorded event |
| [mCurrentLocation](m-current-location.md) | `val mCurrentLocation: `[`PointF`](https://developer.android.com/reference/android/graphics/PointF.html) |
| [mDownLocation](m-down-location.md) | `val mDownLocation: `[`PointF`](https://developer.android.com/reference/android/graphics/PointF.html) |
| [mDownTime](m-down-time.md) | `var mDownTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [mHandler](m-handler.md) | `val mHandler: `[`GestureHandler`](-gesture-handler/index.md) |
| [mPreviousDownLocation](m-previous-down-location.md) | `val mPreviousDownLocation: `[`PointF`](https://developer.android.com/reference/android/graphics/PointF.html) |
| [mPreviousDownTime](m-previous-down-time.md) | `var mPreviousDownTime: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [numberOfTouches](number-of-touches.md) | `open val numberOfTouches: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [requireFailureOf](require-failure-of.md) | `var requireFailureOf: `[`UIGestureRecognizer`](./index.md)`?`<br>Creates a dependency relationship between the receiver and another gesture recognizer when the objects are created |
| [state](state.md) | `var state: `[`State`](-state/index.md)`?` |
| [stateListener](state-listener.md) | `var stateListener: (`[`UIGestureRecognizer`](./index.md)`, `[`State`](-state/index.md)`?, `[`State`](-state/index.md)`?) -> `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>UIGestureRecognizer's state change callback |
| [tag](tag.md) | `var tag: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`?`<br>custom object the instance should keep |

### Functions

| Name | Summary |
|---|---|
| [addOnStateChangeListenerListener](add-on-state-change-listener-listener.md) | `fun addOnStateChangeListenerListener(listener: `[`OnGestureRecognizerStateChangeListener`](../-on-gesture-recognizer-state-change-listener/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [computeFocusPoint](compute-focus-point.md) | `fun computeFocusPoint(event: `[`MotionEvent`](https://developer.android.com/reference/android/view/MotionEvent.html)`, out: `[`PointF`](https://developer.android.com/reference/android/graphics/PointF.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [finalize](finalize.md) | `fun finalize(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [fireActionEvent](fire-action-event.md) | `fun fireActionEvent(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [handleMessage](handle-message.md) | `abstract fun handleMessage(msg: `[`Message`](https://developer.android.com/reference/android/os/Message.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [hasBeganFiringEvents](has-began-firing-events.md) | `open fun hasBeganFiringEvents(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [hasMessages](has-messages.md) | `fun hasMessages(vararg messages: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [hasOnStateChangeListenerListener](has-on-state-change-listener-listener.md) | `fun hasOnStateChangeListenerListener(listener: `[`OnGestureRecognizerStateChangeListener`](../-on-gesture-recognizer-state-change-listener/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [inState](in-state.md) | `fun inState(vararg states: `[`State`](-state/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [listenForOtherStateChanges](listen-for-other-state-changes.md) | `fun listenForOtherStateChanges(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [logMessage](log-message.md) | `fun logMessage(level: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, fmt: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [onTouchEvent](on-touch-event.md) | `open fun onTouchEvent(event: `[`MotionEvent`](https://developer.android.com/reference/android/view/MotionEvent.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [removeMessages](remove-messages.md) | `abstract fun removeMessages(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>`fun removeMessages(vararg messages: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [removeOnStateChangeListenerListener](remove-on-state-change-listener-listener.md) | `fun removeOnStateChangeListenerListener(listener: `[`OnGestureRecognizerStateChangeListener`](../-on-gesture-recognizer-state-change-listener/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [reset](reset.md) | `open fun reset(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [setBeginFiringEvents](set-begin-firing-events.md) | `fun setBeginFiringEvents(value: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [stopListenForOtherStateChanges](stop-listen-for-other-state-changes.md) | `fun stopListenForOtherStateChanges(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [toString](to-string.md) | `open fun toString(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Inherited Functions

| Name | Summary |
|---|---|
| [onStateChanged](../-on-gesture-recognizer-state-change-listener/on-state-changed.md) | `abstract fun onStateChanged(recognizer: `[`UIGestureRecognizer`](./index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |

### Companion Object Properties

| Name | Summary |
|---|---|
| [DOUBLE_TAP_SLOP](-d-o-u-b-l-e_-t-a-p_-s-l-o-p.md) | `const val DOUBLE_TAP_SLOP: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [DOUBLE_TAP_TIMEOUT](-d-o-u-b-l-e_-t-a-p_-t-i-m-e-o-u-t.md) | `val DOUBLE_TAP_TIMEOUT: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [DOUBLE_TAP_TOUCH_SLOP](-d-o-u-b-l-e_-t-a-p_-t-o-u-c-h_-s-l-o-p.md) | `const val DOUBLE_TAP_TOUCH_SLOP: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [LOG_TAG](-l-o-g_-t-a-g.md) | `val LOG_TAG: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [LONG_PRESS_TIMEOUT](-l-o-n-g_-p-r-e-s-s_-t-i-m-e-o-u-t.md) | `val LONG_PRESS_TIMEOUT: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [TAP_TIMEOUT](-t-a-p_-t-i-m-e-o-u-t.md) | `val TAP_TIMEOUT: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
| [TIMEOUT_DELAY_MILLIS](-t-i-m-e-o-u-t_-d-e-l-a-y_-m-i-l-l-i-s.md) | `const val TIMEOUT_DELAY_MILLIS: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [TOUCH_SLOP](-t-o-u-c-h_-s-l-o-p.md) | `const val TOUCH_SLOP: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [VERSION](-v-e-r-s-i-o-n.md) | `const val VERSION: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [id](id.md) | `var id: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [logEnabled](log-enabled.md) | `var logEnabled: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [sDebug](s-debug.md) | `var sDebug: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [eventActionToString](event-action-to-string.md) | `fun eventActionToString(action: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |

### Inheritors

| Name | Summary |
|---|---|
| [UILongPressGestureRecognizer](../-u-i-long-press-gesture-recognizer/index.md) | `open class UILongPressGestureRecognizer : `[`UIGestureRecognizer`](./index.md)`, `[`UIContinuousRecognizer`](../-u-i-continuous-recognizer.md)<br>UILongPressGestureRecognizer looks for long-press gestures. The user must press one or more fingers on a view and hold them there for a minimum period of time before the action triggers. While down, the user’s fingers may not move more than a specified distance; if they move beyond the specified distance, the gesture fails. |
| [UIPanGestureRecognizer](../-u-i-pan-gesture-recognizer/index.md) | `open class UIPanGestureRecognizer : `[`UIGestureRecognizer`](./index.md)`, `[`UIContinuousRecognizer`](../-u-i-continuous-recognizer.md)<br>UIPanGestureRecognizer is a subclass of UIGestureRecognizer that looks for panning (dragging) gestures. The user must be pressing one or more fingers on a view while they pan it. Clients implementing the action method for this gesture recognizer can ask it for the current translation and velocity of the gesture. |
| [UIPinchGestureRecognizer](../-u-i-pinch-gesture-recognizer/index.md) | `open class UIPinchGestureRecognizer : `[`UIGestureRecognizer`](./index.md)`, `[`UIContinuousRecognizer`](../-u-i-continuous-recognizer.md)`, `[`OnScaleGestureListener`](../-scale-gesture-detector/-on-scale-gesture-listener/index.md)<br>UIPinchGestureRecognizer is a subclass of UIGestureRecognizer that looks for pinching gestures involving two touches. When the user moves the two fingers toward each other, the conventional meaning is zoom-out; when the user moves the two fingers away from each other, the conventional meaning is zoom-in. |
| [UIRotateGestureRecognizer](../-u-i-rotate-gesture-recognizer/index.md) | `open class UIRotateGestureRecognizer : `[`UIGestureRecognizer`](./index.md)`, `[`UIContinuousRecognizer`](../-u-i-continuous-recognizer.md)<br>UIRotationGestureRecognizer is a subclass of UIGestureRecognizer that looks for rotation gestures involving two touches. When the user moves the fingers opposite each other in a circular motion, the underlying view should rotate in a corresponding direction and speed. |
| [UIScreenEdgePanGestureRecognizer](../-u-i-screen-edge-pan-gesture-recognizer/index.md) | `open class UIScreenEdgePanGestureRecognizer : `[`UIGestureRecognizer`](./index.md)`, `[`UIContinuousRecognizer`](../-u-i-continuous-recognizer.md)<br>UIPanGestureRecognizer is a subclass of UIGestureRecognizer that looks for panning (dragging) gestures. The user must be pressing one or more fingers on a view while they pan it. Clients implementing the action method for this gesture recognizer can ask it for the current translation and velocity of the gesture. |
| [UISwipeGestureRecognizer](../-u-i-swipe-gesture-recognizer/index.md) | `open class UISwipeGestureRecognizer : `[`UIGestureRecognizer`](./index.md)`, `[`UIDiscreteGestureRecognizer`](../-u-i-discrete-gesture-recognizer.md)<br>UISwipeGestureRecognizer is a subclass of UIGestureRecognizer that looks for swiping gestures in one or more directions. A swipe is a discrete gesture, and thus the associated action message is sent only once per gesture. |
| [UITapGestureRecognizer](../-u-i-tap-gesture-recognizer/index.md) | `open class UITapGestureRecognizer : `[`UIGestureRecognizer`](./index.md)`, `[`UIDiscreteGestureRecognizer`](../-u-i-discrete-gesture-recognizer.md)<br>UITapGestureRecognizer looks for single or multiple taps. For the gesture to be recognized, the specified number of fingers must tap the view a specified number of times. |
