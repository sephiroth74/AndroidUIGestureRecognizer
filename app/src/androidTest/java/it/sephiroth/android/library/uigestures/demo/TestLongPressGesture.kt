package it.sephiroth.android.library.uigestures.demo

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import it.sephiroth.android.library.uigestures.UILongPressGestureRecognizer
import junit.framework.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class TestLongPressGesture : TestBaseClass() {

    /**
     * To test Single tap-Long press functionality.
     */
    @Test
    fun test_singleTapLongPress() {
        val delegate = activityTestRule.activity.delegate
        val latch = CountDownLatch(2)

        Assert.assertNotNull(delegate)
        delegate.clear()

        val longpressRecognizer = UILongPressGestureRecognizer(context)
        longpressRecognizer.tag = "long-press"

        longpressRecognizer.touchesRequired = 1
        longpressRecognizer.tapsRequired = 0
        longpressRecognizer.minimumPressDuration = 300// set as .5 seconds
        longpressRecognizer.allowableMovement = 100.toFloat() //move to 100x100

        longpressRecognizer.actionListener = {
            if(latch.count == 2L) {
                assertEquals(State.Began, it.state)
                latch.countDown()
            } else {
                assertEquals(State.Ended, it.state)
                latch.countDown()

            }
        }
        delegate.addGestureRecognizer(longpressRecognizer)

        textView.text = "None"

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.longClick())
        latch.await()
    }

}
