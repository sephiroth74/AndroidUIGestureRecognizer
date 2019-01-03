package it.sephiroth.android.library.uigestures

import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
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
class TestLongPressGesture : TestBaseClass() {

    @Test
    fun test_singleTap1Finger_longPressWithMotion() {
        setTitle("Long Press 1 touch")
        val latchBegan = CountDownLatch(1)
        val latchChanged = CountDownLatch(1)
        val latchEnd = CountDownLatch(1)
        val activity = activityTestRule.activity
        val delegate = activity.delegate
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UILongPressGestureRecognizer(context)
        recognizer.tag = "long-press"
        recognizer.touchesRequired = 1
        recognizer.tapsRequired = 0
        recognizer.longPressTimeout = ViewConfiguration.getLongPressTimeout().toLong()
        recognizer.actionListener = { recognizer: UIGestureRecognizer ->
            Log.v(TAG, "recognizer: $recognizer")

            activityTestRule.activity.actionListener.invoke(recognizer)

            when (recognizer.state) {
                State.Began -> latchBegan.countDown()
                State.Changed -> latchChanged.countDown()
                State.Ended -> latchEnd.countDown()
            }
        }

        delegate.addGestureRecognizer(recognizer)

        val pt1 = randomPointOnScreen()
        val pt2 = randomPointOnScreen()

        interaction.swipe(pt1.x, pt1.y, pt2.x, pt2.y, 5, true, (ViewConfiguration.getLongPressTimeout() * 2.5).toLong())

        latchBegan.await(10, TimeUnit.SECONDS)
        latchChanged.await(10, TimeUnit.SECONDS)
        latchEnd.await(10, TimeUnit.SECONDS)

        assertEquals(0L, latchBegan.count)
        assertEquals(0L, latchChanged.count)
        assertEquals(0L, latchEnd.count)
    }


    @Test
    fun test_2fingers_longPressWithMotion() {
        setTitle("Long Press 2 touches")
        val latchBegan = CountDownLatch(1)
        val latchChanged = CountDownLatch(1)
        val latchEnd = CountDownLatch(1)
        val activity = activityTestRule.activity
        val delegate = activity.delegate
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UILongPressGestureRecognizer(context)
        recognizer.tag = "long-press"
        recognizer.touchesRequired = 2
        recognizer.tapsRequired = 0
        recognizer.longPressTimeout = 100


        recognizer.actionListener = { recognizer: UIGestureRecognizer ->
            Log.v(TAG, "recognizer: $recognizer")

            activityTestRule.activity.actionListener.invoke(recognizer)

            when (recognizer.state) {
                State.Began -> latchBegan.countDown()
                State.Changed -> latchChanged.countDown()
                State.Ended -> latchEnd.countDown()
            }
        }

        delegate.addGestureRecognizer(recognizer)

        var pt1 = randomPointOnScreen()
        var pt2 = randomPointOnScreen()

        val array = arrayListOf<Array<MotionEvent.PointerCoords>>()

        array.add(arrayOf(
                PointerCoordsBuilder.newBuilder()
                    .setCoords(pt1.x.toFloat(), pt1.y.toFloat())
                    .setSize(1f)
                    .build(),
                PointerCoordsBuilder.newBuilder()
                    .setCoords(pt2.x.toFloat(), pt2.y.toFloat())
                    .setSize(1f)
                    .build()))

        for (i in 0..5) {
            pt1 = randomPointOnScreen()
            pt2 = randomPointOnScreen()
            array.add(arrayOf(
                    PointerCoordsBuilder.newBuilder()
                        .setCoords(pt1.x.toFloat(), pt1.y.toFloat())
                        .setSize(1f)
                        .build(),
                    PointerCoordsBuilder.newBuilder()
                        .setPressure(1f)
                        .setCoords(pt2.x.toFloat(), pt2.y.toFloat())
                        .setSize(1f)
                        .build()))
        }

        interaction.performMultiPointerGesture(array.toTypedArray())

        latchBegan.await(10, TimeUnit.SECONDS)
        latchChanged.await(10, TimeUnit.SECONDS)
        latchEnd.await(10, TimeUnit.SECONDS)

        assertEquals(0L, latchBegan.count)
        assertEquals(0L, latchChanged.count)
        assertEquals(0L, latchEnd.count)
    }

    @Test
    fun test_singleTapLongPress() {
        setTitle("Long Press simple")
        val delegate = activityTestRule.activity.delegate
        val latch = CountDownLatch(2)

        assertNotNull(delegate)
        delegate.clear()

        val longpressRecognizer = UILongPressGestureRecognizer(context)
        longpressRecognizer.tag = "long-press"

        longpressRecognizer.touchesRequired = 1
        longpressRecognizer.tapsRequired = 0
        longpressRecognizer.minimumPressDuration = 300// set as .5 seconds
        longpressRecognizer.allowableMovement = 100.toFloat() //move to 100x100

        longpressRecognizer.actionListener = {
            activityTestRule.activity.actionListener.invoke(it)
            if (latch.count == 2L) {
                assertEquals(State.Began, it.state)
                latch.countDown()
            } else {
                assertEquals(State.Ended, it.state)
                latch.countDown()

            }
        }
        delegate.addGestureRecognizer(longpressRecognizer)
        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.longClick())
        latch.await(10, TimeUnit.SECONDS)

        assertEquals(0L, latch.count)
    }
}
