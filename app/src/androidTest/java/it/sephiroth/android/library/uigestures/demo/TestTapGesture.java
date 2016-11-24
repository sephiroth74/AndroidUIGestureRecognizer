package it.sephiroth.android.library.uigestures.demo;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Point;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.SdkSuppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State;
import it.sephiroth.android.library.uigestures.UIGestureRecognizerDelegate;
import it.sephiroth.android.library.uigestures.UITapGestureRecognizer;

import static android.content.Context.POWER_SERVICE;
import static android.os.PowerManager.ACQUIRE_CAUSES_WAKEUP;
import static android.os.PowerManager.FULL_WAKE_LOCK;
import static android.os.PowerManager.ON_AFTER_RELEASE;
import static android.support.test.espresso.Espresso.onView;
import static org.junit.Assert.assertEquals;

@RunWith (AndroidJUnit4.class)
@SdkSuppress (minSdkVersion = 18)
public class TestTapGesture {

    static final String PACKAGE_NAME = "it.sephiroth.android.library.uigestures.demo";
    static final int LAUNCH_TIMEOUT = 5000;

    private Instrumentation instrumentation;
    private Context context;
    private UiDevice device;
    PowerManager.WakeLock wakeLock;

    @Before
    public void launchSample() {
        instrumentation = InstrumentationRegistry.getInstrumentation();
        device = UiDevice.getInstance(instrumentation);
        context = InstrumentationRegistry.getContext();
        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT);

        PowerManager power = (PowerManager) context.getSystemService(POWER_SERVICE);
        wakeLock = power.newWakeLock(FULL_WAKE_LOCK | ACQUIRE_CAUSES_WAKEUP | ON_AFTER_RELEASE, "test");
        wakeLock.acquire();
    }

    @After
    public void tearDown() {
        wakeLock.release();
    }

    @Rule
    public ActivityTestRule<BaseTest> mActivityRule = new ActivityTestRule<>(BaseTest.class);

    private UiObject getMainView() {
        return device.findObject(new UiSelector().resourceId(PACKAGE_NAME + ":id/activity_main"));
    }

    private UiObject getTextView() {
        return device.findObject(new UiSelector().resourceId(PACKAGE_NAME + ":id/text"));
    }

    private UiObject getTitleView() {
        return device.findObject(new UiSelector().resourceId(PACKAGE_NAME + ":id/title"));
    }

    @Test
    public void test_singleTap() throws UiObjectNotFoundException, InterruptedException {
        BaseTest activity = mActivityRule.getActivity();

        final UIGestureRecognizerDelegate delegate = activity.delegate;
        Assert.assertNotNull(delegate);
        delegate.clear();

        UITapGestureRecognizer recognizer = new UITapGestureRecognizer(context);
        recognizer.setTag("single-tap");
        recognizer.setNumberOfTouchesRequired(1);
        recognizer.setNumberOfTapsRequired(1);
        recognizer.setActionListener(mActivityRule.getActivity());
        delegate.addGestureRecognizer(recognizer);

        final UiObject mainView = getMainView();
        final UiObject textView = getTextView();
        final UiObject title = getTitleView();
        title.setText("1 Tap");

        textView.setText("None");

        mainView.click();
        SystemClock.sleep(200);

        assertEquals(recognizer.getTag() + ": " + State.Ended, textView.getText());
    }

    @Test
    public void test_singleTap2Fingers() throws UiObjectNotFoundException, InterruptedException {
        final UIGestureRecognizerDelegate delegate = mActivityRule.getActivity().delegate;
        Assert.assertNotNull(delegate);
        delegate.clear();

        UITapGestureRecognizer recognizer = new UITapGestureRecognizer(context);
        recognizer.setTag("single-tap");
        recognizer.setNumberOfTouchesRequired(2);
        recognizer.setNumberOfTapsRequired(1);
        recognizer.setActionListener(mActivityRule.getActivity());
        delegate.addGestureRecognizer(recognizer);

        final UiObject mainView = getMainView();
        final UiObject textView = getTextView();
        final UiObject title = getTitleView();

        title.setText("1 Tap 2 Fingers");

        mainView.performTwoPointerGesture(
            new Point(200, 300),
            new Point(200, 400),
            new Point(200, 300),
            new Point(200, 400),
            1
        );
        SystemClock.sleep(200);
        assertEquals(recognizer.getTag() + ": " + State.Ended, textView.getText());
    }

    @Test
    public void test_singleTap2Taps() throws UiObjectNotFoundException, InterruptedException {
        BaseTest activity = mActivityRule.getActivity();

        final UIGestureRecognizerDelegate delegate = activity.delegate;
        delegate.clear();

        UITapGestureRecognizer recognizer = new UITapGestureRecognizer(context);
        recognizer.setTag("single-tap");
        recognizer.setNumberOfTouchesRequired(1);
        recognizer.setNumberOfTapsRequired(2);
        recognizer.setActionListener(mActivityRule.getActivity());
        delegate.addGestureRecognizer(recognizer);

        final UiObject mainView = getMainView();
        final UiObject textView = getTextView();
        final UiObject title = getTitleView();

        title.setText("2 Taps");
        textView.setText("None");

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.doubleClick());
        SystemClock.sleep(200);

        assertEquals(recognizer.getTag() + ": " + State.Ended, textView.getText());
    }

    @Test
    public void test_singleTap2Taps2Fingers() throws UiObjectNotFoundException, InterruptedException {

    }
}
