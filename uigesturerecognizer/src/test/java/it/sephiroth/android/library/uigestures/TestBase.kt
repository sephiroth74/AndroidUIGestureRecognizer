package it.sephiroth.android.library.uigestures

import android.app.Activity
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

    @Before
    fun setup() {
        delegate = UIGestureRecognizerDelegate()
        activity = Robolectric.buildActivity(Activity::class.java).create().get()
        layout = FrameLayout(activity.baseContext)

        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        activity.addContentView(layout, params)
    }

}