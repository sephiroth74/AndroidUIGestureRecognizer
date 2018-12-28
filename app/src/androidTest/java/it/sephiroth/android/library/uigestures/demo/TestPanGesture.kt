package it.sephiroth.android.library.uigestures.demo

import android.view.MotionEvent
import androidx.test.core.view.PointerCoordsBuilder
import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import it.sephiroth.android.library.uigestures.UIPanGestureRecognizer
import it.sephiroth.android.library.uigestures.demo.Interaction.Companion.SWIPE_MARGIN_LIMIT
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.concurrent.CountDownLatch

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class TestPanGesture : TestBaseClass() {

    @Test
    fun testPanSingleFinger() {
        val latch = CountDownLatch(3)
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UIPanGestureRecognizer(context)
        recognizer.tag = "pan"
        recognizer.minimumNumberOfTouches = 1
        recognizer.maximumNumberOfTouches = 1

        recognizer.actionListener = {
            activityTestRule.activity.actionListener.invoke(recognizer)
            Timber.v("actionListener: $recognizer")

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

        latch.await()
    }

    @Test
    fun testPanDoubleFingers() {
        val latch = CountDownLatch(3)
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UIPanGestureRecognizer(context)
        recognizer.tag = "pan"
        recognizer.minimumNumberOfTouches = 2
        recognizer.maximumNumberOfTouches = 2

        recognizer.actionListener = {
            activityTestRule.activity.actionListener.invoke(recognizer)
            Timber.v("actionListener: $recognizer")

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

        interaction.performMultiPointerGesture(array.toTypedArray())
        latch.await()
    }
}
