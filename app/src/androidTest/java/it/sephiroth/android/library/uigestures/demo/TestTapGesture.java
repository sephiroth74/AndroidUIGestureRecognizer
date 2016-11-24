package it.sephiroth.android.library.uigestures.demo;

import android.content.Context;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.SdkSuppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.Configurator;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State;
import it.sephiroth.android.library.uigestures.UIGestureRecognizerDelegate;
import it.sephiroth.android.library.uigestures.UITapGestureRecognizer;

import static org.junit.Assert.assertEquals;

@RunWith (AndroidJUnit4.class)
@SdkSuppress (minSdkVersion = 18)
public class TestTapGesture {

    static final String PACKAGE_NAME = "it.sephiroth.android.library.uigestures.demo";

    @Rule
    public ActivityTestRule<BaseTest> mActivityRule = new ActivityTestRule<>(BaseTest.class);
    private UiDevice mDevice;

    @Before
    public void setUp() throws Exception {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void test_singleTap() throws UiObjectNotFoundException, InterruptedException {
        Context context = InstrumentationRegistry.getTargetContext();

        final UIGestureRecognizerDelegate delegate = mActivityRule.getActivity().delegate;
        Assert.assertNotNull(delegate);
        delegate.clear();

        UITapGestureRecognizer recognizer = new UITapGestureRecognizer(context);
        recognizer.setTag("single-tap");
        recognizer.setNumberOfTouchesRequired(1);
        recognizer.setNumberOfTapsRequired(1);
        recognizer.setActionListener(mActivityRule.getActivity());
        delegate.addGestureRecognizer(recognizer);

        final UiObject mainView = mDevice.findObject(new UiSelector().resourceId(PACKAGE_NAME + ":id/activity_main"));
        final UiObject textView = mDevice.findObject(new UiSelector().resourceId(PACKAGE_NAME + ":id/text"));

        textView.setText("None");
        mainView.click();
        SystemClock.sleep(200);

        assertEquals(recognizer.getTag() + ": " + State.Ended, textView.getText());
        assertEquals(mActivityRule.getActivity().getCurrentState(), State.Ended);
        assertEquals(recognizer.getState(), State.Ended);

        // test second click
        textView.setText("None");

        mainView.click();
        SystemClock.sleep(200);
        assertEquals(recognizer.getTag() + ": " + State.Ended, textView.getText());
    }

    @Test
    public void test_doubleTap() throws UiObjectNotFoundException, InterruptedException {
        Context context = InstrumentationRegistry.getTargetContext();

        final UIGestureRecognizerDelegate delegate = mActivityRule.getActivity().delegate;
        Assert.assertNotNull(delegate);
        delegate.clear();

        UITapGestureRecognizer recognizer = new UITapGestureRecognizer(context);
        recognizer.setTag("double-tap");
        recognizer.setNumberOfTouchesRequired(1);
        recognizer.setNumberOfTapsRequired(2);
        recognizer.setActionListener(mActivityRule.getActivity());
        delegate.addGestureRecognizer(recognizer);

        final UiObject mainView = mDevice.findObject(new UiSelector().resourceId(PACKAGE_NAME + ":id/activity_main"));
        final UiObject textView = mDevice.findObject(new UiSelector().resourceId(PACKAGE_NAME + ":id/text"));
        SystemClock.sleep(500);

        Configurator cc = Configurator.getInstance();
        cc.setActionAcknowledgmentTimeout(50);
        Espresso.onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.doubleClick());

        assertEquals(State.Ended, recognizer.getState());
        assertEquals(recognizer.getTag() + ": " + State.Ended, textView.getText());
    }

    @Test
    public void test_singleTapWithRequireFailureOfDoubleTap() throws UiObjectNotFoundException, InterruptedException {
        Context context = InstrumentationRegistry.getTargetContext();

        final UIGestureRecognizerDelegate delegate = mActivityRule.getActivity().delegate;
        Assert.assertNotNull(delegate);
        delegate.clear();

        UITapGestureRecognizer recognizer = new UITapGestureRecognizer(context);
        recognizer.setTag("single-tap");
        recognizer.setNumberOfTouchesRequired(1);
        recognizer.setNumberOfTapsRequired(1);
        recognizer.setActionListener(mActivityRule.getActivity());
        delegate.addGestureRecognizer(recognizer);

        UITapGestureRecognizer recognizer1 = new UITapGestureRecognizer(context);
        recognizer1.setTag("double-tap");
        recognizer1.setNumberOfTapsRequired(2);
        recognizer1.setNumberOfTouchesRequired(1);
        recognizer1.setActionListener(mActivityRule.getActivity());
        delegate.addGestureRecognizer(recognizer1);

        recognizer.requireFailureOf(recognizer1);

        final UiObject mainView = mDevice.findObject(new UiSelector().resourceId(PACKAGE_NAME + ":id/activity_main"));
        final UiObject textView = mDevice.findObject(new UiSelector().resourceId(PACKAGE_NAME + ":id/text"));
        textView.setText("None");

        Configurator cc = Configurator.getInstance();
        cc.setActionAcknowledgmentTimeout(100);
        mainView.click();

        SystemClock.sleep(600);

        assertEquals(recognizer.getTag() + ": " + State.Ended, textView.getText());
        assertEquals(State.Ended, recognizer.getState());
        assertEquals(State.Failed, recognizer1.getState());
    }
}
