package it.sephiroth.android.library.uigestures.demo

import android.graphics.Point
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import it.sephiroth.android.library.uigestures.UITapGestureRecognizer
import junit.framework.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class TestTapGesture : TestBaseClass() {

    @Test
    fun test_singleTap() {
        val latch = CountDownLatch(1)
        val activity = activityTestRule.activity
        val delegate = activity.delegate
        Assert.assertNotNull(delegate)
        delegate.clear()

        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "single-tap"
        recognizer.touchesRequired = 1
        recognizer.tapsRequired = 1
        recognizer.actionListener = {
            assertEquals(State.Ended, it.state)
            latch.countDown()
        }
        delegate.addGestureRecognizer(recognizer)

        textView.text = "None"

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.click())
        latch.await()
    }

//    @Test
    fun test_singleTap2Fingers() {
        val latch = CountDownLatch(1)
        val delegate = activityTestRule.activity.delegate
        Assert.assertNotNull(delegate)
        delegate.clear()

        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "single-tap"
        recognizer.touchesRequired = 2
        recognizer.tapsRequired = 1
        recognizer.actionListener = activityTestRule.activity.actionListener

        recognizer.actionListener = {
            assertEquals(State.Ended, it.state)
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)

        mainView.performTwoPointerGesture(
                Point(100, 200),
                Point(200, 300),
                Point(100, 200),
                Point(200, 300),
                1)

        latch.await()
    }

    @Test
    fun test_doubleTap() {
        val latch = CountDownLatch(1)
        val activity = activityTestRule.activity

        val delegate = activity.delegate
        delegate.clear()

        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "double-tap"
        recognizer.touchesRequired = 1
        recognizer.tapsRequired = 2
        recognizer.tapTimeout = 400
        recognizer.actionListener = {
            assertEquals(State.Ended, it.state)
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)

        textView.text = "None"

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.doubleClick())
        latch.await()
    }

}
