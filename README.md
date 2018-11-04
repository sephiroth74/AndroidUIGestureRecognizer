# Android UIGestureRecognizer

[![Build Status](https://travis-ci.org/sephiroth74/AndroidUIGestureRecognizer.svg?branch=master)](https://travis-ci.org/sephiroth74/AndroidUIGestureRecognizer)

Java: [ ![Download](https://api.bintray.com/packages/bintray/jcenter/it.sephiroth.android.library.uigestures%3Auigesture-recognizer/images/download.svg) ](https://bintray.com/bintray/jcenter/it.sephiroth.android.library.uigestures%3Auigesture-recognizer/_latestVersion)
<br />
Kotlin: [ ![Download](https://api.bintray.com/packages/bintray/jcenter/it.sephiroth.android.library.uigestures%3Auigesture-recognizer-kotlin/images/download.svg) ](https://bintray.com/bintray/jcenter/it.sephiroth.android.library.uigestures%3Auigesture-recognizer-kotlin/_latestVersion)
<br />

AndroidGestureRecognizer is an Android implementation  of the Apple's UIGestureRecognizer framework.
https://developer.apple.com/reference/uikit/uigesturerecognizer

For more info about the ui gesture recognizers look this WWDC video [https://developer.apple.com/videos/play/wwdc2012/233/](https://developer.apple.com/videos/play/wwdc2012/233)

### From Apple API reference:
> UIGestureRecognizer is an abstract base class for concrete gesture-recognizer classes. A gesture-recognizer object—or, simply, a gesture recognizer—decouples the logic for recognizing a gesture and acting on that recognition. When one of these objects recognizes a common gesture or, in some cases, a change in the gesture, it sends an action message to each designated target object.


# Available Recognizers

* [UITapGestureRecognizer](https://developer.apple.com/reference/uikit/uitapgesturerecognizer)
> UIGestureRecognizer that looks for single or multiple taps. For the gesture to be recognized, the specified number of fingers must tap the view a specified number of times.

* [UIPinchGestureRecognizer](https://developer.apple.com/reference/uikit/uipinchgesturerecognizer)
> Pinching is a continuous gesture. The gesture begins (began) when the two touches have moved enough to be considered a pinch gesture. The gesture changes (changed) when a finger moves (with both fingers remaining pressed). The gesture ends (ended) when both fingers lift from the view.

* [UIRotationGestureRecognizer](https://developer.apple.com/reference/uikit/uirotationgesturerecognizer)
> Rotation is a continuous gesture. It begins when two touches have moved enough to be considered a rotation. The gesture changes when a finger moves while the two fingers are down. It ends when both fingers have lifted. At each stage in the gesture, the gesture recognizer sends its action message.

* [UISwipeGestureRecognizer](https://developer.apple.com/reference/uikit/uiswipegesturerecognizer)
> UISwipeGestureRecognizer recognizes a swipe when the specified number of touches (numberOfTouchesRequired) have moved mostly in an allowable direction (direction) far enough to be considered a swipe. Swipes can be slow or fast. A slow swipe requires high directional precision but a small distance; a fast swipe requires low directional precision but a large distance.

* [UIPanGestureRecognizer](https://developer.apple.com/reference/uikit/uipangesturerecognizer)
> A panning gesture is continuous. It begins (began) when the minimum number of fingers allowed (minimumNumberOfTouches) has moved enough to be considered a pan. It changes (changed) when a finger moves while at least the minimum number of fingers are pressed down. It ends (ended) when all fingers are lifted.

* [UIScreenEdgePanGestureRecognizer](https://developer.apple.com/reference/uikit/uiscreenedgepangesturerecognizer)
> A gesture recognizer that looks for panning (dragging) gestures that starts near the edge of the screen.<br />
The maximum distance between the screen edge can be changed overriding the resource dimension of *gestures\_screen\_edge\_limit*.

* [UILongPressGestureRecognizer](https://developer.apple.com/reference/uikit/uilongpressgesturerecognizer)
> Long-press gestures are continuous. The gesture begins (began) when the number of allowable fingers (numberOfTouchesRequired) have been pressed for the specified period (minimumPressDuration) and the touches do not move beyond the allowable range of movement (allowableMovement). The gesture recognizer transitions to the Change state whenever a finger moves, and it ends (ended) when any of the fingers are lifted.

# Discrete vs Continuous
> The gesture interpreted by a gesture recognizer can be either discrete or continuous. A discrete gesture, such as a double tap, occurs but once in a multi-touch sequence and results in a single action sent. However, when a gesture recognizer interprets a continuous gesture such as a rotation gesture, it sends an action message for each incremental change until the multi-touch sequence concludes. <small>(from https://developer.apple.com/reference/uikit/uigesturerecognizer)</small>

There are 2 types of UI GestureRecognizers: **UIContinuousRecognizer** and **UIDiscreteGestureRecognizer**
#### UIDiscreteGestureRecognizer
Gesture Recognizers that implement this interface will only fire the `Ended` state change.
Internally they will switch between `Possible`, `Ended`, `Failed` or `Cancelled` state.
#### UIContinuousRecognizer
A continuous gesture which will dispatch `Began`, `Changed` and `Ended` state changed events (for instance a pinch gesture, or a rotate gesture).

# Installation

Add the library dependency:

    implementation 'it.sephiroth.android.library.uigestures:uigesture-recognizer:**version**'

# Kotlin

    implementation 'it.sephiroth.android.library.uigestures:uigesture-recognizer-kotlin:**version**'


# Example

```java
    public class MainActivity extends AppCompatActivity
        implements UIGestureRecognizer.OnActionListener, UIGestureRecognizerDelegate.Callback {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            ...

            final UIGestureRecognizerDelegate delegate = new UIGestureRecognizerDelegate(null);

            // optional, override the delegate behaviors
            delegate.setCallback(this);

            // single tap gesture
            UITapGestureRecognizer recognizer1 = new UITapGestureRecognizer(this);
            recognizer1.setNumberOfTapsRequired(1);
            recognizer1.setNumberOfTouchesRequired(1);
            recognizer1.setTag("single-tap");
            recognizer1.setActionListener(this);

            // double tap gesture
            UITapGestureRecognizer recognizer2 = new UITapGestureRecognizer(this);
            recognizer2.setTag("double-tap");
            recognizer2.setNumberOfTapsRequired(2);
            recognizer2.setNumberOfTouchesRequired(1);
            recognizer2.setActionListener(this);

            // We want to recognize a single tap and a double tap separately. Normally, when the user
            // performs a double tap, the single tap would be triggered twice.
            // In this way, however, the single tap will wait until the double tap will fail. So a single tap
            // and a double tap will be triggered separately.
            recognizer1.requireFailureOf(recognizer2);

            // add both gestures to the delegate
            delegate.addGestureRecognizer(recognizer);
            delegate.addGestureRecognizer(recognizer2);

            // forward the touch events to the delegate
            findViewById(R.id.root).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View view, final MotionEvent motionEvent) {
                    return delegate.onTouchEvent(view, motionEvent);
                }
            });
        }

        // ui gesture recognizer event callback
        @Override
        public void onGestureRecognized(@NonNull final UIGestureRecognizer recognizer) {
            Log.d(getClass().getSimpleName(), "onGestureRecognized(" + recognizer + "). state: " + recognizer.getState());
        }

        // delegate methods

        /**
         * @see https://developer.apple.com/reference/uikit/uigesturerecognizerdelegate/1624213-gesturerecognizershouldbegin
         */
        @Override
        public boolean shouldBegin(final UIGestureRecognizer recognizer) {
            return true;
        }

        /**
         * @see https://developer.apple.com/reference/uikit/uigesturerecognizerdelegate/1624208-gesturerecognizer
         */
        @Override
        public boolean shouldRecognizeSimultaneouslyWithGestureRecognizer(
            final UIGestureRecognizer current, final UIGestureRecognizer recognizer) {
            return true;
        }

        /**
         * @see https://developer.apple.com/reference/uikit/uigesturerecognizerdelegate/1624214-gesturerecognizer
         */
        @Override
        public boolean shouldReceiveTouch(final UIGestureRecognizer recognizer) {
            return true;
        }        
```

# JavaDocs
**JavaDocs** are available here: [javadoc.zip](javadoc.zip)
