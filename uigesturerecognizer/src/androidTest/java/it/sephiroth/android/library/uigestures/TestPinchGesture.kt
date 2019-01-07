package it.sephiroth.android.library.uigestures

import androidx.test.filters.SmallTest
import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
@SmallTest
class TestPinchGesture : TestBaseClass() {

    @Test
    fun testPinchIn() {
        setTitle("Pinch")
        val latch = CountDownLatch(3)
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UIPinchGestureRecognizer(context)
        recognizer.tag = "pinch"

        recognizer.actionListener = {
            Timber.v("actionListener: $it")

            activity.actionListener.invoke(it)

            when (recognizer.state) {
                State.Began -> {
                    assertTrue(latch.count == 3L)
                    latch.countDown()
                }

                State.Changed -> {
                    if (latch.count == 2L) {
                        latch.countDown()
                    }
                }

                State.Ended -> {
                    assertTrue(latch.count == 1L)
                    latch.countDown()
                }
            }

            Timber.v("latch: ${latch.count}")
        }

        delegate.addGestureRecognizer(recognizer)

        mainView.pinchOut(50, 4)

        latch.await(2, TimeUnit.SECONDS)

        assertEquals(0L, latch.count)
    }
}
