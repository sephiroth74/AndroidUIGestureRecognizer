package it.sephiroth.android.library.uigestures.demo;

/**
 * Created by crugnola on 11/23/16.
 * AndroidUIGestureRecognizer
 */

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import it.sephiroth.android.library.uigestures.UIGestureRecognizer;
import it.sephiroth.android.library.uigestures.UIGestureRecognizerDelegate;
import it.sephiroth.android.library.uigestures.UITapGestureRecognizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public void changeText_sameActivity() throws UiObjectNotFoundException, InterruptedException {
        Context context = InstrumentationRegistry.getTargetContext();

        final UIGestureRecognizerDelegate delegate = mActivityRule.getActivity().delegate;
        Assert.assertNotNull(delegate);

        UITapGestureRecognizer recognizer = new UITapGestureRecognizer(context);
        recognizer.setNumberOfTouchesRequired(1);
        recognizer.setNumberOfTapsRequired(1);
        recognizer.setActionListener(mActivityRule.getActivity());
        delegate.addGestureRecognizer(recognizer);

        final UiObject mainView = mDevice.findObject(new UiSelector().resourceId(PACKAGE_NAME + ":id/activity_main"));
        final UiObject textView = mDevice.findObject(new UiSelector().resourceId(PACKAGE_NAME + ":id/text"));

        mainView.click();

        mDevice.waitForIdle(500);

        assertTrue(textView.getText().equals("Ended"));
        assertEquals(mActivityRule.getActivity().getCurrentState(), UIGestureRecognizer.State.Ended);
        assertEquals(recognizer.getState(), UIGestureRecognizer.State.Ended);

    }
}
