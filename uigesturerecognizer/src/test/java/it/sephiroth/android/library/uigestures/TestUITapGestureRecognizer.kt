package it.sephiroth.android.library.uigestures

import android.os.SystemClock
import android.view.MotionEvent
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

        val downTime = SystemClock.uptimeMillis()

        var event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 100f, 100f, 0)
        var result = layout.dispatchTouchEvent(event)
        assertTrue(result)

        SystemClock.sleep(recognizer.tapTimeout / 2)
        event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 100f, 100f, 0)
        result = layout.dispatchTouchEvent(event)
        assertTrue(result)

        latch.await()
    }

}