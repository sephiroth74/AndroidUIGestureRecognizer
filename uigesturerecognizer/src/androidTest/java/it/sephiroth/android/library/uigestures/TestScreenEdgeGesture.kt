package it.sephiroth.android.library.uigestures

import androidx.test.filters.SmallTest
import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
@SmallTest
class TestScreenEdgeGesture : TestBaseClass() {

    private lateinit var latch: CountDownLatch

    @Before
    fun initLatch() {
        latch = CountDownLatch(3)
    }

    private val actionListener = { it: UIGestureRecognizer ->
        Timber.v("actionListener: $it")

        activityTestRule.activity.actionListener.invoke(it)

        when (it.state) {
            State.Began -> {
                assertEquals(3L, latch.count)
                latch.countDown()
            }

            State.Changed -> {
                if (latch.count == 2L) {
                    latch.countDown()
                } else {
                    Timber.v("skipping...")
                }
            }

            State.Ended -> {
                assertEquals(1L, latch.count)
                latch.countDown()
            }
            else -> {
                Timber.w("State not handled")
            }
        }
    }

    @Test
    fun testSwipeRight() {
        setTitle("Edge Left")
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UIScreenEdgePanGestureRecognizer(context)
        recognizer.tag = "edge-left"
        recognizer.edge = UIRectEdge.LEFT

        recognizer.actionListener = actionListener

        delegate.addGestureRecognizer(recognizer)
        mainView.swipeRight(5)

        latch.await(10, TimeUnit.SECONDS)
        assertEquals(0L, latch.count)
    }

    @Test
    fun testSwipeLeft() {
        setTitle("Edge Right")
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UIScreenEdgePanGestureRecognizer(context)
        recognizer.tag = "edge-right"
        recognizer.edge = UIRectEdge.RIGTH

        recognizer.actionListener = actionListener

        delegate.addGestureRecognizer(recognizer)
        mainView.swipeLeft(5)

        latch.await(10, TimeUnit.SECONDS)
        assertEquals(0L, latch.count)
    }

    @Test
    fun testSwipeUp() {
        setTitle("Edge Bottom")
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UIScreenEdgePanGestureRecognizer(context)
        recognizer.tag = "edge-bottom"
        recognizer.edge = UIRectEdge.BOTTOM

        recognizer.actionListener = actionListener

        delegate.addGestureRecognizer(recognizer)
        mainView.swipeUp(5)

        latch.await(10, TimeUnit.SECONDS)
        assertEquals(0L, latch.count)
    }
}
