# Android UIGestureRecognizer

[![Build Status](https://travis-ci.org/sephiroth74/AndroidUIGestureRecognizer.svg?branch=master)](https://travis-ci.org/sephiroth74/AndroidUIGestureRecognizer)
<br />

AndroidGestureRecognizer is an Android implementation  of the Apple's UIGestureRecognizer framework.
https://developer.apple.com/reference/uikit/uigesturerecognizer

For more info about the ui gesture recognizers look this WWDC video [https://developer.apple.com/videos/play/wwdc2012/233/](https://developer.apple.com/videos/play/wwdc2012/233)

### From Apple API reference:
> UIGestureRecognizer is an abstract base class for concrete gesture-recognizer classes. A gesture-recognizer object—or, simply, a gesture recognizer—decouples the logic for recognizing a gesture and acting on that recognition. When one of these objects recognizes a common gesture or, in some cases, a change in the gesture, it sends an action message to each designated target object.


# Available Recognizers

* [UITapGestureRecognizer](https://developer.apple.com/reference/uikit/uitapgesturerecognizer)
* [UIPinchGestureRecognizer](https://developer.apple.com/reference/uikit/uipinchgesturerecognizer)
* [UIRotationGestureRecognizer](https://developer.apple.com/reference/uikit/uirotationgesturerecognizer)
* [UISwipeGestureRecognizer](https://developer.apple.com/reference/uikit/uiswipegesturerecognizer)
* [UIPanGestureRecognizer](https://developer.apple.com/reference/uikit/uipangesturerecognizer)
* ~~UIScreenEdgePanGestureRecognizer~~
* [UILongPressGestureRecognizer](https://developer.apple.com/reference/uikit/uilongpressgesturerecognizer)

# Discrete vs Continuous
> The gesture interpreted by a gesture recognizer can be either discrete or continuous. A discrete gesture, such as a double tap, occurs but once in a multi-touch sequence and results in a single action sent. However, when a gesture recognizer interprets a continuous gesture such as a rotation gesture, it sends an action message for each incremental change until the multi-touch sequence concludes. <small>(from https://developer.apple.com/reference/uikit/uigesturerecognizer)</small>

There are 2 types of UI GestureRecognizers: **UIContinuousRecognizer** and **UIDiscreteGestureRecognizer**
#### UIDiscreteGestureRecognizer
Gesture Recognizers that implement this interface will only fire the `Ended` state change.
Internally they will switch between `Possible`, `Ended`, `Failed` or `Cancelled` state.
#### UIContinuousRecognizer
A continuous gesture which will dispatch `Began`, `Changed` and `Ended` state changed events (for instance a pinch gesture, or a rotate gesture).


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

            // the single tap gesture requires the double tap to fail
            // in order to be recognized
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
