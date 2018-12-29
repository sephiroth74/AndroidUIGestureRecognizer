package it.sephiroth.android.library.uigestures

import android.os.SystemClock
import android.view.MotionEvent
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TestUIGestureRecognizerDelegate : TestBase() {

    @Test
    fun testCount() {
        delegate.clear()
        assertSame(0, delegate.size())

        val recognizer = UITapGestureRecognizer(activity)
        delegate.addGestureRecognizer(recognizer)

        assertSame(1, delegate.size())
        assertTrue(delegate.removeGestureRecognizer(recognizer))
        assertSame(0, delegate.size())
    }

    @Test
    fun testRemoveNotAdded() {
        delegate.clear()

        val recognizer = UITapGestureRecognizer(activity)
        assertFalse(delegate.removeGestureRecognizer(recognizer))
    }

    @Test
    fun testRemove() {
        delegate.clear()

        val recognizer = UITapGestureRecognizer(activity)
        delegate.addGestureRecognizer(recognizer)
        assertTrue(delegate.removeGestureRecognizer(recognizer))
    }

    @Test
    fun testClear() {
        delegate.clear()
        val recognizer = UITapGestureRecognizer(activity)
        delegate.addGestureRecognizer(recognizer)
        assertSame(1, delegate.size())
        delegate.clear()
        assertSame(0, delegate.size())
    }

    @Test
    fun testDelegateEmpty() {
        delegate.clear()
        layout.setGestureDelegate(delegate)

        val downTime = SystemClock.uptimeMillis()
        val event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 100f, 100f, 0)
        val result = layout.dispatchTouchEvent(event)
        assertFalse(result)
    }

    @Test
    fun testDisabled() {
        delegate.clear()
        layout.setGestureDelegate(delegate)

        val recognizer = UITapGestureRecognizer(activity)
        delegate.addGestureRecognizer(recognizer)

        delegate.isEnabled = false
        assertFalse(delegate.isEnabled)
        assertFalse(recognizer.isEnabled)

        val downTime = SystemClock.uptimeMillis()
        val event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 100f, 100f, 0)
        val result = layout.dispatchTouchEvent(event)
        assertFalse(result)
    }
}