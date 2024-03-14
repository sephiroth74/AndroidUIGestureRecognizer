package it.sephiroth.android.library.uigestures

import android.app.UiAutomation
import android.graphics.Point
import android.graphics.PointF
import android.os.SystemClock
import android.util.Log
import android.view.InputDevice
import android.view.InputEvent
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.PointerCoords
import android.view.ViewConfiguration
import android.view.accessibility.AccessibilityEvent
import androidx.test.core.view.PointerCoordsBuilder
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.Configurator
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObjectNotFoundException
import timber.log.Timber
import java.util.concurrent.TimeoutException
import kotlin.math.min


class Interaction {
    private var mDownTime: Long = 0

    fun clickNoSync(x: Int, y: Int, tapTimeout: Long = REGULAR_CLICK_LENGTH): Boolean {
        Log.d(LOG_TAG, "clickNoSync ($x, $y)")

        if (touchDown(x, y)) {
            SystemClock.sleep(tapTimeout)
            if (touchUp(x, y))
                return true
        }
        return false
    }

    fun clickAndSync(x: Int, y: Int, timeout: Long): Boolean {

        val logString = String.format("clickAndSync(%d, %d)", x, y)
        Log.d(LOG_TAG, logString)

        return runAndWaitForEvents(clickRunnable(x, y), WaitForAnyEventPredicate(
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or AccessibilityEvent.TYPE_VIEW_SELECTED), timeout) != null
    }

    fun longTapNoSync(x: Int, y: Int, timeout: Long = ViewConfiguration.getLongPressTimeout().toLong()): Boolean {
        if (DEBUG) {
            Log.d(LOG_TAG, "longTapNoSync ($x, $y)")
        }

        if (touchDown(x, y)) {
            SystemClock.sleep(timeout)
            if (touchUp(x, y)) {
                return true
            }
        }
        return false
    }

    fun longTapAndSync(x: Int, y: Int, timeout: Long): Boolean {

        val logString = String.format("clickAndSync(%d, %d)", x, y)
        Log.d(LOG_TAG, logString)

        return runAndWaitForEvents(longTapRunnable(x, y), WaitForAnyEventPredicate(
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or AccessibilityEvent.TYPE_VIEW_SELECTED), timeout) != null
    }

    @Throws(UiObjectNotFoundException::class)
    fun swipeLeftMultiTouch(view: UiObject, steps: Int, fingers: Int = 1): Boolean {

        if (steps < 2)
            throw RuntimeException("at least 2 steps required")

        if (fingers < 2)
            throw RuntimeException("at least 2 fingers required")

        val rect = view.visibleBounds
        rect.inset(SWIPE_MARGIN_LIMIT, SWIPE_MARGIN_LIMIT)

        Timber.v("visibleBounds: $rect")

        if (rect.width() <= SWIPE_MARGIN_LIMIT * 2) return false

        val array = arrayListOf<Array<MotionEvent.PointerCoords>>()
        var pointArray = Array(fingers, init = { PointerCoords() })

        val height = rect.height() / 2
        val stepHeight = height / (fingers - 1)
        val top = rect.top + (rect.height() - height) / 2
        val width = rect.width()
        val startPoint = Point(rect.right, top)

        for (i in 0 until fingers) {
            pointArray[i] =
                    PointerCoordsBuilder.newBuilder()
                            .setCoords(startPoint.x.toFloat(), startPoint.y + (stepHeight * i).toFloat())
                            .setSize(1f)
                            .build()
        }

        array.add(pointArray)

        for (step in 1 until steps) {
            pointArray = Array(fingers, init = { PointerCoords() })
            for (i in 0 until fingers) {
                pointArray[i] =
                        PointerCoordsBuilder.newBuilder()
                                .setCoords(startPoint.x.toFloat() - (width / steps) * step, startPoint.y + (stepHeight * i).toFloat())
                                .setSize(1f)
                                .build()
            }
            array.add(pointArray)
        }

        return performMultiPointerGesture(array.toTypedArray())
    }


    fun swipe(downX: Int, downY: Int, upX: Int, upY: Int, steps: Int, drag: Boolean = false,
              timeout: Long = ViewConfiguration.getLongPressTimeout().toLong()): Boolean {
        var ret: Boolean
        var swipeSteps = steps
        var xStep: Double
        var yStep: Double

        if (swipeSteps == 0) swipeSteps = 1

        xStep = (upX - downX).toDouble() / swipeSteps
        yStep = (upY - downY).toDouble() / swipeSteps

        ret = touchDown(downX, downY)

        if (drag) SystemClock.sleep(timeout)

        for (i in 1 until swipeSteps) {
            ret = ret and touchMove(downX + (xStep * i).toInt(), downY + (yStep * i).toInt())
            if (!ret) {
                break
            }
            SystemClock.sleep(MOTION_EVENT_INJECTION_DELAY_MILLIS.toLong())
        }
        if (drag) SystemClock.sleep(REGULAR_CLICK_LENGTH)
        ret = ret and touchUp(upX, upY)
        return ret
    }

    fun touchDown(x: Int, y: Int): Boolean {
        if (DEBUG) {
            Log.d(LOG_TAG, "touchDown ($x, $y)")
        }
        mDownTime = SystemClock.uptimeMillis()
        val event = getMotionEvent(mDownTime, mDownTime, ACTION_DOWN, x.toFloat(), y.toFloat())
        return injectEventSync(event)
    }

    fun touchUp(x: Int, y: Int): Boolean {
        if (DEBUG) {
            Log.d(LOG_TAG, "touchUp ($x, $y)")
        }
        val eventTime = SystemClock.uptimeMillis()
        val event = getMotionEvent(mDownTime, eventTime, MotionEvent.ACTION_UP, x.toFloat(), y.toFloat())
        mDownTime = 0
        return injectEventSync(event)
    }

    fun touchMove(x: Int, y: Int): Boolean {
        if (DEBUG) {
            Log.d(LOG_TAG, "touchMove ($x, $y)")
        }
        val eventTime = SystemClock.uptimeMillis()
        val event = getMotionEvent(mDownTime, eventTime, MotionEvent.ACTION_MOVE, x.toFloat(), y.toFloat())
        return injectEventSync(event)
    }

    fun rotate(view: UiObject, deg: Float, steps: Int): Boolean {
        val rect = view.visibleBounds

        val size = min(rect.height(), rect.width()).toFloat()
        val pt1 = PointF(rect.centerX().toFloat(), (rect.centerY() - (size / 4)))
        val pt2 = PointF(rect.centerX().toFloat(), (rect.centerY() + (size / 4)))
        val pt11 = PointF(pt1.x, pt1.y)
        val pt21 = PointF(pt2.x, pt2.y)

        val center = PointF(rect.centerX().toFloat(), rect.centerY().toFloat())

        val array = arrayListOf<Array<MotionEvent.PointerCoords>>()

        array.add(arrayOf(
                PointerCoordsBuilder.newBuilder().setCoords(pt1.x, pt1.y).setSize(1f).build(),
                PointerCoordsBuilder.newBuilder().setCoords(pt2.x, pt2.y).setSize(1f).build()))

        for (i in 1 until steps) {
            val p1 = Point2D.rotateAroundBy(pt11, center, (i.toFloat() / (steps - 1)) * deg)
            val p2 = Point2D.rotateAroundBy(pt21, center, (i.toFloat() / (steps - 1)) * deg)

            array.add(arrayOf(
                    PointerCoordsBuilder.newBuilder().setCoords(p1.x, p1.y).setSize(1f).build(),
                    PointerCoordsBuilder.newBuilder().setCoords(p2.x, p2.y).setSize(1f).build()))
        }

        return performMultiPointerGesture(array.toTypedArray())
    }

    fun performMultiPointerGesture(touches: Array<Array<PointerCoords>>,
                                   sleepBeforeMove: Long = MOTION_EVENT_INJECTION_DELAY_MILLIS.toLong(),
                                   sleepBeforeUp: Long = MOTION_EVENT_INJECTION_DELAY_MILLIS.toLong()):
            Boolean {
        Log.i(LOG_TAG, "performMultiPointerGesture, size: ${touches.size}")
        var ret = true

        // Get the pointer with the max steps to inject.
        val maxSteps = touches.size - 1


        // ACTION_DOWN
        val currentPointer = touches[0][0]
        val downTime = SystemClock.uptimeMillis()
        Log.i(LOG_TAG, "ACTION_DOWN (${currentPointer.x}, ${currentPointer.y})")
        var event: MotionEvent
        event =
                getMotionEvent(downTime, ACTION_DOWN, currentPointer.x, currentPointer.y, currentPointer.pressure, currentPointer.size)
        ret = ret and injectEventSync(event)

        SystemClock.sleep(MOTION_EVENT_INJECTION_DELAY_MILLIS.toLong())

        // ACTION_POINTER_DOWN
        Log.i(LOG_TAG, "ACTION_POINTER_DOWN")

        // specify the properties for each pointer as finger touch
        var properties = arrayOfNulls<MotionEvent.PointerProperties>(touches[0].size)
        var pointerCoords = Array(touches[0].size) { PointerCoords() }
        for (x in touches[0].indices) {
            val prop = MotionEvent.PointerProperties()
            prop.id = x
            prop.toolType = Configurator.getInstance().toolType
            properties[x] = prop
            pointerCoords[x] = touches[0][x]
        }

        for (x in 1 until touches[0].size) {
            event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
                    getPointerAction(MotionEvent.ACTION_POINTER_DOWN, x), x + 1, properties,
                    pointerCoords, 0, 0, 1f, 1f, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0)
            ret = ret and injectEventSync(event)
        }


        // ACTION_MOVE
        if (maxSteps > 1) {
            SystemClock.sleep(sleepBeforeMove)

            // specify the properties for each pointer as finger touch
            for (step in 1..maxSteps) {
                Log.i(LOG_TAG, "ACTION_MOVE, steps: $step of $maxSteps")

                val currentTouchArray = touches[step]
                properties = arrayOfNulls(currentTouchArray.size)
                pointerCoords = currentTouchArray

                for (touchIndex in currentTouchArray.indices) {
                    val prop = MotionEvent.PointerProperties()
                    prop.id = touchIndex
                    prop.toolType = Configurator.getInstance().toolType
                    properties[touchIndex] = prop
                }

                event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_MOVE, currentTouchArray.size, properties, pointerCoords, 0, 0, 1f, 1f,
                        0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0)

                ret = ret and injectEventSync(event)
                SystemClock.sleep(MOTION_EVENT_INJECTION_DELAY_MILLIS.toLong())
            }
        }

        SystemClock.sleep(sleepBeforeUp)

        // ACTION_POINTER_UP
        Log.i(LOG_TAG, "ACTION_POINTER_UP")
        val currentTouchArray = touches[touches.size - 1]

        for (x in 1 until currentTouchArray.size) {
            event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(),
                    getPointerAction(MotionEvent.ACTION_POINTER_UP, x), x + 1, properties,
                    pointerCoords, 0, 0, 1f, 1f, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0)
            ret = ret and injectEventSync(event)
        }

        // first to touch down is last up
        Log.i(LOG_TAG, "ACTION_UP")
        event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 1,
                properties, pointerCoords, 0, 0, 1f, 1f, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0)
        ret = ret and injectEventSync(event)
        return ret
    }

    fun clickRunnable(x: Int, y: Int): Runnable {
        return Runnable {
            if (touchDown(x, y)) {
                SystemClock.sleep(REGULAR_CLICK_LENGTH)
                touchUp(x, y)
            }
        }
    }

    fun longTapRunnable(x: Int, y: Int): Runnable {
        return Runnable {
            if (touchDown(x, y)) {
                SystemClock.sleep(ViewConfiguration.getLongPressTimeout().toLong())
                touchUp(x, y)
            }
        }
    }


    fun getUiAutomation(): UiAutomation {
        return getInstrumentation().uiAutomation
    }

    private fun runAndWaitForEvents(command: Runnable,
                                    filter: UiAutomation.AccessibilityEventFilter, timeout: Long): AccessibilityEvent? {

        return try {
            getUiAutomation().executeAndWaitForEvent(command, filter, timeout)
        } catch (e: TimeoutException) {
            Log.w(LOG_TAG, "runAndwaitForEvents timed out waiting for events")
            null
        } catch (e: Exception) {
            Log.e(LOG_TAG, "exception from executeCommandAndWaitForAccessibilityEvent", e)
            null
        }

    }

    fun injectEventSync(event: InputEvent): Boolean {
        return getUiAutomation().injectInputEvent(event, true)
    }

    internal inner class WaitForAnyEventPredicate(private var mMask: Int) : UiAutomation.AccessibilityEventFilter {
        override fun accept(t: AccessibilityEvent): Boolean {
            return t.eventType and mMask != 0
        }
    }

    companion object {
        const val DEBUG: Boolean = true
        const val REGULAR_CLICK_LENGTH: Long = 100
        const val MOTION_EVENT_INJECTION_DELAY_MILLIS = 5
        const val SWIPE_MARGIN_LIMIT = 5

        val LOG_TAG: String = Interaction::class.java.name

        fun getMotionEvent(downTime: Long, eventTime: Long, action: Int,
                           x: Float, y: Float, pressure: Float = 1f, size: Float = 1f, pointerCount: Int = 1): MotionEvent {

            val properties = MotionEvent.PointerProperties()
            properties.id = 0
            properties.toolType = Configurator.getInstance().toolType

            val coords = MotionEvent.PointerCoords()
            coords.pressure = pressure
            coords.size = size
            coords.x = x
            coords.y = y

            return MotionEvent.obtain(downTime, eventTime, action, pointerCount,
                    arrayOf(properties), arrayOf(coords),
                    0, 0, 1.0f, 1.0f, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0)
        }

        fun getMotionEvent(downTime: Long, action: Int, x: Float, y: Float, pressure: Float = 1f, size: Float = 1f, pointerCount: Int = 1): MotionEvent {
            return getMotionEvent(downTime, SystemClock.uptimeMillis(), action, x, y, pressure, size, pointerCount)
        }

        fun getPointerAction(motionEnvent: Int, index: Int): Int {
            return motionEnvent + (index shl MotionEvent.ACTION_POINTER_INDEX_SHIFT)
        }
    }
}

object Point2D {

    /**
     * Rotate a point around a pivot point. The point will be updated in place
     *
     * @param position - The point to be rotated
     * @param center   - The center point
     * @param angle    - The angle, in degrees
     */
    fun rotateAroundBy(position: PointF, center: PointF, angle: Float): PointF {
        val angleInRadians = angle * (Math.PI / 180)
        val cosTheta = Math.cos(angleInRadians)
        val sinTheta = Math.sin(angleInRadians)

        val x = (cosTheta * (position.x - center.x) - sinTheta * (position.y - center.y) + center.x).toFloat()
        val y = (sinTheta * (position.x - center.x) + cosTheta * (position.y - center.y) + center.y).toFloat()
        return PointF(x, y)
    }

    /**
     * Rotate a point in place around it's origin
     *
     * @param point  - point to rotate
     * @param origin - origin point
     * @param deg    - angle in degrees
     */
    fun rotateAroundOrigin(point: PointF, origin: PointF, deg: Float) {
        val rad = radians(deg.toDouble()).toFloat()
        val s = Math.sin(rad.toDouble()).toFloat()
        val c = Math.cos(rad.toDouble()).toFloat()

        point.x -= origin.x
        point.y -= origin.y

        val xnew = point.x * c - point.y * s
        val ynew = point.x * s + point.y * c

        point.x = xnew + origin.x
        point.y = ynew + origin.y
    }

    /**
     * Get the point between 2 points at the given t distance ( between 0 and 1 )
     *
     * @param pt1      the first point
     * @param pt2      the second point
     * @param t        the distance to calculate the average point ( 0 >= t <= 1 )
     * @param dstPoint the destination point
     */
    fun getLerp(pt1: PointF, pt2: PointF, t: Float): PointF {
        return PointF(pt1.x + (pt2.x - pt1.x) * t, pt1.y + (pt2.y - pt1.y) * t)
    }

    /**
     * Degrees to radians.
     *
     * @param degree the degree
     * @return the double
     */
    fun radians(degree: Double): Double {
        return degree * (Math.PI / 180)
    }
}
