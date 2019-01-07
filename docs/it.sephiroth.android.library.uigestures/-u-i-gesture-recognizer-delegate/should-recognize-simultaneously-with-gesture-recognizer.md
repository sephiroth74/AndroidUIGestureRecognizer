[uigesturerecognizer](../../index.md) / [it.sephiroth.android.library.uigestures](../index.md) / [UIGestureRecognizerDelegate](index.md) / [shouldRecognizeSimultaneouslyWithGestureRecognizer](./should-recognize-simultaneously-with-gesture-recognizer.md)

# shouldRecognizeSimultaneouslyWithGestureRecognizer

`var shouldRecognizeSimultaneouslyWithGestureRecognizer: (recognizer: `[`UIGestureRecognizer`](../-u-i-gesture-recognizer/index.md)`, other: `[`UIGestureRecognizer`](../-u-i-gesture-recognizer/index.md)`) -> `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Asks the delegate if two gesture recognizers should be allowed to recognize gestures simultaneously.
true to allow both gestureRecognizer and otherGestureRecognizer to recognize their gestures simultaneously.

### Parameters

`recognizer` - the first recognizer

`other` - the second recognizer

**Return**
true if both recognizers shouls be recognized simultaneously

