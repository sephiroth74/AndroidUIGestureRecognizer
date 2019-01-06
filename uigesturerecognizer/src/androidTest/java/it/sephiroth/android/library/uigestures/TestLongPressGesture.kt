package it.sephiroth.android.library.uigestures

import android.os.SystemClock
import androidx.test.filters.MediumTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@MediumTest
class TestLongPressGesture : TestBaseClass() {

    @Test
    fun test01SingleTapLongPress() {
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
    fun test02SingleTapLongPressFail() {
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
    fun test03DoubleTapLongPress() {
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
    fun test04LongPressAndMove() {
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

//
//    @Test
//    fun test_singleTap1Finger_longPressWithMotion() {
//        setTitle("Long Press 1 touch")
//        val latchBegan = CountDownLatch(1)
//        val latchChanged = CountDownLatch(1)
//        val latchEnd = CountDownLatch(1)
//        val activity = activityTestRule.activity
//        val delegate = activity.delegate
//        assertNotNull(delegate)
//        delegate.clear()
//
//        val recognizer = UILongPressGestureRecognizer(context)
//        recognizer.tag = "long-press"
//        recognizer.touchesRequired = 1
//        recognizer.tapsRequired = 0
//        recognizer.longPressTimeout = ViewConfiguration.getLongPressTimeout().toLong()
//        recognizer.actionListener = { recognizer: UIGestureRecognizer ->
//            Log.v(TAG, "recognizer: $recognizer")
//
//            activityTestRule.activity.actionListener.invoke(recognizer)
//
//            when (recognizer.state) {
//                State.Began -> latchBegan.countDown()
//                State.Changed -> latchChanged.countDown()
//                State.Ended -> latchEnd.countDown()
//            }
//        }
//
//        delegate.addGestureRecognizer(recognizer)
//
//        val pt1 = randomPointOnScreen()
//        val pt2 = randomPointOnScreen()
//
//        interaction.swipe(pt1.x, pt1.y, pt2.x, pt2.y, 5, true, (ViewConfiguration.getLongPressTimeout() * 2.5).toLong())
//
//        latchBegan.await(10, TimeUnit.SECONDS)
//        latchChanged.await(10, TimeUnit.SECONDS)
//        latchEnd.await(10, TimeUnit.SECONDS)
//
//        assertEquals(0L, latchBegan.count)
//        assertEquals(0L, latchChanged.count)
//        assertEquals(0L, latchEnd.count)
//    }
//
//    @Test
//    fun test_2fingers_longPressWithMotion() {
//        setTitle("Long Press 2 touches")
//        val latchBegan = CountDownLatch(1)
//        val latchChanged = CountDownLatch(1)
//        val latchEnd = CountDownLatch(1)
//        val activity = activityTestRule.activity
//        val delegate = activity.delegate
//        assertNotNull(delegate)
//        delegate.clear()
//
//        val recognizer = UILongPressGestureRecognizer(context)
//        recognizer.tag = "long-press"
//        recognizer.touchesRequired = 2
//        recognizer.tapsRequired = 0
//        recognizer.longPressTimeout = 100
//
//
//        recognizer.actionListener = { recognizer: UIGestureRecognizer ->
//            Log.v(TAG, "recognizer: $recognizer")
//
//            activityTestRule.activity.actionListener.invoke(recognizer)
//
//            when (recognizer.state) {
//                State.Began -> latchBegan.countDown()
//                State.Changed -> latchChanged.countDown()
//                State.Ended -> latchEnd.countDown()
//            }
//        }
//
//        delegate.addGestureRecognizer(recognizer)
//
//        var pt1 = randomPointOnScreen()
//        var pt2 = randomPointOnScreen()
//
//        val array = arrayListOf<Array<MotionEvent.PointerCoords>>()
//
//        array.add(arrayOf(
//                PointerCoordsBuilder.newBuilder()
//                    .setCoords(pt1.x.toFloat(), pt1.y.toFloat())
//                    .setSize(1f)
//                    .build(),
//                PointerCoordsBuilder.newBuilder()
//                    .setCoords(pt2.x.toFloat(), pt2.y.toFloat())
//                    .setSize(1f)
//                    .build()))
//
//        for (i in 0..5) {
//            pt1 = randomPointOnScreen()
//            pt2 = randomPointOnScreen()
//            array.add(arrayOf(
//                    PointerCoordsBuilder.newBuilder()
//                        .setCoords(pt1.x.toFloat(), pt1.y.toFloat())
//                        .setSize(1f)
//                        .build(),
//                    PointerCoordsBuilder.newBuilder()
//                        .setPressure(1f)
//                        .setCoords(pt2.x.toFloat(), pt2.y.toFloat())
//                        .setSize(1f)
//                        .build()))
//        }
//
//        interaction.performMultiPointerGesture(array.toTypedArray(), 1000L)
//
//        latchBegan.await(10, TimeUnit.SECONDS)
//        latchChanged.await(10, TimeUnit.SECONDS)
//        latchEnd.await(10, TimeUnit.SECONDS)
//
//        assertEquals(0L, latchBegan.count)
//        assertEquals(0L, latchChanged.count)
//        assertEquals(0L, latchEnd.count)
//    }
//
//    @Test
//    fun test_singleTapLongPress() {
//        setTitle("Long Press simple")
//        val delegate = activityTestRule.activity.delegate
//        val latch = CountDownLatch(2)
//
//        assertNotNull(delegate)
//        delegate.clear()
//
//        val longpressRecognizer = UILongPressGestureRecognizer(context)
//        longpressRecognizer.tag = "long-press"
//
//        longpressRecognizer.touchesRequired = 1
//        longpressRecognizer.tapsRequired = 0
//        longpressRecognizer.minimumPressDuration = 300// set as .5 seconds
//        longpressRecognizer.allowableMovement = 100.toFloat() //move to 100x100
//
//        longpressRecognizer.actionListener = {
//            activityTestRule.activity.actionListener.invoke(it)
//            if (latch.count == 2L) {
//                assertEquals(State.Began, it.state)
//                latch.countDown()
//            } else {
//                assertEquals(State.Ended, it.state)
//                latch.countDown()
//
//            }
//        }
//        delegate.addGestureRecognizer(longpressRecognizer)
//        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.longClick())
//        latch.await(10, TimeUnit.SECONDS)
//
//        assertEquals(0L, latch.count)
//    }
}
