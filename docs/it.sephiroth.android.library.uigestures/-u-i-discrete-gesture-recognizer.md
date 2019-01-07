[uigesturerecognizer](../index.md) / [it.sephiroth.android.library.uigestures](index.md) / [UIDiscreteGestureRecognizer](./-u-i-discrete-gesture-recognizer.md)

# UIDiscreteGestureRecognizer

`interface UIDiscreteGestureRecognizer`

Created by Alessandro Crugnola on 11/20/16

Any [UIGestureRecognizer](-u-i-gesture-recognizer/index.md) that implements the
UIDiscreteGestureRecognizer will only have these possible states:

* [UIGestureRecognizer.State.Possible](-u-i-gesture-recognizer/-state/-possible.md)
* [UIGestureRecognizer.State.Ended](-u-i-gesture-recognizer/-state/-ended.md)
* [UIGestureRecognizer.State.Failed](-u-i-gesture-recognizer/-state/-failed.md)
* [UIGestureRecognizer.State.Cancelled](-u-i-gesture-recognizer/-state/-cancelled.md)

### Inheritors

| Name | Summary |
|---|---|
| [UISwipeGestureRecognizer](-u-i-swipe-gesture-recognizer/index.md) | `open class UISwipeGestureRecognizer : `[`UIGestureRecognizer`](-u-i-gesture-recognizer/index.md)`, `[`UIDiscreteGestureRecognizer`](./-u-i-discrete-gesture-recognizer.md)<br>UISwipeGestureRecognizer is a subclass of UIGestureRecognizer that looks for swiping gestures in one or more directions. A swipe is a discrete gesture, and thus the associated action message is sent only once per gesture. |
| [UITapGestureRecognizer](-u-i-tap-gesture-recognizer/index.md) | `open class UITapGestureRecognizer : `[`UIGestureRecognizer`](-u-i-gesture-recognizer/index.md)`, `[`UIDiscreteGestureRecognizer`](./-u-i-discrete-gesture-recognizer.md)<br>UITapGestureRecognizer looks for single or multiple taps. For the gesture to be recognized, the specified number of fingers must tap the view a specified number of times. |
