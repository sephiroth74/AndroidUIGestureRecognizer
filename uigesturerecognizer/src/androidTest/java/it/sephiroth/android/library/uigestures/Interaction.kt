package it.sephiroth.android.library.uigestures

import android.app.UiAutomation
import android.os.SystemClock
import android.util.Log
import android.view.InputDevice
import android.view.InputEvent
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.PointerCoords
import android.view.ViewConfiguration
import android.view.accessibility.AccessibilityEvent
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.Configurator
import java.util.concurrent.TimeoutException

class Interaction {
    private var mDownTime: Long = 0

    fun clickNoSync(x: Int, y: Int): Boolean {
        Log.d(LOG_TAG, "clickNoSync ($x, $y)")

        if (touchDown(x, y)) {
            SystemClock.sleep(REGULAR_CLICK_LENGTH)
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

    fun performMultiPointerGesture(touches: Array<Array<PointerCoords>>): Boolean {
        Log.i(LOG_TAG, "performMultiPointerGesture, size: ${touches.size}")
        var ret = true
//        if (touches.size < 2) {
//            throw IllegalArgumentException("Must provide coordinates for at least 2 pointers")
//        }

        // Get the pointer with the max steps to inject.
        val maxSteps = touches.size - 1

        Log.i(LOG_TAG, "ACTION_DOWN")


        // ACTION_DOWN
        var currentPointer = touches[0][0]
        val downTime = SystemClock.uptimeMillis()
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

            SystemClock.sleep(1000)

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

        SystemClock.sleep(MOTION_EVENT_INJECTION_DELAY_MILLIS.toLong())

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

    private fun injectEventSync(event: InputEvent): Boolean {
        return getUiAutomation().injectInputEvent(event, true)
    }

    private fun getPointerAction(motionEnvent: Int, index: Int): Int {
        return motionEnvent + (index shl MotionEvent.ACTION_POINTER_INDEX_SHIFT)
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
                           x: Float, y: Float, pressure: Float = 1f, size: Float = 1f): MotionEvent {

            val properties = MotionEvent.PointerProperties()
            properties.id = 0
            properties.toolType = Configurator.getInstance().toolType

            val coords = MotionEvent.PointerCoords()
            coords.pressure = pressure
            coords.size = size
            coords.x = x
            coords.y = y

            return MotionEvent.obtain(downTime, eventTime, action, 1,
                    arrayOf(properties), arrayOf(coords),
                    0, 0, 1.0f, 1.0f, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0)
        }

        fun getMotionEvents(downTime: Long, eventTime: Long, action: Int, pointerCount: Int,
                            x: Array<Float>, y: Array<Float>, pressure: Array<Float>, size: Array<Float>): MotionEvent {

            val properties = Array(x.size) { MotionEvent.PointerProperties() }
            val coords = Array(x.size) { PointerCoords() }

            for (i in 0 until x.size) {
                properties[i].id = i + 1
                properties[i].toolType = Configurator.getInstance().toolType

                coords[i].pressure = pressure[i]
                coords[i].size = size[i]
                coords[i].x = x[i]
                coords[i].y = y[i]
            }

            return MotionEvent.obtain(downTime, eventTime, action, pointerCount,
                    properties, coords,
                    0, 0, 1.0f, 1.0f, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0)
        }

        fun getMotionEvent(downTime: Long, action: Int, x: Float, y: Float, pressure: Float = 1f, size: Float = 1f): MotionEvent {
            return getMotionEvent(downTime, SystemClock.uptimeMillis(), action, x, y, pressure, size)
        }
    }

}