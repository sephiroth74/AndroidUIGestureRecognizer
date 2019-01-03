package it.sephiroth.android.library.uigestures

import android.graphics.PointF
import android.view.MotionEvent
import androidx.test.core.view.PointerCoordsBuilder
import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.math.min

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
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

        val rect = mainView.visibleBounds

        val size = min(rect.height(), rect.width()).toFloat()
        val pt1 = PointF(rect.centerX().toFloat(), (rect.centerY() - (size / 4)))
        val pt2 = PointF(rect.centerX().toFloat(), (rect.centerY() + (size / 4)))
        val pt11 = PointF(pt1.x, pt1.y)
        val pt21 = PointF(pt2.x, pt2.y)

        val center = PointF(rect.centerX().toFloat(), rect.centerY().toFloat())

        Point2D.rotateAroundOrigin(pt11, center, 90f)
        Point2D.rotateAroundOrigin(pt21, center, 90f)

        val array = arrayListOf<Array<MotionEvent.PointerCoords>>()

        array.add(arrayOf(
                PointerCoordsBuilder.newBuilder().setCoords(pt1.x, pt1.y).setSize(1f).build(),
                PointerCoordsBuilder.newBuilder().setCoords(pt2.x, pt2.y).setSize(1f).build()))

        for (i in 1..5) {
            val p1 = Point2D.getLerp(pt1, pt11, i.toFloat() / 5f)
            val p2 = Point2D.getLerp(pt2, pt21, i.toFloat() / 5f)

            array.add(arrayOf(
                    PointerCoordsBuilder.newBuilder().setCoords(p1.x, p1.y).setSize(1f).build(),
                    PointerCoordsBuilder.newBuilder().setCoords(p2.x, p2.y).setSize(1f).build()))
        }

        interaction.performMultiPointerGesture(array.toTypedArray())

        latch.await(10, TimeUnit.SECONDS)
        assertEquals(0L, latch.count)
    }
}
