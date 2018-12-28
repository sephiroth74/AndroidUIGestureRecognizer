package it.sephiroth.android.library.uigestures.demo

import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import it.sephiroth.android.library.uigestures.UIPinchGestureRecognizer
import org.hamcrest.Matchers
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.concurrent.CountDownLatch

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class TestPinchGesture : TestBaseClass() {

    @Test
    fun testPinchIn() {
        val latch = CountDownLatch(3)
        assertNotNull(delegate)
        delegate.clear()

        val recognizer = UIPinchGestureRecognizer(context)
        recognizer.tag = "pinch"
        recognizer.isQuickScaleEnabled = true

        recognizer.actionListener = {
            activityTestRule.activity.actionListener.invoke(recognizer)
            Timber.v("actionListener: $recognizer")

            when (recognizer.state) {
                State.Began -> {
                    latch.countDown()
                }
                State.Changed -> {
                    if (latch.count == 2L) {
                        latch.countDown()
                    }
                }
                State.Ended -> {
                    assertThat(latch.count, Matchers.`is`(1L))
                    latch.countDown()
                }
            }

            Timber.v("latch: ${latch.count}")
        }

        delegate.addGestureRecognizer(recognizer)

        mainView.pinchIn(50, 20)
        latch.await()
    }
}
