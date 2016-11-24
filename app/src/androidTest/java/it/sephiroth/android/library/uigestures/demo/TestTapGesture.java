package it.sephiroth.android.library.uigestures.demo;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Point;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.view.MotionEvent;

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

        final UiObject mainView = device.findObject(new UiSelector().resourceId(PACKAGE_NAME + ":id/activity_main"));
        final UiObject textView = device.findObject(new UiSelector().resourceId(PACKAGE_NAME + ":id/text"));

        textView.setText("None");

        mainView.click();
        SystemClock.sleep(200);

        assertEquals(recognizer.getTag() + ": " + State.Ended, textView.getText());
        assertEquals(mActivityRule.getActivity().getCurrentState(), State.Ended);
        assertEquals(State.Ended, recognizer.getState());

        // test second click
        SystemClock.sleep(200);
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

        BaseTest activity = mActivityRule.getActivity();

        UITapGestureRecognizer recognizer = new UITapGestureRecognizer(context);
        recognizer.setTag("single-tap");
        recognizer.setNumberOfTouchesRequired(2);
        recognizer.setNumberOfTapsRequired(1);
        recognizer.setActionListener(mActivityRule.getActivity());
        delegate.addGestureRecognizer(recognizer);

        final UiObject mainView = device.findObject(new UiSelector().resourceId(PACKAGE_NAME + ":id/activity_main"));
        final UiObject textView = device.findObject(new UiSelector().resourceId(PACKAGE_NAME + ":id/text"));

        mainView.performTwoPointerGesture(
            new Point(200, 300),
            new Point(200, 400),
            new Point(200, 300),
            new Point(200, 400),
            1
        );
        SystemClock.sleep(200);

        assertEquals(recognizer.getTag() + ": " + State.Ended, textView.getText());
        assertEquals(mActivityRule.getActivity().getCurrentState(), State.Ended);
        assertEquals(State.Ended, recognizer.getState());

        // test second click
        textView.setText("None");
        mainView.click();

        SystemClock.sleep(800);
        assertEquals("None", textView.getText());
    }

    @Test
    public void test_singleTap5Fingers() throws UiObjectNotFoundException, InterruptedException {
        final UIGestureRecognizerDelegate delegate = mActivityRule.getActivity().delegate;
        Assert.assertNotNull(delegate);
        delegate.clear();

        UITapGestureRecognizer recognizer = new UITapGestureRecognizer(context);
        recognizer.setTag("single-tap");
        recognizer.setNumberOfTouchesRequired(5);
        recognizer.setNumberOfTapsRequired(1);
        recognizer.setActionListener(mActivityRule.getActivity());
        delegate.addGestureRecognizer(recognizer);

        final UiObject mainView = device.findObject(new UiSelector().resourceId(PACKAGE_NAME + ":id/activity_main"));
        final UiObject textView = device.findObject(new UiSelector().resourceId(PACKAGE_NAME + ":id/text"));

        final MotionEvent.PointerCoords[] pointers1 = new MotionEvent.PointerCoords[1];
        pointers1[0] = new MotionEvent.PointerCoords();
        pointers1[0].x = 200;
        pointers1[0].y = 300;

        final MotionEvent.PointerCoords[] pointers2 = new MotionEvent.PointerCoords[1];
        pointers2[0] = new MotionEvent.PointerCoords();
        pointers2[0].x = 300;
        pointers2[0].y = 400;

        final MotionEvent.PointerCoords[] pointers3 = new MotionEvent.PointerCoords[1];
        pointers3[0] = new MotionEvent.PointerCoords();
        pointers3[0].x = 400;
        pointers3[0].y = 500;

        final MotionEvent.PointerCoords[] pointers4 = new MotionEvent.PointerCoords[1];
        pointers4[0] = new MotionEvent.PointerCoords();
        pointers4[0].x = 500;
        pointers4[0].y = 600;

        final MotionEvent.PointerCoords[] pointers5 = new MotionEvent.PointerCoords[1];
        pointers5[0] = new MotionEvent.PointerCoords();
        pointers5[0].x = 700;
        pointers5[0].y = 700;

        mainView.performMultiPointerGesture(pointers1, pointers2, pointers3, pointers4, pointers5);

        SystemClock.sleep(200);

        assertEquals(recognizer.getTag() + ": " + State.Ended, textView.getText());
    }

}
