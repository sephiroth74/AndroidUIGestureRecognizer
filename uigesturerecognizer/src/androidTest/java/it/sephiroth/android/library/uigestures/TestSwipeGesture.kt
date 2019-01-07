package it.sephiroth.android.library.uigestures

import androidx.test.filters.MediumTest
import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
@MediumTest
class TestSwipeGesture : TestBaseClass() {

    private lateinit var latch: CountDownLatch

    private val actionListener = { it: UIGestureRecognizer ->
        Timber.v("actionListener: $it")
        activityTestRule.activity.actionListener.invoke(it)
        assertEquals(State.Ended, it.state)
        latch.countDown()
    }

    @Test
    fun testSwipeRight() {
        setTitle("Swipe Right")

        latch = CountDownLatch(1)
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UISwipeGestureRecognizer(context)
        recognizer.tag = "swipe-right"
        recognizer.numberOfTouchesRequired = 1
        recognizer.direction = UISwipeGestureRecognizer.RIGHT
        recognizer.actionListener = actionListener
        delegate.addGestureRecognizer(recognizer)
        mainView.swipeRight(5)

        latch.await(2, TimeUnit.SECONDS)

        assertEquals(0L, latch.count)
    }

    @Test
    fun testSwipeLeft2Fingers() {
        setTitle("Swipe Left 2 Fingers")

        latch = CountDownLatch(1)
        delegate.clear()

        val recognizer = UISwipeGestureRecognizer(context)
        recognizer.tag = "swipe-left"
        recognizer.numberOfTouchesRequired = 2
        recognizer.direction = UISwipeGestureRecognizer.LEFT
        recognizer.actionListener = actionListener
        delegate.addGestureRecognizer(recognizer)

        interaction.swipeLeftMultiTouch(mainView, 5, 2)

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(0L, latch.count)
    }

    @Test
    fun testSwipeLeftTooManyTouches() {
        setTitle("Swipe Left 2 Fingers")

        latch = CountDownLatch(1)
        delegate.clear()

        val recognizer = UISwipeGestureRecognizer(context)
        recognizer.tag = "swipe-left"
        recognizer.numberOfTouchesRequired = 1
        recognizer.direction = UISwipeGestureRecognizer.LEFT
        recognizer.actionListener = actionListener
        delegate.addGestureRecognizer(recognizer)

        interaction.swipeLeftMultiTouch(mainView, 5, 2)

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(1L, latch.count)
    }

    @Test
    fun testSwipeLeftNotEnoughTouches() {
        setTitle("Swipe Left 2 Fingers")

        latch = CountDownLatch(1)
        delegate.clear()

        val recognizer = UISwipeGestureRecognizer(context)
        recognizer.tag = "swipe-left"
        recognizer.numberOfTouchesRequired = 2
        recognizer.direction = UISwipeGestureRecognizer.LEFT
        recognizer.actionListener = actionListener
        delegate.addGestureRecognizer(recognizer)

        mainView.swipeLeft(5)

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(1L, latch.count)
    }

    @Test
    fun testSwipeRightWrongDirection() {
        setTitle("Swipe Right Wrong Direction")

        latch = CountDownLatch(1)
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UISwipeGestureRecognizer(context)
        recognizer.tag = "swipe-right"
        recognizer.numberOfTouchesRequired = 1
        recognizer.direction = UISwipeGestureRecognizer.RIGHT

        recognizer.actionListener = {
            fail("unexpected")
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)
        mainView.swipeLeft(5)

        latch.await(2, TimeUnit.SECONDS)

        assertEquals(1L, latch.count)
    }

    @Test
    fun testSwipeLeftTooSlow() {
        setTitle("Swipe Left 2 Slow")

        latch = CountDownLatch(1)
        delegate.clear()

        val recognizer = UISwipeGestureRecognizer(context)
        recognizer.tag = "swipe-left"
        recognizer.numberOfTouchesRequired = 1
        recognizer.scaledMinimumFlingVelocity = 4000
        recognizer.direction = UISwipeGestureRecognizer.LEFT

        recognizer.actionListener = {
            fail("unexpected")
        }

        recognizer.stateListener = { it: UIGestureRecognizer, oldState: State?, newState: State? ->
            if (oldState == State.Possible) {
                assertEquals(State.Failed, newState)
                Timber.d("ok, valid!")
                latch.countDown()
            }
        }

        delegate.addGestureRecognizer(recognizer)

        val bounds = mainView.visibleBounds
        val distance = bounds.width() / 8

        interaction.swipe(bounds.centerX() + distance,
                bounds.centerY(),
                bounds.centerX() - distance,
                bounds.centerY(), 80)

        latch.await(3, TimeUnit.SECONDS)
        assertEquals(0L, latch.count)
    }

}
