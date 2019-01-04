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
class TestRotateGesture : TestBaseClass() {

    @Test
    fun testRotate() {
        setTitle("Rotate")
        val latch = CountDownLatch(3)
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UIRotateGestureRecognizer(context)
        recognizer.tag = "rotate"

        recognizer.actionListener = {
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
                    }

                    Timber.v("rotation: ${recognizer.rotationInRadians}")
                    Timber.v("velocity: ${recognizer.velocity}")

                    assertNotEquals(0.0, recognizer.rotationInRadians)
                }

                State.Ended -> {
                    assertEquals(1L, latch.count)
                    latch.countDown()

                }
            }
        }

        delegate.addGestureRecognizer(recognizer)

        interaction.rotate(mainView, 90F, 7)

        latch.await(10, TimeUnit.SECONDS)
        assertEquals(0L, latch.count)
    }
}
