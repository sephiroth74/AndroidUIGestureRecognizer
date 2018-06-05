package it.sephiroth.android.library.uigestures.demo;

import android.graphics.Point;
import android.os.SystemClock;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State;
import it.sephiroth.android.library.uigestures.UIGestureRecognizerDelegate;
import it.sephiroth.android.library.uigestures.UITapGestureRecognizer;

import static android.support.test.espresso.Espresso.onView;
import static org.junit.Assert.assertEquals;

@RunWith (AndroidJUnit4.class)
@SdkSuppress (minSdkVersion = 18)
public class TestTapGesture extends TestBaseClass {

    @Test
    public void test_singleTap() throws UiObjectNotFoundException, InterruptedException {
        BaseTest activity = activityTestRule.getActivity();

        final UIGestureRecognizerDelegate delegate = activity.delegate;
        Assert.assertNotNull(delegate);
        delegate.clear();

        UITapGestureRecognizer recognizer = new UITapGestureRecognizer(context);
        recognizer.setTag("single-tap");
        recognizer.setNumberOfTouchesRequired(1);
        recognizer.setNumberOfTapsRequired(1);
        recognizer.setActionListener(activityTestRule.getActivity());
        delegate.addGestureRecognizer(recognizer);

        getTitleView().setText("1 Tap");
        getTextView().setText("None");

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.click());
        SystemClock.sleep(2000);

        assertEquals(recognizer.getTag() + ": " + State.Ended, getTextView().getText());
    }

    @Test
    public void test_singleTap2Fingers() throws UiObjectNotFoundException, InterruptedException {
        final UIGestureRecognizerDelegate delegate = activityTestRule.getActivity().delegate;
        Assert.assertNotNull(delegate);
        delegate.clear();

        UITapGestureRecognizer recognizer = new UITapGestureRecognizer(context);
        recognizer.setTag("single-tap");
        recognizer.setNumberOfTouchesRequired(2);
        recognizer.setNumberOfTapsRequired(1);
        recognizer.setActionListener(activityTestRule.getActivity());
        delegate.addGestureRecognizer(recognizer);

        final UiObject mainView = getMainView();

        getTitleView().setText("1 Tap 2 Fingers");

        mainView.performTwoPointerGesture(
            new Point(200, 300),
            new Point(200, 400),
            new Point(200, 300),
            new Point(200, 400),
            1
        );
        SystemClock.sleep(200);
        assertEquals(recognizer.getTag() + ": " + State.Ended, getTextView().getText());
    }

    @Test
    public void test_singleTap2Taps() throws UiObjectNotFoundException, InterruptedException {
        BaseTest activity = activityTestRule.getActivity();

        final UIGestureRecognizerDelegate delegate = activity.delegate;
        delegate.clear();

        UITapGestureRecognizer recognizer = new UITapGestureRecognizer(context);
        recognizer.setTag("single-tap");
        recognizer.setNumberOfTouchesRequired(1);
        recognizer.setNumberOfTapsRequired(2);
        recognizer.setActionListener(activityTestRule.getActivity());
        delegate.addGestureRecognizer(recognizer);

        getTitleView().setText("2 Taps");
        getTextView().setText("None");

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.doubleClick());
        SystemClock.sleep(200);

        assertEquals(recognizer.getTag() + ": " + State.Ended, getTextView().getText());
    }

}
