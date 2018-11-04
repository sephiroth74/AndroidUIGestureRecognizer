package it.sephiroth.android.library.uigestures.demo

import android.graphics.Point
import android.os.SystemClock
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.filters.SdkSuppress
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiObjectNotFoundException
import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import it.sephiroth.android.library.uigestures.UITapGestureRecognizer
import junit.framework.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class TestTapGesture : TestBaseClass() {

    @Test
    @Throws(UiObjectNotFoundException::class, InterruptedException::class)
    fun test_singleTap() {
        val activity = activityTestRule.activity

        val delegate = activity.delegate
        Assert.assertNotNull(delegate)
        delegate.clear()

        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "single-tap"
        recognizer.touchesRequired = 1
        recognizer.tapsRequired = 1
        recognizer.actionListener = activityTestRule.activity
        delegate.addGestureRecognizer(recognizer)

        titleView.text = "1 Tap"
        textView.text = "None"

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.click())
        SystemClock.sleep(200)

        assertEquals(recognizer.tag as String + ": " + State.Ended, textView.text)
    }

    @Test
    @Throws(UiObjectNotFoundException::class, InterruptedException::class)
    fun test_singleTap2Fingers() {
        val delegate = activityTestRule.activity.delegate
        Assert.assertNotNull(delegate)
        delegate.clear()

        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "single-tap"
        recognizer.touchesRequired = 2
        recognizer.tapsRequired = 1
        recognizer.actionListener = activityTestRule.activity
        delegate.addGestureRecognizer(recognizer)

        titleView.text = "1 Tap 2 Fingers"

        mainView.performTwoPointerGesture(
                Point(200, 400),
                Point(400, 800),
                Point(200, 400),
                Point(400, 800),
                1
                                         )
        SystemClock.sleep(200)
        assertEquals(recognizer.tag as String + ": " + State.Ended, textView.text)
    }

    @Test
    @Throws(UiObjectNotFoundException::class, InterruptedException::class)
    fun test_doubleTap() {
        val activity = activityTestRule.getActivity()

        val delegate = activity.delegate
        delegate.clear()

        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "double-tap"
        recognizer.touchesRequired = 1
        recognizer.tapsRequired = 2
        recognizer.actionListener = activityTestRule.activity
        delegate.addGestureRecognizer(recognizer)

        titleView.text = "2 Taps"
        textView.text = "None"

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.doubleClick())
        SystemClock.sleep(200)

        assertEquals(recognizer.tag as String + ": " + State.Ended, textView.text)
    }

}
