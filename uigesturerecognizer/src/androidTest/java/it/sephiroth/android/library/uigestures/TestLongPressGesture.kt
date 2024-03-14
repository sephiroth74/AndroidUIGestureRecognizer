package it.sephiroth.android.library.uigestures

import android.os.SystemClock
import android.view.MotionEvent
import androidx.test.core.view.PointerCoordsBuilder
import androidx.test.filters.MediumTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
@MediumTest
class TestLongPressGesture : TestBaseClass() {

    @Test
    fun testSingleTapLongPress() {
        setTitle("Long Press 1")

        val latch = CountDownLatch(2)

        delegate.clear()

        val recognizer = UILongPressGestureRecognizer(context)
        recognizer.tag = "long-press"
        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)

            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)

        mainView.longClick()

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(0, latch.count)
    }

    @Test
    fun testSingleTapLongPressMovedTooMuch() {
        setTitle("Long Press 1 Fail")

        val latch = CountDownLatch(1)
        val bounds = mainView.visibleBounds

        delegate.clear()

        val recognizer = UILongPressGestureRecognizer(context)
        recognizer.tag = "long-press"
        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)
            fail("unexpected")
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)

        interaction.touchDown(bounds.centerX(), bounds.centerY())
        interaction.touchMove(bounds.centerX() + recognizer.allowableMovement, bounds.centerY() + recognizer.allowableMovement)
        SystemClock.sleep(recognizer.longPressTimeout)
        interaction.touchUp(bounds.centerX(), bounds.centerY())

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(1, latch.count)
    }

    @Test
    fun testSingleTapLongPressMovedButNotTooMuch() {
        setTitle("Long Press 1 Move")

        val latch = CountDownLatch(1)
        val bounds = mainView.visibleBounds

        delegate.clear()

        val recognizer = UILongPressGestureRecognizer(context)
        recognizer.tag = "long-press"
        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)
            if (it.inState(UIGestureRecognizer.State.Ended)) {
                latch.countDown()
            }
        }

        delegate.addGestureRecognizer(recognizer)

        interaction.touchDown(bounds.centerX(), bounds.centerY())
        interaction.touchMove(bounds.centerX() + recognizer.allowableMovement / 3, bounds.centerY() + recognizer.allowableMovement / 3)
        SystemClock.sleep(recognizer.longPressTimeout)
        interaction.touchUp(bounds.centerX(), bounds.centerY())

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(0, latch.count)
    }

    @Test
    fun testSingleTapLongPressFail() {
        setTitle("Long Press 1")

        val latch = CountDownLatch(1)

        delegate.clear()

        val recognizer = UILongPressGestureRecognizer(context)
        recognizer.tag = "long-press"
        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)
            fail("not expected")
        }

        delegate.addGestureRecognizer(recognizer)

        mainView.click()

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(1, latch.count)
    }

    @Test
    fun testDoubleTapLongPress() {
        setTitle("Long Press Double Tap")

        val latch = CountDownLatch(2)
        val bounds = mainView.visibleBounds

        delegate.clear()

        val recognizer = UILongPressGestureRecognizer(context)
        recognizer.tag = "long-press"
        recognizer.tapsRequired = 2

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)

        interaction.touchDown(bounds.centerX(), bounds.centerY())
        SystemClock.sleep(Interaction.REGULAR_CLICK_LENGTH)

        interaction.touchUp(bounds.centerX(), bounds.centerY())
        SystemClock.sleep(Interaction.REGULAR_CLICK_LENGTH)

        interaction.longTapNoSync(bounds.centerX(), bounds.centerY(), recognizer.longPressTimeout * 2)

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(0, latch.count)
    }

    @Test
    fun testDoubleTapTooFar2Tap() {
        setTitle("Long Press Double Tap")

        val latch = CountDownLatch(1)
        val bounds = mainView.visibleBounds

        delegate.clear()

        val recognizer = UILongPressGestureRecognizer(context)
        recognizer.tag = "long-press"
        recognizer.tapsRequired = 2

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)
            fail("unexpected")
        }

        delegate.addGestureRecognizer(recognizer)

        val x = bounds.left + Interaction.SWIPE_MARGIN_LIMIT
        val y = bounds.centerY()

        interaction.touchDown(x, y)
        SystemClock.sleep(Interaction.REGULAR_CLICK_LENGTH)

        interaction.touchUp(x, y)
        SystemClock.sleep(Interaction.REGULAR_CLICK_LENGTH)

        interaction.longTapNoSync(x + recognizer.scaledDoubleTapSlop + 10, y, recognizer.longPressTimeout * 2)

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(1, latch.count)
    }

    @Test
    fun testLongPressAndMove() {
        setTitle("Long Press Move")

        val latch = CountDownLatch(3)
        val bounds = mainView.visibleBounds

        delegate.clear()

        val recognizer = UILongPressGestureRecognizer(context)
        recognizer.tag = "long-press"
        recognizer.tapsRequired = 1

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)

            when (it.state) {
                UIGestureRecognizer.State.Began -> {
                    assertEquals(3L, latch.count)
                    latch.countDown()
                }
                UIGestureRecognizer.State.Changed -> {
                    if (latch.count == 2L) {
                        latch.countDown()
                    }
                }
                UIGestureRecognizer.State.Ended -> {
                    assertEquals(1L, latch.count)
                    latch.countDown()
                }

                else -> {
                    // empty
                }
            }

        }

        delegate.addGestureRecognizer(recognizer)

        interaction.touchDown(bounds.centerX(), bounds.centerY())
        SystemClock.sleep(recognizer.longPressTimeout * 2)

        interaction.touchMove(bounds.left + Interaction.SWIPE_MARGIN_LIMIT, bounds.centerY())
        interaction.touchUp(bounds.centerX(), bounds.centerY())

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(0, latch.count)
    }

    @Test
    fun testSingle2Fingers() {
        setTitle("Long 2 Fingers")

        val latch = CountDownLatch(3)
        val bounds = mainView.visibleBounds

        delegate.clear()

        val recognizer = UILongPressGestureRecognizer(context)
        recognizer.tag = "long-press"
        recognizer.tapsRequired = 1
        recognizer.touchesRequired = 2

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)

            when (it.state) {
                UIGestureRecognizer.State.Began -> {
                    assertEquals(3L, latch.count)
                    latch.countDown()
                }
                UIGestureRecognizer.State.Changed -> {
                    if (latch.count == 2L) {
                        latch.countDown()
                    }
                }
                UIGestureRecognizer.State.Ended -> {
                    assertEquals(1L, latch.count)
                    latch.countDown()
                }

                else -> {
                    // empty
                }
            }

        }

        delegate.addGestureRecognizer(recognizer)

        val array = arrayListOf<Array<MotionEvent.PointerCoords>>()

        var x = (bounds.right - Interaction.SWIPE_MARGIN_LIMIT).toFloat()
        val y = bounds.centerY().toFloat()

        // down
        array.add(arrayOf(
                PointerCoordsBuilder.newBuilder().setCoords(x, y).build(),
                PointerCoordsBuilder.newBuilder().setCoords(x, y + Interaction.SWIPE_MARGIN_LIMIT).build()
        ))

        // move
        x = bounds.centerX().toFloat()
        array.add(arrayOf(
                PointerCoordsBuilder.newBuilder().setCoords(x, y).build(),
                PointerCoordsBuilder.newBuilder().setCoords(x, y + Interaction.SWIPE_MARGIN_LIMIT).build()
        ))

        // up
        x = (bounds.left + Interaction.SWIPE_MARGIN_LIMIT).toFloat()
        array.add(arrayOf(
                PointerCoordsBuilder.newBuilder().setCoords(x, y).build(),
                PointerCoordsBuilder.newBuilder().setCoords(x, y + Interaction.SWIPE_MARGIN_LIMIT).build()
        ))

        interaction.performMultiPointerGesture(array.toTypedArray(), recognizer.longPressTimeout + 16)

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(0, latch.count)
    }

    @Test
    fun testDoubleTap2Fingers() {
        setTitle("Long 2 Fingers")

        val latch = CountDownLatch(2)
        val bounds = mainView.visibleBounds

        delegate.clear()

        val recognizer = UILongPressGestureRecognizer(context)
        recognizer.tag = "long-press"
        recognizer.tapsRequired = 2
        recognizer.touchesRequired = 2

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)

            when (it.state) {
                UIGestureRecognizer.State.Began -> {
                    assertEquals(2L, latch.count)
                    latch.countDown()
                }
                UIGestureRecognizer.State.Changed -> {
                    fail("unexpected")
                }
                UIGestureRecognizer.State.Ended -> {
                    assertEquals(1L, latch.count)
                    latch.countDown()
                }

                else -> {
                    // empty
                }
            }

        }

        delegate.addGestureRecognizer(recognizer)

        val array = arrayListOf<Array<MotionEvent.PointerCoords>>()

        var x = bounds.centerX().toFloat()
        val y = bounds.centerY().toFloat()

        // down
        array.add(arrayOf(
                PointerCoordsBuilder.newBuilder().setCoords(x, y).build(),
                PointerCoordsBuilder.newBuilder().setCoords(x, y + Interaction.SWIPE_MARGIN_LIMIT).build()
        ))

        // up
        array.add(arrayOf(
                PointerCoordsBuilder.newBuilder().setCoords(x, y).build(),
                PointerCoordsBuilder.newBuilder().setCoords(x, y + Interaction.SWIPE_MARGIN_LIMIT).build()
        ))

        interaction.performMultiPointerGesture(array.toTypedArray())

        SystemClock.sleep(Interaction.REGULAR_CLICK_LENGTH / 2)

        interaction.performMultiPointerGesture(array.toTypedArray(), 5, recognizer.longPressTimeout + 16)

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(0, latch.count)
    }

    @Test
    fun testDoubleTap2FingersTooFar() {
        setTitle("Long 2 Fingers Fail")

        val latch = CountDownLatch(1)
        val bounds = mainView.visibleBounds

        delegate.clear()

        val recognizer = UILongPressGestureRecognizer(context)
        recognizer.tag = "long-press"
        recognizer.tapsRequired = 2
        recognizer.touchesRequired = 2

        recognizer.actionListener = { it ->
            activity.actionListener.invoke(it)
            fail("unexpected")
            latch.countDown()
        }

        delegate.addGestureRecognizer(recognizer)

        val array = arrayListOf<Array<MotionEvent.PointerCoords>>()

        var x = bounds.centerX().toFloat()
        var y = bounds.centerY().toFloat()

        // down
        array.add(arrayOf(
                PointerCoordsBuilder.newBuilder().setCoords(x, y).build(),
                PointerCoordsBuilder.newBuilder().setCoords(x, y + Interaction.SWIPE_MARGIN_LIMIT).build()
        ))

        // up
        array.add(arrayOf(
                PointerCoordsBuilder.newBuilder().setCoords(x, y).build(),
                PointerCoordsBuilder.newBuilder().setCoords(x, y + Interaction.SWIPE_MARGIN_LIMIT).build()
        ))

        interaction.performMultiPointerGesture(array.toTypedArray())
        SystemClock.sleep(Interaction.REGULAR_CLICK_LENGTH / 2)


        array.clear()

        x = bounds.centerX().toFloat() + recognizer.scaledDoubleTapSlop
        y = bounds.centerY().toFloat() + recognizer.scaledDoubleTapSlop

        // down
        array.add(arrayOf(
                PointerCoordsBuilder.newBuilder().setCoords(x, y).build(),
                PointerCoordsBuilder.newBuilder().setCoords(x, y + Interaction.SWIPE_MARGIN_LIMIT).build()
        ))

        // up
        array.add(arrayOf(
                PointerCoordsBuilder.newBuilder().setCoords(x, y).build(),
                PointerCoordsBuilder.newBuilder().setCoords(x, y + Interaction.SWIPE_MARGIN_LIMIT).build()
        ))

        interaction.performMultiPointerGesture(array.toTypedArray(), 5, recognizer.longPressTimeout + 16)

        latch.await(2, TimeUnit.SECONDS)
        assertEquals(1, latch.count)
    }
}
