package it.sephiroth.android.library.uigestures

import android.view.MotionEvent
import androidx.test.core.view.PointerCoordsBuilder
import it.sephiroth.android.library.uigestures.Interaction.Companion.SWIPE_MARGIN_LIMIT
import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class TestPanGesture : TestBaseClass() {

    @Test
    fun testPanSingleFinger() {
        setTitle("Pan")
        val latch = CountDownLatch(3)
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UIPanGestureRecognizer(context)
        recognizer.tag = "pan"
        recognizer.minimumNumberOfTouches = 1
        recognizer.maximumNumberOfTouches = 1

        recognizer.actionListener = {
            Timber.v("actionListener: $recognizer")

            activityTestRule.activity.actionListener.invoke(it)

            when (recognizer.state) {
                State.Began -> latch.countDown()
                State.Changed -> {
                    if (latch.count == 2L) {
                        latch.countDown()
                    }
                }
                State.Ended -> latch.countDown()
            }
        }

        delegate.addGestureRecognizer(recognizer)

        val rect = mainView.visibleBounds

        interaction.swipe(
                rect.centerX(),
                rect.top + SWIPE_MARGIN_LIMIT,
                rect.centerX(),
                rect.bottom - SWIPE_MARGIN_LIMIT, 10)

        latch.await(10, TimeUnit.SECONDS)
        Assert.assertEquals(0L, latch.count)
    }

    @Test
    fun testPanDoubleFingers() {
        setTitle("Pan 2 fingers")
        val latch = CountDownLatch(3)
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UIPanGestureRecognizer(context)
        recognizer.tag = "pan-double"
        recognizer.minimumNumberOfTouches = 2
        recognizer.maximumNumberOfTouches = 2

        recognizer.actionListener = {
            Timber.v("actionListener: $recognizer")

            activityTestRule.activity.actionListener.invoke(it)

            when (recognizer.state) {
                State.Began -> latch.countDown()
                State.Changed -> {
                    if (latch.count == 2L) {
                        latch.countDown()
                    }
                }
                State.Ended -> latch.countDown()
            }
        }

        delegate.addGestureRecognizer(recognizer)

        var rect = mainView.visibleBounds
        rect.inset(50, 50)

        val distance = rect.bottom - rect.top
        val steps = distance.toFloat() / 20f

        val array = arrayListOf<Array<MotionEvent.PointerCoords>>()

        for (i in 0..10) {
            array.add(arrayOf(
                    PointerCoordsBuilder.newBuilder().setCoords(rect.centerX().toFloat() - 20f, rect.top + (steps * i)).build(),
                    PointerCoordsBuilder.newBuilder().setCoords(rect.centerX().toFloat() + 20f, rect.top + (steps * i)).build()
                             ))
        }

        interaction.performMultiPointerGesture(array.toTypedArray(), 500)
        latch.await(10, TimeUnit.SECONDS)
        Assert.assertEquals(0L, latch.count)
    }
}
