package it.sephiroth.android.library.uigestures

import android.os.SystemClock
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
class TestPinchGesture : TestBaseClass() {

    @Test
    fun testPinchIn() {
        setTitle("Pinch")
        val latch = CountDownLatch(3)
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UIPinchGestureRecognizer(context)
        recognizer.tag = "pinch"

        recognizer.actionListener = {
            Timber.v("actionListener: $it")

            activity.actionListener.invoke(it)

            when (recognizer.state) {
                State.Began -> {
                    assertTrue(latch.count == 3L)
                    latch.countDown()
                }

                State.Changed -> {
                    if (latch.count == 2L) {
                        latch.countDown()
                    }
                }

                State.Ended -> {
                    assertTrue(latch.count == 1L)
                    latch.countDown()
                }

                else -> {}
            }

            Timber.v("latch: ${latch.count}")
        }

        delegate.addGestureRecognizer(recognizer)

        mainView.pinchOut(50, 4)

        latch.await(2, TimeUnit.SECONDS)

        assertEquals(0L, latch.count)
    }

    @Test
    fun testPinchInQuickScale() {
        setTitle("Pinch")
        val latch = CountDownLatch(3)
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UIPinchGestureRecognizer(context)
        recognizer.tag = "pinch"
        recognizer.isQuickScaleEnabled = true

        recognizer.actionListener = {
            Timber.v("actionListener: $it")

            activity.actionListener.invoke(it)

            when (recognizer.state) {
                State.Began -> {
                    assertTrue(latch.count == 3L)
                    latch.countDown()
                }

                State.Changed -> {
                    if (latch.count == 2L) {
                        latch.countDown()
                    }
                }

                State.Ended -> {
                    assertTrue(latch.count == 1L)
                    latch.countDown()
                }

                else -> {}
            }

            Timber.v("latch: ${latch.count}")
        }

        delegate.addGestureRecognizer(recognizer)


        val bounds = mainView.visibleBounds
        val x = bounds.centerX()
        val y = bounds.centerY()

        interaction.touchDown(x, y)
        SystemClock.sleep(Interaction.REGULAR_CLICK_LENGTH)

        interaction.touchUp(x, y)
        SystemClock.sleep(Interaction.REGULAR_CLICK_LENGTH / 2)

        interaction.touchDown(x, y)
        SystemClock.sleep(Interaction.REGULAR_CLICK_LENGTH)

        val distance = ((bounds.bottom - Interaction.SWIPE_MARGIN_LIMIT) - bounds.centerY()).toFloat()
        val steps = 4
        val step = distance / steps

        for (i in 1..steps) {
            interaction.touchMove(x, (y + (step * i)).toInt())
            SystemClock.sleep(Interaction.MOTION_EVENT_INJECTION_DELAY_MILLIS.toLong())
        }

        interaction.touchUp(x, bounds.bottom - Interaction.SWIPE_MARGIN_LIMIT)

        latch.await(2, TimeUnit.SECONDS)

        assertEquals(0L, latch.count)
    }
}
