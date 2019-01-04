package it.sephiroth.android.library.uigestures

import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
        mainView.swipeRight(3)

        latch.await(10, TimeUnit.SECONDS)
        assertEquals(0L, latch.count)
    }
//
//    @Test
//    fun testSwipeLeft() {
//        setTitle("Swipe Left")
//        latch = CountDownLatch(1)
//        assertNotNull(delegate)
//        delegate.clear()
//
//        val recognizer = UISwipeGestureRecognizer(context)
//        recognizer.tag = "swipe-left"
//        recognizer.numberOfTouchesRequired = 1
//        recognizer.direction = UISwipeGestureRecognizer.LEFT
//        recognizer.actionListener = actionListener
//        delegate.addGestureRecognizer(recognizer)
//        mainView.swipeLeft(3)
//
//        latch.await(10, TimeUnit.SECONDS)
//        assertEquals(0L, latch.count)
//    }
//
//    //    @Test
//    fun testSwipeUp() {
//        setTitle("Swipe Up")
//        latch = CountDownLatch(1)
//        assertNotNull(delegate)
//        delegate.clear()
//
//        val recognizer = UISwipeGestureRecognizer(context)
//        recognizer.tag = "swipe-up"
//        recognizer.numberOfTouchesRequired = 1
//        recognizer.direction = UISwipeGestureRecognizer.UP
//        recognizer.actionListener = actionListener
//        delegate.addGestureRecognizer(recognizer)
//        mainView.swipeUp(3)
//
//        latch.await(10, TimeUnit.SECONDS)
//        assertEquals(0L, latch.count)
//    }
//
//    //    @Test
//    fun testSwipeDown() {
//        setTitle("Swipe Down")
//        latch = CountDownLatch(1)
//        assertNotNull(delegate)
//        delegate.clear()
//
//        val recognizer = UISwipeGestureRecognizer(context)
//        recognizer.tag = "swipe-down"
//        recognizer.numberOfTouchesRequired = 1
//        recognizer.direction = UISwipeGestureRecognizer.DOWN
//        recognizer.actionListener = actionListener
//        delegate.addGestureRecognizer(recognizer)
//
//        mainView.swipeDown(3)
//        latch.await(10, TimeUnit.SECONDS)
//        assertEquals(0L, latch.count)
//    }
//
//    //    @Test
//    fun testSwipeLeft2Fingers() {
//        setTitle("Swipe Left 2 Fingers")
//        latch = CountDownLatch(1)
//        assertNotNull(delegate)
//        delegate.clear()
//
//        val recognizer = UISwipeGestureRecognizer(context)
//        recognizer.numberOfTouchesRequired = 2
//        recognizer.tag = "swipe-left2"
//        recognizer.direction = UISwipeGestureRecognizer.LEFT
//        recognizer.actionListener = actionListener
//        delegate.addGestureRecognizer(recognizer)
//
//        interaction.swipeLeftMultiTouch(mainView, 4, 2)
//        latch.await(10, TimeUnit.SECONDS)
//        assertEquals(0L, latch.count)
//    }
//
//    //    @Test
//    fun testSwipeLeft2FingersFail() {
//        setTitle("Swipe Left 2 Fingers")
//        latch = CountDownLatch(1)
//        assertNotNull(delegate)
//        delegate.clear()
//
//        val recognizer = UISwipeGestureRecognizer(context)
//        recognizer.numberOfTouchesRequired = 2
//        recognizer.tag = "swipe-left2"
//        recognizer.direction = UISwipeGestureRecognizer.UP
//        recognizer.actionListener = actionListener
//        delegate.addGestureRecognizer(recognizer)
//
//        interaction.swipeLeftMultiTouch(mainView, 4, 2)
//        latch.await(2, TimeUnit.SECONDS)
//        assertEquals(1L, latch.count)
//    }
//
//    //    @Test
//    fun testSwipeLeft2FingersFailSingleFinger() {
//        setTitle("Swipe Left 2 Fingers")
//        latch = CountDownLatch(1)
//        assertNotNull(delegate)
//        delegate.clear()
//
//        val recognizer = UISwipeGestureRecognizer(context)
//        recognizer.numberOfTouchesRequired = 2
//        recognizer.tag = "swipe-left2"
//        recognizer.direction = UISwipeGestureRecognizer.UP
//        recognizer.actionListener = actionListener
//        delegate.addGestureRecognizer(recognizer)
//
//        mainView.swipeLeft(4)
//        latch.await(2, TimeUnit.SECONDS)
//        assertEquals(1L, latch.count)
//    }
//
//    //    @Test
//    fun testFailSwipe() {
//        setTitle("Fail Swipe")
//        val latch = CountDownLatch(1)
//        assertNotNull(delegate)
//        delegate.clear()
//
//        val recognizer = UISwipeGestureRecognizer(context)
//        recognizer.tag = "swipe-up"
//        recognizer.numberOfTouchesRequired = 1
//        recognizer.direction = UISwipeGestureRecognizer.UP
//
//        recognizer.stateListener = { it ->
//            Timber.i("onStateChanged: ${it.state}")
//            if (State.Failed == it.state) {
//                latch.countDown()
//            }
//        }
//
//        delegate.addGestureRecognizer(recognizer)
//        mainView.swipeDown(3)
//
//        latch.await(10, TimeUnit.SECONDS)
//        assertEquals(0L, latch.count)
//    }
}
