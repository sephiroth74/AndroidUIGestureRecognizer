package it.sephiroth.android.library.uigestures.demo;

import android.app.Instrumentation;
import android.content.Context;
import android.os.PowerManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;
import it.sephiroth.android.library.uigestures.UIGestureRecognizer;

import static android.content.Context.POWER_SERVICE;
import static android.os.PowerManager.ACQUIRE_CAUSES_WAKEUP;
import static android.os.PowerManager.FULL_WAKE_LOCK;
import static android.os.PowerManager.ON_AFTER_RELEASE;

public class TestBaseClass {
    static final String PACKAGE_NAME = "it.sephiroth.android.library.uigestures.demo";
    static final int LAUNCH_TIMEOUT = 5000;

    Instrumentation instrumentation;
    Context context;
    UiDevice device;
    PowerManager.WakeLock wakeLock;

    @Rule
    public ActivityTestRule<BaseTest> activityTestRule = new ActivityTestRule<>(BaseTest.class);

    @Before
    public void launchSample() {
        instrumentation = InstrumentationRegistry.getInstrumentation();
        device = UiDevice.getInstance(instrumentation);
        context = InstrumentationRegistry.getContext();
        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT);

        UIGestureRecognizer.Companion.setLogEnabled(true);

        PowerManager power = (PowerManager) context.getSystemService(POWER_SERVICE);
        wakeLock = power.newWakeLock(FULL_WAKE_LOCK | ACQUIRE_CAUSES_WAKEUP | ON_AFTER_RELEASE, "test");
        wakeLock.acquire();
    }

    @After
    public void tearDown() {
        wakeLock.release();
    }

    UiObject getMainView() {
        return device.findObject(new UiSelector().resourceId(PACKAGE_NAME + ":id/activity_main"));
    }

    UiObject getTextView() {
        return device.findObject(new UiSelector().resourceId(PACKAGE_NAME + ":id/text"));
    }
}
