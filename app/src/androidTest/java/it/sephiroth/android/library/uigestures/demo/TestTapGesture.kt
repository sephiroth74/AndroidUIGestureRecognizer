package it.sephiroth.android.library.uigestures.demo

import android.graphics.Point
import android.os.SystemClock
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import it.sephiroth.android.library.uigestures.UITapGestureRecognizer
import junit.framework.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class TestTapGesture : TestBaseClass() {

    @Test
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
