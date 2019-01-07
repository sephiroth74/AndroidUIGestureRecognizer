[uigesturerecognizer](../index.md) / [it.sephiroth.android.library.uigestures](index.md) / [UIContinuousRecognizer](./-u-i-continuous-recognizer.md)

# UIContinuousRecognizer

`interface UIContinuousRecognizer`

Created by Alessandro Crugnola on 11/20/16

Any [UIGestureRecognizer](-u-i-gesture-recognizer/index.md) that implements the
UIContinuousRecognizer will have these possible states:

* [UIGestureRecognizer.State.Possible](-u-i-gesture-recognizer/-state/-possible.md)
* [UIGestureRecognizer.State.Began](-u-i-gesture-recognizer/-state/-began.md)
* [UIGestureRecognizer.State.Changed](-u-i-gesture-recognizer/-state/-changed.md)
* [UIGestureRecognizer.State.Ended](-u-i-gesture-recognizer/-state/-ended.md)
* [UIGestureRecognizer.State.Failed](-u-i-gesture-recognizer/-state/-failed.md)
* [UIGestureRecognizer.State.Cancelled](-u-i-gesture-recognizer/-state/-cancelled.md)

### Inheritors

| Name | Summary |
|---|---|
| [UILongPressGestureRecognizer](-u-i-long-press-gesture-recognizer/index.md) | `open class UILongPressGestureRecognizer : `[`UIGestureRecognizer`](-u-i-gesture-recognizer/index.md)`, `[`UIContinuousRecognizer`](./-u-i-continuous-recognizer.md)<br>UILongPressGestureRecognizer looks for long-press gestures. The user must press one or more fingers on a view and hold them there for a minimum period of time before the action triggers. While down, the user’s fingers may not move more than a specified distance; if they move beyond the specified distance, the gesture fails. |
| [UIPanGestureRecognizer](-u-i-pan-gesture-recognizer/index.md) | `open class UIPanGestureRecognizer : `[`UIGestureRecognizer`](-u-i-gesture-recognizer/index.md)`, `[`UIContinuousRecognizer`](./-u-i-continuous-recognizer.md)<br>UIPanGestureRecognizer is a subclass of UIGestureRecognizer that looks for panning (dragging) gestures. The user must be pressing one or more fingers on a view while they pan it. Clients implementing the action method for this gesture recognizer can ask it for the current translation and velocity of the gesture. |
| [UIPinchGestureRecognizer](-u-i-pinch-gesture-recognizer/index.md) | `open class UIPinchGestureRecognizer : `[`UIGestureRecognizer`](-u-i-gesture-recognizer/index.md)`, `[`UIContinuousRecognizer`](./-u-i-continuous-recognizer.md)`, `[`OnScaleGestureListener`](-scale-gesture-detector/-on-scale-gesture-listener/index.md)<br>UIPinchGestureRecognizer is a subclass of UIGestureRecognizer that looks for pinching gestures involving two touches. When the user moves the two fingers toward each other, the conventional meaning is zoom-out; when the user moves the two fingers away from each other, the conventional meaning is zoom-in. |
| [UIRotateGestureRecognizer](-u-i-rotate-gesture-recognizer/index.md) | `open class UIRotateGestureRecognizer : `[`UIGestureRecognizer`](-u-i-gesture-recognizer/index.md)`, `[`UIContinuousRecognizer`](./-u-i-continuous-recognizer.md)<br>UIRotationGestureRecognizer is a subclass of UIGestureRecognizer that looks for rotation gestures involving two touches. When the user moves the fingers opposite each other in a circular motion, the underlying view should rotate in a corresponding direction and speed. |
| [UIScreenEdgePanGestureRecognizer](-u-i-screen-edge-pan-gesture-recognizer/index.md) | `open class UIScreenEdgePanGestureRecognizer : `[`UIGestureRecognizer`](-u-i-gesture-recognizer/index.md)`, `[`UIContinuousRecognizer`](./-u-i-continuous-recognizer.md)<br>UIPanGestureRecognizer is a subclass of UIGestureRecognizer that looks for panning (dragging) gestures. The user must be pressing one or more fingers on a view while they pan it. Clients implementing the action method for this gesture recognizer can ask it for the current translation and velocity of the gesture. |
