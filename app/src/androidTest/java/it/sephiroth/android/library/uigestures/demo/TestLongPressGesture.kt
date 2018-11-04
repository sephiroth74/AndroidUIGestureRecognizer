package it.sephiroth.android.library.uigestures.demo

import android.os.SystemClock
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.filters.SdkSuppress
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiObjectNotFoundException
import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import it.sephiroth.android.library.uigestures.UILongPressGestureRecognizer
import junit.framework.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 18)
class TestLongPressGesture : TestBaseClass() {

    /**
     * To test Single tap-Long press functionality.
     */
    @Test
    @Throws(UiObjectNotFoundException::class, InterruptedException::class)
    fun test_singleTapLongPress() {
        val delegate = activityTestRule.activity.delegate

        Assert.assertNotNull(delegate)
        delegate.clear()

        val longpressRecognizer = UILongPressGestureRecognizer(context)
        longpressRecognizer.tag = "long-press"

        longpressRecognizer.touchesRequired = 1
        longpressRecognizer.tapsRequired = 0
        longpressRecognizer.minimumPressDuration = 300// set as .5 seconds
        longpressRecognizer.allowableMovement = 100.toFloat() //move to 100x100

        longpressRecognizer.actionListener = activityTestRule.activity
        delegate.addGestureRecognizer(longpressRecognizer)

        titleView.text = "1 Tap"
        textView.text = "None"

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.longClick())
        SystemClock.sleep(200)

        assertEquals(longpressRecognizer.tag as String + ": " + State.Ended, textView.text)
    }

}
