[uigesturerecognizer](../../index.md) / [it.sephiroth.android.library.uigestures](../index.md) / [UIGestureRecognizerDelegate](./index.md)

# UIGestureRecognizerDelegate

`class UIGestureRecognizerDelegate`

**Author**
alessandro crugnola

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `UIGestureRecognizerDelegate()` |

### Properties

| Name | Summary |
|---|---|
| [isEnabled](is-enabled.md) | `var isEnabled: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Enable/Disable any registered gestures |
| [shouldBegin](should-begin.md) | `var shouldBegin: (recognizer: `[`UIGestureRecognizer`](../-u-i-gesture-recognizer/index.md)`) -> `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Asks the delegate if a gesture recognizer should begin interpreting touches. |
| [shouldReceiveTouch](should-receive-touch.md) | `var shouldReceiveTouch: (recognizer: `[`UIGestureRecognizer`](../-u-i-gesture-recognizer/index.md)`) -> `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Ask the delegate if a gesture recognizer should receive an object representing a touch. |
| [shouldRecognizeSimultaneouslyWithGestureRecognizer](should-recognize-simultaneously-with-gesture-recognizer.md) | `var shouldRecognizeSimultaneouslyWithGestureRecognizer: (recognizer: `[`UIGestureRecognizer`](../-u-i-gesture-recognizer/index.md)`, other: `[`UIGestureRecognizer`](../-u-i-gesture-recognizer/index.md)`) -> `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Asks the delegate if two gesture recognizers should be allowed to recognize gestures simultaneously. true to allow both gestureRecognizer and otherGestureRecognizer to recognize their gestures simultaneously. |

### Functions

| Name | Summary |
|---|---|
| [addGestureRecognizer](add-gesture-recognizer.md) | `fun addGestureRecognizer(recognizer: `[`UIGestureRecognizer`](../-u-i-gesture-recognizer/index.md)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [clear](clear.md) | `fun clear(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Remove all the gesture recognizers currently associated with the delegate |
| [onTouchEvent](on-touch-event.md) | `fun onTouchEvent(view: `[`View`](https://developer.android.com/reference/android/view/View.html)`, event: `[`MotionEvent`](https://developer.android.com/reference/android/view/MotionEvent.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Forward the view's touchEvent |
| [removeGestureRecognizer](remove-gesture-recognizer.md) | `fun removeGestureRecognizer(recognizer: `[`UIGestureRecognizer`](../-u-i-gesture-recognizer/index.md)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [size](size.md) | `fun size(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Returns the number of UIGestureRecognizer currently registered |
