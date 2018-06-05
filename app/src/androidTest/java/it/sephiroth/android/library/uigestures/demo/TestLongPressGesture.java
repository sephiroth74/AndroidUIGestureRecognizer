package it.sephiroth.android.library.uigestures.demo;

import android.os.SystemClock;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiObjectNotFoundException;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State;
import it.sephiroth.android.library.uigestures.UIGestureRecognizerDelegate;
import it.sephiroth.android.library.uigestures.UILongPressGestureRecognizer;

import static android.support.test.espresso.Espresso.onView;
import static org.junit.Assert.assertEquals;

@RunWith (AndroidJUnit4.class)
@SdkSuppress (minSdkVersion = 18)
public class TestLongPressGesture extends TestBaseClass {

    /**
     * To test Single tap-Long press functionality.
     */
    @Test
    public void test_singleTapLongPress() throws UiObjectNotFoundException, InterruptedException {
        final UIGestureRecognizerDelegate delegate = activityTestRule.getActivity().delegate;

        Assert.assertNotNull(delegate);
        delegate.clear();

        UILongPressGestureRecognizer longpressRecognizer = new UILongPressGestureRecognizer(context);
        longpressRecognizer.setTag("long-press");

        longpressRecognizer.setNumberOfTouchesRequired(1);
        longpressRecognizer.setNumberOfTapsRequired(0);
        longpressRecognizer.setMinimumPressDuration(300);// set as .5 seconds
        longpressRecognizer.setAllowableMovement(100); //move to 100x100

        longpressRecognizer.setActionListener(activityTestRule.getActivity());
        delegate.addGestureRecognizer(longpressRecognizer);

        getTitleView().setText("1 Tap");
        getTextView().setText("None");

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.longClick());
        SystemClock.sleep(200);

        assertEquals(longpressRecognizer.getTag() + ": " + State.Ended, getTextView().getText());
    }

}
