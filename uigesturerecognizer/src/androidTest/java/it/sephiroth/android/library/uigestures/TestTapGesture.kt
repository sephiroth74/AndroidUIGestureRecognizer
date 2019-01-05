package it.sephiroth.android.library.uigestures

import android.os.SystemClock
import android.view.MotionEvent
import androidx.test.core.view.PointerCoordsBuilder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.SmallTest
import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
@SmallTest
class TestTapGesture : TestBaseClass() {

    @Test
    fun testTap() {
        setTitle("Tap")
        val latch = CountDownLatch(1)

        delegate.clear()
        assertEquals(0, delegate.size())

        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "tap"
        recognizer.tapsRequired = 1
        recognizer.touchesRequired = 1

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)

            assertEquals(State.Ended, it.state)
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)

        assertEquals(1, delegate.size())
        assertTrue(delegate.isEnabled)

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.click())
        latch.await(3, TimeUnit.SECONDS)
        assertEquals(0, latch.count)
    }

    @Test
    fun testDoubleTap() {
        setTitle("Double Tap")
        val latch = CountDownLatch(1)

        delegate.clear()
        assertEquals(0, delegate.size())

        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "double-tap"
        recognizer.tapsRequired = 2
        recognizer.touchesRequired = 1

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)

            assertEquals(State.Ended, it.state)
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)

        assertEquals(1, delegate.size())
        assertTrue(delegate.isEnabled)

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.doubleClick())
        latch.await(3, TimeUnit.SECONDS)
        assertEquals(0, latch.count)
    }

    @Test
    fun testDoubleTapTooLong() {
        setTitle("Double Tap")
        val latch = CountDownLatch(1)

        delegate.clear()
        assertEquals(0, delegate.size())

        val bounds = mainView.visibleBounds
        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "double-tap"
        recognizer.tapsRequired = 2
        recognizer.touchesRequired = 1

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)

            fail("unexpected")
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)

        assertEquals(1, delegate.size())
        assertTrue(delegate.isEnabled)

        interaction.touchDown(bounds.centerX(), bounds.centerY())
        SystemClock.sleep(Interaction.MOTION_EVENT_INJECTION_DELAY_MILLIS.toLong())
        interaction.touchUp(bounds.centerX(), bounds.centerY())
        SystemClock.sleep(recognizer.doubleTapTimeout)

        interaction.touchDown(bounds.centerX(), bounds.centerY())
        SystemClock.sleep(Interaction.MOTION_EVENT_INJECTION_DELAY_MILLIS.toLong())
        interaction.touchUp(bounds.centerX(), bounds.centerY())

        latch.await(3, TimeUnit.SECONDS)
        assertEquals(1, latch.count)
    }

    @Test
    fun testTapCoordinates() {
        setTitle("Tap")
        val latch = CountDownLatch(1)

        delegate.clear()
        assertEquals(0, delegate.size())

        val bounds = mainView.visibleBounds

        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "tap"
        recognizer.tapsRequired = 1
        recognizer.touchesRequired = 1

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)
            assertEquals(State.Ended, it.state)
            assertEquals(bounds.centerX().toFloat(), bounds.left + it.downLocationX)
            assertEquals(bounds.centerY().toFloat(), bounds.top + it.downLocationY)
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)

        assertEquals(1, delegate.size())

        Timber.v("bounds: $bounds")
        Timber.v("bounds center: ${bounds.centerX()}, ${bounds.centerY()}")

        interaction.clickNoSync(bounds.centerX(), bounds.centerY())
        latch.await(3, TimeUnit.SECONDS)
        assertEquals(0, latch.count)
    }

    @Test
    fun testTapTooLong() {
        setTitle("Tap Fail")
        val latch = CountDownLatch(1)
        delegate.clear()
        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "tap"
        recognizer.tapsRequired = 1
        recognizer.touchesRequired = 1

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)
            fail("actionListener unexpected")
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)

        assertEquals(1, delegate.size())

        val bounds = mainView.visibleBounds
        interaction.clickNoSync(bounds.centerX(), bounds.centerY(), recognizer.tapTimeout * 2)
        latch.await(2, TimeUnit.SECONDS)
        assertEquals(1, latch.count)
    }

    @Test
    fun testTapMovedShouldFail() {
        setTitle("Tap Fail Move")
        val latch = CountDownLatch(1)
        delegate.clear()
        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "tap"
        recognizer.tapsRequired = 1
        recognizer.touchesRequired = 1

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)
            fail("actionListener unexpected")
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)

        assertEquals(1, delegate.size())

        val bounds = mainView.visibleBounds
        var x = bounds.centerX()
        var y = bounds.centerY()

        interaction.touchDown(x, y)
        SystemClock.sleep(Interaction.MOTION_EVENT_INJECTION_DELAY_MILLIS.toLong())

        x += (recognizer.touchSlop * 1.5).toInt()

        interaction.touchMove(x, y)
        SystemClock.sleep(Interaction.MOTION_EVENT_INJECTION_DELAY_MILLIS.toLong())

        interaction.touchUp(x, y)

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(1, latch.count)
    }

    @Test
    fun testTapMovedAccepted() {
        setTitle("Tap Fail Move")
        val latch = CountDownLatch(1)
        delegate.clear()
        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "tap"
        recognizer.tapsRequired = 1
        recognizer.touchesRequired = 1

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)
            assertEquals(State.Ended, it.state)
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)

        assertEquals(1, delegate.size())

        val bounds = mainView.visibleBounds
        var x = bounds.centerX()
        var y = bounds.centerY()

        interaction.touchDown(x, y)
        SystemClock.sleep(Interaction.MOTION_EVENT_INJECTION_DELAY_MILLIS.toLong())

        x += recognizer.touchSlop / 3
        y += recognizer.touchSlop / 3

        interaction.touchMove(x, y)
        SystemClock.sleep(Interaction.MOTION_EVENT_INJECTION_DELAY_MILLIS.toLong())

        interaction.touchUp(x, y)

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(0, latch.count)
    }

    @Test
    fun testSingleTap2Fingers() {
        setTitle("Single Tap 2 fingers")

        delegate.clear()

        val latch = CountDownLatch(1)
        val bounds = mainView.visibleBounds
        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "tap"
        recognizer.touchesRequired = 2
        recognizer.tapsRequired = 1

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)

            assertEquals(State.Ended, it.state)
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)

        val array = arrayListOf<Array<MotionEvent.PointerCoords>>()

        array.add(arrayOf(
                PointerCoordsBuilder.newBuilder().setCoords((bounds.centerX() - 10).toFloat(), (bounds.centerY() - 10).toFloat()).build(),
                PointerCoordsBuilder.newBuilder().setCoords((bounds.centerX() + 10).toFloat(), (bounds.centerY() + 10).toFloat()).build()
        ))

        array.add(arrayOf(
                PointerCoordsBuilder.newBuilder().setCoords((bounds.centerX() - 10).toFloat(), (bounds.centerY() - 10).toFloat()).build(),
                PointerCoordsBuilder.newBuilder().setCoords((bounds.centerX() + 10).toFloat(), (bounds.centerY() + 10).toFloat()).build()
        ))

        interaction.performMultiPointerGesture(array.toTypedArray())

        latch.await(3, TimeUnit.SECONDS)
        assertEquals(0, latch.count)
    }

    @Test
    fun testSingleTap2FingersShouldFaild() {
        setTitle("Single Tap 2 fingers")

        delegate.clear()

        val latch = CountDownLatch(1)
        val bounds = mainView.visibleBounds
        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "tap"
        recognizer.touchesRequired = 1
        recognizer.tapsRequired = 1

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)

            fail("actionlistener not expected")
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)

        val array = arrayListOf<Array<MotionEvent.PointerCoords>>()

        array.add(arrayOf(
                PointerCoordsBuilder.newBuilder().setCoords((bounds.centerX() - 10).toFloat(), (bounds.centerY() - 10).toFloat()).build(),
                PointerCoordsBuilder.newBuilder().setCoords((bounds.centerX() + 10).toFloat(), (bounds.centerY() + 10).toFloat()).build()
        ))

        array.add(arrayOf(
                PointerCoordsBuilder.newBuilder().setCoords((bounds.centerX() - 10).toFloat(), (bounds.centerY() - 10).toFloat()).build(),
                PointerCoordsBuilder.newBuilder().setCoords((bounds.centerX() + 10).toFloat(), (bounds.centerY() + 10).toFloat()).build()
        ))

        interaction.performMultiPointerGesture(array.toTypedArray())

        latch.await(3, TimeUnit.SECONDS)
        assertEquals(1, latch.count)
    }

//
//    @Test
//    fun test_singleTap() {
//        setTitle("Single Tap")
//        val latch = CountDownLatch(1)
//        val activity = activityTestRule.activity
//        val delegate = activity.delegate
//        assertNotNull(delegate)
//        delegate.clear()
//
//        val recognizer = UITapGestureRecognizer(context)
//        recognizer.tag = "single-tap"
//        recognizer.touchesRequired = 1
//        recognizer.tapsRequired = 1
//        recognizer.actionListener = {
//            activityTestRule.activity.actionListener.invoke(it)
//            assertEquals(State.Ended, it.state)
//            latch.countDown()
//        }
//        delegate.addGestureRecognizer(recognizer)
//
//        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.click())
//        latch.await(10, TimeUnit.SECONDS)
//        assertEquals(0L, latch.count)
//    }
//
//    @Test
//    fun test_singleTap2Fingers() {
//        setTitle("Single Tap 2 Fingers")
//        val latch = CountDownLatch(1)
//        val delegate = activityTestRule.activity.delegate
//        assertNotNull(delegate)
//        delegate.clear()
//
//        val recognizer = UITapGestureRecognizer(context)
//        recognizer.tag = "single-tap"
//        recognizer.touchesRequired = 2
//        recognizer.tapsRequired = 1
//        recognizer.tapTimeout = 400
//
//        recognizer.actionListener = {
//            activityTestRule.activity.actionListener.invoke(it)
//            assertEquals(State.Ended, it.state)
//            latch.countDown()
//        }
//
//        delegate.addGestureRecognizer(recognizer)
//
//        val pt1 = super.randomPointOnScreen()
//        val pt2 = super.randomPointOnScreen()
//
//        val array = arrayListOf<Array<MotionEvent.PointerCoords>>()
//        array.add(arrayOf(
//                PointerCoordsBuilder.newBuilder().setSize(1f).setPressure(1f).setCoords(pt1.x.toFloat(), pt1.y.toFloat()).build(),
//                PointerCoordsBuilder.newBuilder().setSize(1f).setPressure(1f).setCoords(pt2.x.toFloat(), pt2.y.toFloat()).build()
//                         ))
//
//        interaction.performMultiPointerGesture(array.toTypedArray())
//        latch.await(10, TimeUnit.SECONDS)
//        assertEquals(0L, latch.count)
//    }
//
//    @Test
//    fun test_doubleTap() {
//        setTitle("Double Tap")
//        val latch = CountDownLatch(1)
//        val activity = activityTestRule.activity
//
//        val delegate = activity.delegate
//        delegate.clear()
//
//        val recognizer = UITapGestureRecognizer(context)
//        recognizer.tag = "double-tap"
//        recognizer.touchesRequired = 1
//        recognizer.tapsRequired = 2
//        recognizer.tapTimeout = 400
//        recognizer.actionListener = {
//            activityTestRule.activity.actionListener.invoke(it)
//            assertEquals(State.Ended, it.state)
//            latch.countDown()
//        }
//
//        delegate.addGestureRecognizer(recognizer)
//
//        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.doubleClick())
//        latch.await(10, TimeUnit.SECONDS)
//        assertEquals(0L, latch.count)
//    }
//
//    @Test
//    fun test_requireFailure() {
//        setTitle("Tap Failure")
//
//        val latch = CountDownLatch(1)
//        val activity = activityTestRule.activity
//        val delegate = activity.delegate
//
//        delegate.clear()
//        val recognizer1 = UITapGestureRecognizer(activity)
//        recognizer1.tag = "single-tap"
//        recognizer1.tapsRequired = 1
//        recognizer1.tapTimeout = 300
//
//        val recognizer2 = UITapGestureRecognizer(activity)
//        recognizer2.tag = "double-tap"
//        recognizer2.tapsRequired = 2
//        recognizer2.tapTimeout = 300
//
//        recognizer1.requireFailureOf = recognizer2
//
//        Log.e("test", "tap timeout: ${recognizer1.tapTimeout}")
//
//        recognizer1.actionListener = {
//            Log.e("test", "recognizer1: $it")
//            assertEquals(State.Ended, it.state)
//            latch.countDown()
//        }
//
//        recognizer2.actionListener = {
//            Log.e("test", "recognizer2: $it")
//            fail("Unexpected code!")
//        }
//
//        recognizer1.stateListener = {
//            Log.w("test", "recognizer1 state: $it")
//        }
//
//        recognizer2.stateListener = {
//            Log.w("test", "recognizer2 state: $it")
//        }
//
//        delegate.addGestureRecognizer(recognizer1)
//        delegate.addGestureRecognizer(recognizer2)
//
//        val bounds = mainView.visibleBounds
//
//        interaction.clickAndSync(bounds.centerX(), bounds.centerY(), 10)
//        latch.await(10, TimeUnit.SECONDS)
//        assertEquals(0L, latch.count)
//    }
//
//
//    @Test
//    fun test_singleTapDisabled() {
//        setTitle("Single Tap Disabled")
//        val latch = CountDownLatch(1)
//        val activity = activityTestRule.activity
//        val delegate = activity.delegate
//        assertNotNull(delegate)
//        delegate.clear()
//
//        val recognizer = UITapGestureRecognizer(context)
//        recognizer.tag = "single-tap"
//        recognizer.actionListener = {
//            activityTestRule.activity.actionListener.invoke(it)
//            fail("no action expected!")
//            latch.countDown()
//        }
//
//        delegate.addGestureRecognizer(recognizer)
//        delegate.isEnabled = false
//
//        mainView.click()
//
//        latch.await(5, TimeUnit.SECONDS)
//        assertEquals(1L, latch.count)
//    }
}
