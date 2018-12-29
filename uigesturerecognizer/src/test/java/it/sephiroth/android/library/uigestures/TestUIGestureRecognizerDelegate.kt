package it.sephiroth.android.library.uigestures

import android.app.Activity
import android.os.SystemClock
import android.view.MotionEvent
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(minSdk = 28)
class TestUIGestureRecognizerDelegate {

    private lateinit var activity: Activity
    private lateinit var layout: FrameLayout
    private lateinit var delegate: UIGestureRecognizerDelegate

    @Before
    fun setup() {
        delegate = UIGestureRecognizerDelegate()
        activity = Robolectric.buildActivity(Activity::class.java).create().get()
        layout = FrameLayout(activity.baseContext)

        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        activity.addContentView(layout, params)
    }

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