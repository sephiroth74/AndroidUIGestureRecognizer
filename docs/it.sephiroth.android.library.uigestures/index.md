[uigesturerecognizer](../index.md) / [it.sephiroth.android.library.uigestures](./index.md)

## Package it.sephiroth.android.library.uigestures

### Types

| Name | Summary |
|---|---|
| [Geometry](-geometry.md) | `object Geometry`<br>Copyright 2017 Adobe Systems Incorporated.  All rights reserved. $Id$ $DateTime$ $Change$ $File$ $Revision$ $Author$ |
| [OnGestureRecognizerStateChangeListener](-on-gesture-recognizer-state-change-listener/index.md) | `interface OnGestureRecognizerStateChangeListener`<br>Created by alessandro crugnola on 11/20/16. |
| [ScaleGestureDetector](-scale-gesture-detector/index.md) | `class ScaleGestureDetector`<br>This is a slightly modified version of the Android's ScaleGestureDetector which doesn't trigger onScaleEnd when the scale ratio is back to 0. |
| [UIContinuousRecognizer](-u-i-continuous-recognizer.md) | `interface UIContinuousRecognizer`<br>Created by Alessandro Crugnola on 11/20/16 |
| [UIDiscreteGestureRecognizer](-u-i-discrete-gesture-recognizer.md) | `interface UIDiscreteGestureRecognizer`<br>Created by Alessandro Crugnola on 11/20/16 |
| [UIGestureRecognizer](-u-i-gesture-recognizer/index.md) | `abstract class UIGestureRecognizer : `[`OnGestureRecognizerStateChangeListener`](-on-gesture-recognizer-state-change-listener/index.md)<br>AndroidGestureRecognizer is an Android implementation of the Apple's UIGestureRecognizer framework. There's not guarantee, however, that this library works 100% in the same way as the Apple version. This is the base class for all the UI gesture implementations. |
| [UIGestureRecognizerDelegate](-u-i-gesture-recognizer-delegate/index.md) | `class UIGestureRecognizerDelegate` |
| [UILongPressGestureRecognizer](-u-i-long-press-gesture-recognizer/index.md) | `open class UILongPressGestureRecognizer : `[`UIGestureRecognizer`](-u-i-gesture-recognizer/index.md)`, `[`UIContinuousRecognizer`](-u-i-continuous-recognizer.md)<br>UILongPressGestureRecognizer looks for long-press gestures. The user must press one or more fingers on a view and hold them there for a minimum period of time before the action triggers. While down, the user’s fingers may not move more than a specified distance; if they move beyond the specified distance, the gesture fails. |
| [UIPanGestureRecognizer](-u-i-pan-gesture-recognizer/index.md) | `open class UIPanGestureRecognizer : `[`UIGestureRecognizer`](-u-i-gesture-recognizer/index.md)`, `[`UIContinuousRecognizer`](-u-i-continuous-recognizer.md)<br>UIPanGestureRecognizer is a subclass of UIGestureRecognizer that looks for panning (dragging) gestures. The user must be pressing one or more fingers on a view while they pan it. Clients implementing the action method for this gesture recognizer can ask it for the current translation and velocity of the gesture. |
| [UIPinchGestureRecognizer](-u-i-pinch-gesture-recognizer/index.md) | `open class UIPinchGestureRecognizer : `[`UIGestureRecognizer`](-u-i-gesture-recognizer/index.md)`, `[`UIContinuousRecognizer`](-u-i-continuous-recognizer.md)`, `[`OnScaleGestureListener`](-scale-gesture-detector/-on-scale-gesture-listener/index.md)<br>UIPinchGestureRecognizer is a subclass of UIGestureRecognizer that looks for pinching gestures involving two touches. When the user moves the two fingers toward each other, the conventional meaning is zoom-out; when the user moves the two fingers away from each other, the conventional meaning is zoom-in. |
| [UIRectEdge](-u-i-rect-edge/index.md) | `enum class UIRectEdge` |
| [UIRotateGestureRecognizer](-u-i-rotate-gesture-recognizer/index.md) | `open class UIRotateGestureRecognizer : `[`UIGestureRecognizer`](-u-i-gesture-recognizer/index.md)`, `[`UIContinuousRecognizer`](-u-i-continuous-recognizer.md)<br>UIRotationGestureRecognizer is a subclass of UIGestureRecognizer that looks for rotation gestures involving two touches. When the user moves the fingers opposite each other in a circular motion, the underlying view should rotate in a corresponding direction and speed. |
| [UIScreenEdgePanGestureRecognizer](-u-i-screen-edge-pan-gesture-recognizer/index.md) | `open class UIScreenEdgePanGestureRecognizer : `[`UIGestureRecognizer`](-u-i-gesture-recognizer/index.md)`, `[`UIContinuousRecognizer`](-u-i-continuous-recognizer.md)<br>UIPanGestureRecognizer is a subclass of UIGestureRecognizer that looks for panning (dragging) gestures. The user must be pressing one or more fingers on a view while they pan it. Clients implementing the action method for this gesture recognizer can ask it for the current translation and velocity of the gesture. |
| [UISwipeGestureRecognizer](-u-i-swipe-gesture-recognizer/index.md) | `open class UISwipeGestureRecognizer : `[`UIGestureRecognizer`](-u-i-gesture-recognizer/index.md)`, `[`UIDiscreteGestureRecognizer`](-u-i-discrete-gesture-recognizer.md)<br>UISwipeGestureRecognizer is a subclass of UIGestureRecognizer that looks for swiping gestures in one or more directions. A swipe is a discrete gesture, and thus the associated action message is sent only once per gesture. |
| [UITapGestureRecognizer](-u-i-tap-gesture-recognizer/index.md) | `open class UITapGestureRecognizer : `[`UIGestureRecognizer`](-u-i-gesture-recognizer/index.md)`, `[`UIDiscreteGestureRecognizer`](-u-i-discrete-gesture-recognizer.md)<br>UITapGestureRecognizer looks for single or multiple taps. For the gesture to be recognized, the specified number of fingers must tap the view a specified number of times. |

### Extensions for External Classes

| Name | Summary |
|---|---|
| [android.graphics.Point](android.graphics.-point/index.md) |  |
| [android.graphics.PointF](android.graphics.-point-f/index.md) |  |
| [android.view.View](android.view.-view/index.md) |  |
