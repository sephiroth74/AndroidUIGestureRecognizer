package it.sephiroth.android.library.uigestures

import android.app.Activity
import android.os.SystemClock
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import org.junit.Before
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

@Config(minSdk = 28)
open class TestBase {
    lateinit var activity: Activity
    lateinit var layout: FrameLayout
    lateinit var delegate: UIGestureRecognizerDelegate

    val mDownTime = SystemClock.uptimeMillis()

    @Before
    fun setup() {
        delegate = UIGestureRecognizerDelegate()
        activity = Robolectric.buildActivity(Activity::class.java).create().get()
        layout = FrameLayout(activity.baseContext)

        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        activity.addContentView(layout, params)
    }


    fun getMotionEvent(action: Int, x: Float, y: Float): MotionEvent {
        return MotionEvent.obtain(mDownTime, SystemClock.uptimeMillis(), action, x, y, 0)
    }

}