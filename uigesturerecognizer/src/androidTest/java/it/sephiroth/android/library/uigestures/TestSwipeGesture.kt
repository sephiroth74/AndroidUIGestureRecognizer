package it.sephiroth.android.library.uigestures

import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.concurrent.CountDownLatch

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class TestSwipeGesture : TestBaseClass() {

    @Test
    fun testSwipeRight() {
        val latch = CountDownLatch(1)
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UISwipeGestureRecognizer(context)
        recognizer.tag = "swipe"
        recognizer.numberOfTouchesRequired = 1
        recognizer.direction = UISwipeGestureRecognizer.RIGHT

        recognizer.actionListener = {
            Timber.v("actionListener: $it")

            activityTestRule.activity.actionListener.invoke(it)
            assertEquals(State.Ended, it.state)

            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)
        mainView.swipeRight(3)

        latch.await()
    }

    @Test
    fun testSwipeLeft() {
        val latch = CountDownLatch(1)
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UISwipeGestureRecognizer(context)
        recognizer.tag = "swipe"
        recognizer.numberOfTouchesRequired = 1
        recognizer.direction = UISwipeGestureRecognizer.LEFT

        recognizer.actionListener = {
            Timber.v("actionListener: $it")

            activityTestRule.activity.actionListener.invoke(it)
            assertEquals(State.Ended, it.state)

            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)
        mainView.swipeLeft(3)

        latch.await()
    }

    @Test
    fun testSwipeUp() {
        val latch = CountDownLatch(1)
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UISwipeGestureRecognizer(context)
        recognizer.tag = "swipe"
        recognizer.numberOfTouchesRequired = 1
        recognizer.direction = UISwipeGestureRecognizer.UP

        recognizer.actionListener = {
            Timber.v("actionListener: $it")

            activityTestRule.activity.actionListener.invoke(it)
            assertEquals(State.Ended, it.state)

            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)
        mainView.swipeUp(3)

        latch.await()
    }

    @Test
    fun testSwipeDown() {
        val latch = CountDownLatch(1)
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UISwipeGestureRecognizer(context)
        recognizer.tag = "swipe"
        recognizer.numberOfTouchesRequired = 1
        recognizer.direction = UISwipeGestureRecognizer.DOWN

        recognizer.actionListener = {
            Timber.v("actionListener: $it")

            activityTestRule.activity.actionListener.invoke(it)
            assertEquals(State.Ended, it.state)

            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)
        mainView.swipeDown(3)

        latch.await()
    }

    @Test
    fun testFailSwipe() {
        val latch = CountDownLatch(1)
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UISwipeGestureRecognizer(context)
        recognizer.tag = "swipe"
        recognizer.numberOfTouchesRequired = 1
        recognizer.direction = UISwipeGestureRecognizer.UP

        recognizer.stateListener = { it ->
            Timber.i("onStateChanged: ${it.state}")
            if (State.Failed == it.state) {
                latch.countDown()
            }

        }

        delegate.addGestureRecognizer(recognizer)
        mainView.swipeDown(3)

        latch.await()
    }

}
