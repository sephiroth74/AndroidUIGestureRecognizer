package it.sephiroth.android.library.uigestures

import android.os.SystemClock
import android.view.MotionEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class TestUITapGestureRecognizer : TestBase() {

    @Test
    fun testTap() {
        val latch = CountDownLatch(1)
        delegate.clear()
        val recognizer = UITapGestureRecognizer(activity)
        recognizer.actionListener = { r ->
            System.out.println("actionListener: $r")
            if (r.state == UIGestureRecognizer.State.Ended) {
                latch.countDown()
            }
        }

        delegate.addGestureRecognizer(recognizer)
        layout.setGestureDelegate(delegate)

        var event = getMotionEvent(MotionEvent.ACTION_DOWN, 100f, 100f)
        var result = layout.dispatchTouchEvent(event)
        assertTrue(result)

        SystemClock.sleep(recognizer.tapTimeout / 2)
        event = getMotionEvent(MotionEvent.ACTION_UP, 100f, 100f)
        result = layout.dispatchTouchEvent(event)
        assertTrue(result)
        latch.await()
    }

    @Test
    fun testDoubleTap() {
        val latch = CountDownLatch(1)

        val recognizer = UITapGestureRecognizer(activity)
        recognizer.tapsRequired = 2
        recognizer.touchesRequired = 1
        recognizer.actionListener = { r ->
            System.out.println("recognizer: $r")
            assertEquals(1, latch.count)
            assertEquals(UIGestureRecognizer.State.Ended, r.state)
            latch.countDown()
        }

        delegate.clear()
        delegate.addGestureRecognizer(recognizer)

        layout.setGestureDelegate(delegate)

        layout.dispatchTouchEvent(getMotionEvent(MotionEvent.ACTION_DOWN, 100f, 100f))
        SystemClock.sleep(recognizer.tapTimeout / 2)
        layout.dispatchTouchEvent(getMotionEvent(MotionEvent.ACTION_UP, 100f, 100f))
        SystemClock.sleep(recognizer.tapTimeout / 2)
        layout.dispatchTouchEvent(getMotionEvent(MotionEvent.ACTION_DOWN, 100f, 100f))
        SystemClock.sleep(recognizer.tapTimeout / 2)
        layout.dispatchTouchEvent(getMotionEvent(MotionEvent.ACTION_UP, 100f, 100f))

        latch.await()

    }

}