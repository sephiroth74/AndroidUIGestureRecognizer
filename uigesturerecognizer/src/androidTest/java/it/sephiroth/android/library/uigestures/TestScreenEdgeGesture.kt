package it.sephiroth.android.library.uigestures

import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class TestScreenEdgeGesture : TestBaseClass() {

    var latch: CountDownLatch? = null

    @Before
    fun initLatch() {
        latch = CountDownLatch(3)
    }

    private val actionListener = { it: UIGestureRecognizer ->
        Timber.v("actionListener: $it")

        activityTestRule.activity.actionListener.invoke(it)

        when (it.state) {
            State.Began -> {
                assertEquals(3L, latch?.count)
                latch?.countDown()
            }

            State.Changed -> {
                if (latch?.count == 2L) {
                    latch?.countDown()
                } else {
                    Timber.v("skipping...")
                }
            }

            State.Ended -> {
                assertEquals(1L, latch?.count)
                latch?.countDown()
            }
            else -> {
                Timber.w("State not handled")
            }
        }
    }

    @Test
    fun testSwipeRight() {
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UIScreenEdgePanGestureRecognizer(context)
        recognizer.tag = "swipe-left"
        recognizer.edge = UIRectEdge.LEFT

        recognizer.actionListener = actionListener

        delegate.addGestureRecognizer(recognizer)
        mainView.swipeRight(5)

        latch!!.await(10, TimeUnit.SECONDS)
        assertEquals(0L, latch!!.count)
    }

    @Test
    fun testSwipeLeft() {
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UIScreenEdgePanGestureRecognizer(context)
        recognizer.tag = "swipe-right"
        recognizer.edge = UIRectEdge.RIGTH

        recognizer.actionListener = actionListener

        delegate.addGestureRecognizer(recognizer)
        mainView.swipeLeft(5)

        latch!!.await(10, TimeUnit.SECONDS)
        assertEquals(0L, latch!!.count)
    }

    @Test
    fun testSwipeUp() {
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UIScreenEdgePanGestureRecognizer(context)
        recognizer.tag = "swipe-bottom"
        recognizer.edge = UIRectEdge.BOTTOM

        recognizer.actionListener = actionListener

        delegate.addGestureRecognizer(recognizer)
        mainView.swipeUp(5)

        latch!!.await(10, TimeUnit.SECONDS)
        assertEquals(0L, latch!!.count)
    }
}
