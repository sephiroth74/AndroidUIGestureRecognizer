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
        latch.await(2, TimeUnit.SECONDS)
        assertEquals(0, latch.count)
    }

    @Test
    fun testTapNotEnabled() {
        setTitle("Tap Disabled")
        delegate.clear()

        val latch = CountDownLatch(1)
        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "tap"
        recognizer.tapsRequired = 1
        recognizer.touchesRequired = 1

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)
            fail()
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)

        recognizer.isEnabled = false
        assertFalse(recognizer.isEnabled)

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.click())
        latch.await(2, TimeUnit.SECONDS)
        assertEquals(1, latch.count)
    }

    @Test
    fun testTapDelegateNotEnabled() {
        setTitle("Delegate Disabled")

        delegate.clear()

        val latch = CountDownLatch(1)
        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "tap"
        recognizer.tapsRequired = 1
        recognizer.touchesRequired = 1

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)
            fail()
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)

        delegate.isEnabled = false
        assertFalse(delegate.isEnabled)

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.click())
        latch.await(2, TimeUnit.SECONDS)
        assertEquals(1, latch.count)
    }

    @Test
    fun testSingleTapFailUsing2Fingers() {
        setTitle("Tap")
        delegate.clear()

        val latch = CountDownLatch(1)
        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "tap"
        recognizer.tapsRequired = 1
        recognizer.touchesRequired = 2

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)
            fail()
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)
        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.click())
        latch.await(2, TimeUnit.SECONDS)
        assertEquals(1, latch.count)
    }

    @Test
    fun testTapMultiple() {
        setTitle("Tap Tap")
        val latch = CountDownLatch(1)

        delegate.clear()
        assertEquals(0, delegate.size())

        val recognizer1 = UITapGestureRecognizer(context)
        recognizer1.tag = "tap-1"

        val recognizer2 = UITapGestureRecognizer(context)
        recognizer2.tag = "tap-2"

        recognizer1.actionListener = { it ->
            activity.actionListener.invoke(it)
            assertEquals(State.Ended, it.state)
            latch.countDown()
        }

        recognizer2.actionListener = { it ->
            fail()
        }

        delegate.addGestureRecognizer(recognizer1)
        delegate.addGestureRecognizer(recognizer2)

        delegate.shouldRecognizeSimultaneouslyWithGestureRecognizer = { it1: UIGestureRecognizer, it2: UIGestureRecognizer ->
            Timber.d("it1 = $it1, it2 = $it2")
            false
        }

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.click())

        latch.await(2, TimeUnit.SECONDS)

        assertEquals(0, latch.count)
    }

    @Test
    fun testTapMultiple2() {
        setTitle("Tap Tap")
        val latch = CountDownLatch(2)

        delegate.clear()
        assertEquals(0, delegate.size())

        val recognizer1 = UITapGestureRecognizer(context)
        recognizer1.tag = "tap-1"

        val recognizer2 = UITapGestureRecognizer(context)
        recognizer2.tag = "tap-2"

        recognizer1.actionListener = { it ->
            activity.actionListener.invoke(it)
            assertEquals(State.Ended, it.state)
            latch.countDown()
        }

        recognizer2.actionListener = { it ->
            activity.actionListener.invoke(it)
            assertEquals(State.Ended, it.state)
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer1)
        delegate.addGestureRecognizer(recognizer2)


        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.click())

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(0, latch.count)
    }

    @Test
    fun testSingleTapFailShouldNotBegin() {
        setTitle("Tap")
        delegate.clear()

        val latch = CountDownLatch(1)
        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "tap"
        recognizer.tapsRequired = 1
        recognizer.touchesRequired = 1

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)
            fail()
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)
        delegate.shouldBegin = { false }

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.click())
        latch.await(2, TimeUnit.SECONDS)
        assertEquals(1, latch.count)
    }


    @Test
    fun testSingleTapFailShouldNotReceiveTouch() {
        setTitle("Tap")
        delegate.clear()

        val latch = CountDownLatch(1)
        val recognizer = UITapGestureRecognizer(context)
        recognizer.tag = "tap"
        recognizer.tapsRequired = 1
        recognizer.touchesRequired = 1

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)
            fail()
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)
        delegate.shouldReceiveTouch = { false }

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.click())
        latch.await(2, TimeUnit.SECONDS)
        assertEquals(1, latch.count)
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

    @Test
    fun testRequireFailure() {
        setTitle("Tap Failure")
        val latch = CountDownLatch(1)

        delegate.clear()

        val recognizer1 = UITapGestureRecognizer(context)
        recognizer1.tag = "tap-1"
        recognizer1.tapsRequired = 2

        val recognizer2 = UITapGestureRecognizer(context)
        recognizer2.tag = "tap-2"
        recognizer2.tapsRequired = 1

        recognizer1.actionListener = { it ->
            activity.actionListener.invoke(it)
            fail()
            latch.countDown()
        }

        recognizer2.actionListener = { it ->
            activity.actionListener.invoke(it)
            assertEquals(State.Ended, it.state)
            latch.countDown()
        }

        recognizer2.requireFailureOf = recognizer1

        delegate.addGestureRecognizer(recognizer1)
        delegate.addGestureRecognizer(recognizer2)

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.click())

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(0, latch.count)
    }
}
