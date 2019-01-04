package it.sephiroth.android.library.uigestures

import android.content.Context
import android.graphics.PointF
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import kotlin.math.sqrt

/**
 * UISwipeGestureRecognizer is a subclass of UIGestureRecognizer that looks for swiping gestures in one or more
 * directions. A swipe is a discrete gesture, and thus the associated action message is sent only once per gesture.
 *
 * @author alessandro crugnola
 * @see [
 * https://developer.apple.com/reference/uikit/uiswipegesturerecognizer](https://developer.apple.com/reference/uikit/uiswipegesturerecognizer)
 */

open class UISwipeGestureRecognizer(context: Context) : UIGestureRecognizer(context), UIDiscreteGestureRecognizer {

    private val mMaximumFlingVelocity: Int
    private val mMinimumFlingVelocity: Int

    private var mStarted: Boolean = false

    /**
     * @since 1.0.0
     */
    var direction: Int = 0

    /**
     * @since 1.0.0
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var numberOfTouchesRequired: Int = 1

    private var mLastFocusX: Float = 0.toFloat()
    private var mLastFocusY: Float = 0.toFloat()
    private var mDownFocusX: Float = 0.toFloat()
    private var mDownFocusY: Float = 0.toFloat()

    private var mVelocityTracker: VelocityTracker? = null
    private var scrollX: Float = 0.toFloat()
    private var scrollY: Float = 0.toFloat()

    /**
     * @since 1.0.0
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var translationX: Float = 0.toFloat()
        internal set

    /**
     * @since 1.0.0
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var translationY: Float = 0.toFloat()
        internal set

    /**
     * @since 1.0.0
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var yVelocity: Float = 0.toFloat()
        private set

    /**
     * @since 1.0.0
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var xVelocity: Float = 0.toFloat()
        private set

    private val mCurrentLocation: PointF

    @Suppress("MemberVisibilityCanBePrivate")
    var downTime: Long = 0
        private set

    override var numberOfTouches: Int = 0
        internal set

    private var mDown: Boolean = false

    override val currentLocationX: Float
        get() = mCurrentLocation.x

    override val currentLocationY: Float
        get() = mCurrentLocation.y

    @Suppress("MemberVisibilityCanBePrivate")
    var minimumTouchDistance: Int

    @Suppress("MemberVisibilityCanBePrivate")
    var minimumSwipeDistance: Int = 0

    @Suppress("MemberVisibilityCanBePrivate")
    var maximumTouchSlopTime = MAXIMUM_TOUCH_SLOP_TIME

    @Suppress("MemberVisibilityCanBePrivate")
    var maximumTouchFlingTime = MAXIMUM_TOUCH_FLING_TIME

    init {
        direction = RIGHT
        mStarted = false

        val configuration = ViewConfiguration.get(context)

        minimumTouchDistance = configuration.scaledTouchSlop
        mMaximumFlingVelocity = configuration.scaledMaximumFlingVelocity
        mMinimumFlingVelocity = configuration.scaledMinimumFlingVelocity
        minimumSwipeDistance = (minimumTouchDistance * 3f).toInt()

        if (logEnabled) {
            logMessage(Log.INFO, "minimumTouchDistance: $minimumTouchDistance")
            logMessage(Log.INFO, "minimumSwipeDistance: $minimumSwipeDistance")
            logMessage(Log.INFO, "mMinimumFlingVelocity: $mMinimumFlingVelocity")
            logMessage(Log.INFO, "mMaximumFlingVelocity: $mMaximumFlingVelocity")
        }

        mCurrentLocation = PointF()
    }

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            MESSAGE_RESET -> handleReset()
            else -> {
            }
        }
    }

    override fun reset() {
        super.reset()
        handleReset()
    }

    private fun handleReset() {
        mStarted = false
        setBeginFiringEvents(false)
        state = UIGestureRecognizer.State.Possible

    }

    override fun removeMessages() {
        removeMessages(MESSAGE_RESET)
    }

    override fun onStateChanged(recognizer: UIGestureRecognizer) {
        logMessage(Log.VERBOSE, "onStateChanged(${recognizer.state?.name}, $mStarted)")

        if (recognizer.state === UIGestureRecognizer.State.Failed && state === UIGestureRecognizer.State.Ended) {
            removeMessages()
            stopListenForOtherStateChanges()
            fireActionEventIfCanRecognizeSimultaneously()

            if (!mDown) {
                mStarted = false
                state = UIGestureRecognizer.State.Possible
            }

        } else if (recognizer.inState(UIGestureRecognizer.State.Began, UIGestureRecognizer.State.Ended) && mStarted && inState(UIGestureRecognizer.State.Possible, UIGestureRecognizer.State.Ended)) {
            mStarted = false
            setBeginFiringEvents(false)
            stopListenForOtherStateChanges()
            removeMessages()
            state = UIGestureRecognizer.State.Failed
        }
    }

    private fun fireActionEventIfCanRecognizeSimultaneously() {
        if (delegate!!.shouldRecognizeSimultaneouslyWithGestureRecognizer(this)) {
            setBeginFiringEvents(true)
            fireActionEvent()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        if (!isEnabled) {
            return false
        }

        val action = event.action

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }

        mVelocityTracker!!.addMovement(event)

        val pointerUp = action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_POINTER_UP
        val skipIndex = if (pointerUp) event.actionIndex else -1

        // Determine focal point
        var sumX = 0f
        var sumY = 0f
        val count = event.pointerCount
        for (i in 0 until count) {
            if (skipIndex == i) {
                continue
            }
            sumX += event.getX(i)
            sumY += event.getY(i)
        }
        val div = if (pointerUp) count - 1 else count
        val focusX = sumX / div
        val focusY = sumY / div

        mCurrentLocation.x = focusX
        mCurrentLocation.y = focusY

        numberOfTouches = if (pointerUp) count - 1 else count

        when (action and MotionEvent.ACTION_MASK) {

            MotionEvent.ACTION_POINTER_DOWN -> {
                mLastFocusX = focusX
                mLastFocusY = focusY
                mDownFocusX = focusX
                mDownFocusY = focusY

                if (state === UIGestureRecognizer.State.Possible && !mStarted) {
                    if (count > numberOfTouchesRequired) {
                        state = UIGestureRecognizer.State.Failed
                        removeMessages(MESSAGE_RESET)
                    }
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                mLastFocusX = focusX
                mLastFocusY = focusY
                mDownFocusX = focusX
                mDownFocusY = focusY

                mVelocityTracker!!.computeCurrentVelocity(1000, mMaximumFlingVelocity.toFloat())
                val upIndex = event.actionIndex

                val id1 = event.getPointerId(upIndex)
                val x1 = mVelocityTracker!!.getXVelocity(id1)
                val y1 = mVelocityTracker!!.getYVelocity(id1)
                for (i in 0 until count) {
                    if (i == upIndex) {
                        continue
                    }

                    val id2 = event.getPointerId(i)
                    val x = x1 * mVelocityTracker!!.getXVelocity(id2)
                    val y = y1 * mVelocityTracker!!.getYVelocity(id2)

                    val dot = x + y

                    if (dot < 0) {
                        mVelocityTracker!!.clear()
                        break
                    }
                }

                if (state === UIGestureRecognizer.State.Possible && !mStarted) {
                    if (count - 1 < numberOfTouchesRequired) {
                        state = UIGestureRecognizer.State.Failed
                        removeMessages(MESSAGE_RESET)
                    }
                }
            }

            MotionEvent.ACTION_DOWN -> {
                if (delegate?.shouldReceiveTouch?.invoke(this)!!) {
                    mStarted = false
                    mDown = true

                    mLastFocusX = focusX
                    mLastFocusY = focusY
                    mDownFocusX = mLastFocusX
                    mDownFocusY = mLastFocusY
                    downTime = event.eventTime

                    mVelocityTracker!!.clear()

                    setBeginFiringEvents(false)
                    removeMessages(MESSAGE_RESET)
                    state = UIGestureRecognizer.State.Possible
                }
            }

            MotionEvent.ACTION_MOVE -> {
                scrollX = mLastFocusX - focusX
                scrollY = mLastFocusY - focusY

                if (state === UIGestureRecognizer.State.Possible) {
                    val deltaX = (focusX - mDownFocusX).toDouble()
                    val deltaY = (focusY - mDownFocusY).toDouble()
                    val distance = sqrt(Math.pow(deltaX, 2.0) + Math.pow(deltaY, 2.0))
                    if (!mStarted) {
                        if (distance > minimumTouchDistance) {
                            mVelocityTracker!!.computeCurrentVelocity(1000, mMaximumFlingVelocity.toFloat())
                            yVelocity = mVelocityTracker!!.yVelocity
                            xVelocity = mVelocityTracker!!.xVelocity

                            translationX -= scrollX
                            translationY -= scrollY

                            mLastFocusX = focusX
                            mLastFocusY = focusY
                            mStarted = true

                            if (count == numberOfTouchesRequired) {
                                val time = event.eventTime - event.downTime
                                if (time > maximumTouchSlopTime) {
                                    logMessage(Log.WARN, "passed too much time")
                                    mStarted = false
                                    setBeginFiringEvents(false)
                                    state = UIGestureRecognizer.State.Failed
                                } else {
                                    val direction =
                                            getTouchDirection(mDownFocusX, mDownFocusY, focusX, focusY, xVelocity, yVelocity, 0f)
                                    logMessage(Log.VERBOSE, "direction: $direction")
                                    if (direction == -1 || (this.direction and direction) == 0) {
                                        mStarted = false
                                        setBeginFiringEvents(false)
                                        state = UIGestureRecognizer.State.Failed
                                    } else {
                                        logMessage(Log.DEBUG, "direction accepted: ${(this.direction and direction)}")
                                        mStarted = true
                                    }
                                }
                            } else {
                                mStarted = false
                                setBeginFiringEvents(false)
                                state = UIGestureRecognizer.State.Failed
                            }
                        }
                    } else {
                        // touch has been recognized. now let's track the movement
                        mVelocityTracker!!.computeCurrentVelocity(1000, mMaximumFlingVelocity.toFloat())
                        yVelocity = mVelocityTracker!!.yVelocity
                        xVelocity = mVelocityTracker!!.xVelocity
                        val time = event.eventTime - event.downTime

                        if (time > maximumTouchFlingTime) {
                            mStarted = false
                            state = UIGestureRecognizer.State.Failed
                        } else {
                            val direction = getTouchDirection(
                                    mDownFocusX, mDownFocusY, focusX, focusY, xVelocity, yVelocity, minimumSwipeDistance.toFloat())

                            if (direction != -1) {
                                if (this.direction and direction != 0) {
                                    if (delegate?.shouldBegin?.invoke(this)!!) {
                                        state = UIGestureRecognizer.State.Ended
                                        if (null == requireFailureOf) {
                                            fireActionEventIfCanRecognizeSimultaneously()
                                        } else {
                                            when {
                                                requireFailureOf!!.state === UIGestureRecognizer.State.Failed -> fireActionEventIfCanRecognizeSimultaneously()
                                                requireFailureOf!!.inState(UIGestureRecognizer.State.Began, UIGestureRecognizer.State.Ended, UIGestureRecognizer.State.Changed) -> {
                                                    mStarted = false
                                                    setBeginFiringEvents(false)
                                                    state = UIGestureRecognizer.State.Failed
                                                }
                                                else -> {
                                                    logMessage(Log.DEBUG, "waiting...")
                                                    listenForOtherStateChanges()
                                                    setBeginFiringEvents(false)
                                                }
                                            }
                                        }
                                    } else {
                                        state = UIGestureRecognizer.State.Failed
                                        mStarted = false
                                        setBeginFiringEvents(false)
                                    }
                                } else {
                                    mStarted = false
                                    setBeginFiringEvents(false)
                                    state = UIGestureRecognizer.State.Failed
                                }
                            }
                        }

                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                if (mVelocityTracker != null) {
                    mVelocityTracker!!.recycle()
                    mVelocityTracker = null
                }

                // TODO: should we fail if the gesture didn't actually start?

                mDown = false
                removeMessages(MESSAGE_RESET)
            }

            MotionEvent.ACTION_CANCEL -> {
                if (mVelocityTracker != null) {
                    mVelocityTracker!!.recycle()
                    mVelocityTracker = null
                }

                mDown = false
                removeMessages(MESSAGE_RESET)
                state = UIGestureRecognizer.State.Cancelled
                mHandler.sendEmptyMessage(MESSAGE_RESET)
            }

            else -> {
            }
        }

        return cancelsTouchesInView
    }

    private fun getTouchDirection(
            x1: Float, y1: Float, x2: Float, y2: Float, velocityX: Float, velocityY: Float, distanceThreshold: Float): Int {
        val diffY = y2 - y1
        val diffX = x2 - x1
        logMessage(Log.INFO, "getTouchDirection")
        logMessage(Log.VERBOSE, "diff: $diffX, $diffY, distanceThreshold: $distanceThreshold")
        logMessage(Log.VERBOSE, "velocity: $velocityX, $velocityY, mMinimumFlingVelocity: $mMinimumFlingVelocity")

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > distanceThreshold && Math.abs(velocityX) > mMinimumFlingVelocity) {
                return if (diffX > 0) {
                    RIGHT
                } else {
                    LEFT
                }
            }
        } else if (Math.abs(diffY) > distanceThreshold && Math.abs(velocityY) > mMinimumFlingVelocity) {
            return if (diffY > 0) {
                DOWN
            } else {
                UP
            }
        }
        return -1
    }

    /**
     * @since 1.1.2
     */
    @Suppress("unused")
    val relativeScrollX: Float
        get() = -scrollX

    /**
     * @since 1.1.2
     */
    @Suppress("unused")
    val relativeScrollY: Float
        get() = -scrollY

    companion object {
        private const val MESSAGE_RESET = 4

        const val RIGHT = 1 shl 1
        const val LEFT = 1 shl 2
        const val UP = 1 shl 3
        const val DOWN = 1 shl 4
        const val MAXIMUM_TOUCH_SLOP_TIME = 150
        const val MAXIMUM_TOUCH_FLING_TIME = 300
    }
}