package it.sephiroth.android.library.uigestures

import android.view.MotionEvent
import androidx.test.core.view.PointerCoordsBuilder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class TestTapGesture : TestBaseClass() {

    @Test
    fun test_singleTap() {
        setTitle("Single Tap")
        val latch = CountDownLatch(1)
        val activity = activityTestRule.activity
        val delegate = activity.delegate
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "single-tap"
        recognizer.touchesRequired = 1
        recognizer.tapsRequired = 1
        recognizer.actionListener = {
            activityTestRule.activity.actionListener.invoke(it)
            assertEquals(State.Ended, it.state)
            latch.countDown()
        }
        delegate.addGestureRecognizer(recognizer)

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.click())
        latch.await(10, TimeUnit.SECONDS)
        assertEquals(0L, latch.count)
    }

    @Test
    fun test_singleTap2Fingers() {
        setTitle("Single Tap 2 Fingers")
        val latch = CountDownLatch(1)
        val delegate = activityTestRule.activity.delegate
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "single-tap"
        recognizer.touchesRequired = 2
        recognizer.tapsRequired = 1

        recognizer.actionListener = {
            activityTestRule.activity.actionListener.invoke(it)
            assertEquals(State.Ended, it.state)
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)

        val pt1 = super.randomPointOnScreen()
        val pt2 = super.randomPointOnScreen()

        val array = arrayListOf<Array<MotionEvent.PointerCoords>>()
        array.add(arrayOf(
                PointerCoordsBuilder.newBuilder().setSize(1f).setPressure(1f).setCoords(pt1.x.toFloat(), pt1.y.toFloat()).build(),
                PointerCoordsBuilder.newBuilder().setSize(1f).setPressure(1f).setCoords(pt2.x.toFloat(), pt2.y.toFloat()).build()
                         ))

        interaction.performMultiPointerGesture(array.toTypedArray())
        latch.await(10, TimeUnit.SECONDS)
        assertEquals(0L, latch.count)
    }

    @Test
    fun test_doubleTap() {
        setTitle("Double Tap")
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
            activityTestRule.activity.actionListener.invoke(it)
            assertEquals(State.Ended, it.state)
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.doubleClick())
        latch.await(10, TimeUnit.SECONDS)
        assertEquals(0L, latch.count)
    }

}
