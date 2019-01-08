package it.sephiroth.android.library.uigestures

import android.content.Context
import android.graphics.PointF
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration

/**
 * UISwipeGestureRecognizer is a subclass of UIGestureRecognizer that looks for swiping gestures in one or more
 * directions. A swipe is a discrete gesture, and thus the associated action message is sent only once per gesture.
 *
 * @author alessandro crugnola
 * @see [
 * https://developer.apple.com/reference/uikit/uiswipegesturerecognizer](https://developer.apple.com/reference/uikit/uiswipegesturerecognizer)
 */

@Suppress("MemberVisibilityCanBePrivate", "unused")
open class UISwipeGestureRecognizer(context: Context) : UIGestureRecognizer(context), UIDiscreteGestureRecognizer {

    /**
     * Minimum fling velocity before the touch can be accepted
     * @since 1.0.0
     */
    var scaledMinimumFlingVelocity: Int

    var scaledMaximumFlingVelocity: Int

    /**
     * Direction of the swipe gesture. Can be one of RIGHT, LEFT, UP, DOWN
     * @since 1.0.0
     */
    var direction: Int = RIGHT

    /**
     * Number of touches required for the gesture to be accepted
     * @since 1.0.0
     */
    var numberOfTouchesRequired: Int = 1

    var scrollX: Float = 0.toFloat()
        private set

    var scrollY: Float = 0.toFloat()
        private set

    /**
     * @since 1.1.2
     */
    val relativeScrollX: Float get() = -scrollX

    /**
     * @since 1.1.2
     */
    val relativeScrollY: Float get() = -scrollY

    /**
     * @since 1.0.0
     */
    var translationX: Float = 0.toFloat()
        internal set

    /**
     * @since 1.0.0
     */
    var translationY: Float = 0.toFloat()
        internal set

    /**
     * @since 1.0.0
     */
    var yVelocity: Float = 0.toFloat()
        private set

    /**
     * @since 1.0.0
     */
    var xVelocity: Float = 0.toFloat()
        private set

    /**
     * Minimum distance in pixel before the touch can be considered
     * as a scroll
     */
    var scaledTouchSlop: Int

    /**
     * Minimum total distance before the gesture will begin
     * @since 1.0.0
     */
    var minimumSwipeDistance: Int = 0

    /**
     * Maximum amount of time allowed between a touch down and a touch move
     * before the gesture will fail
     * @since 1.0.0
     */
    var maximumTouchSlopTime = MAXIMUM_TOUCH_SLOP_TIME

    /**
     * During a move event, the maximum time between touches before
     * the gesture will fail
     * @since 1.0.0
     */
    var maximumTouchFlingTime = MAXIMUM_TOUCH_FLING_TIME

    private var mDown: Boolean = false
    private var mStarted: Boolean = false
    private val mLastFocusLocation = PointF()
    private val mDownFocusLocation = PointF()
    private var mVelocityTracker: VelocityTracker? = null

    init {
        mStarted = false

        val configuration = ViewConfiguration.get(context)

        scaledTouchSlop = configuration.scaledTouchSlop
        scaledMaximumFlingVelocity = configuration.scaledMaximumFlingVelocity
        scaledMinimumFlingVelocity = configuration.scaledMinimumFlingVelocity
        minimumSwipeDistance = (scaledTouchSlop * 3f).toInt()

        if (logEnabled) {
            logMessage(Log.INFO, "scaledTouchSlop: $scaledTouchSlop")
            logMessage(Log.INFO, "minimumSwipeDistance: $minimumSwipeDistance")
            logMessage(Log.INFO, "scaledMinimumFlingVelocity: $scaledMinimumFlingVelocity")
            logMessage(Log.INFO, "scaledMaximumFlingVelocity: $scaledMaximumFlingVelocity")
        }

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
        state = State.Possible
    }

    override fun removeMessages() {
        removeMessages(MESSAGE_RESET)
    }

    override fun onStateChanged(recognizer: UIGestureRecognizer) {
        if (recognizer.state == State.Failed && state == State.Ended) {
            removeMessages()
            stopListenForOtherStateChanges()
            fireActionEventIfCanRecognizeSimultaneously()

            if (!mDown) {
                mStarted = false
                state = State.Possible
            }

        } else if (recognizer.inState(State.Began, State.Ended) && mStarted && inState(State.Possible, State.Ended)) {
            mStarted = false
            setBeginFiringEvents(false)
            stopListenForOtherStateChanges()
            removeMessages()
            state = State.Failed
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

        val action = event.actionMasked

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }

        mVelocityTracker?.addMovement(event)

        when (action) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                mLastFocusLocation.set(mCurrentLocation)
                mDownFocusLocation.set(mCurrentLocation)

                if (state == State.Possible && !mStarted) {
                    if (numberOfTouches > numberOfTouchesRequired) {
                        state = State.Failed
                        removeMessages(MESSAGE_RESET)
                    }
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                mLastFocusLocation.set(mCurrentLocation)
                mDownFocusLocation.set(mCurrentLocation)

                mVelocityTracker?.computeCurrentVelocity(1000, scaledMaximumFlingVelocity.toFloat())
                val upIndex = event.actionIndex

                val id1 = event.getPointerId(upIndex)
                val x1 = mVelocityTracker!!.getXVelocity(id1)
                val y1 = mVelocityTracker!!.getYVelocity(id1)
                for (i in 0 until numberOfTouches) {
                    if (i == upIndex) {
                        continue
                    }

                    val id2 = event.getPointerId(i)
                    val x = x1 * mVelocityTracker!!.getXVelocity(id2)
                    val y = y1 * mVelocityTracker!!.getYVelocity(id2)

                    val dot = x + y

                    if (dot < 0) {
                        mVelocityTracker?.clear()
                        break
                    }
                }

                if (state == State.Possible && !mStarted) {
                    if (numberOfTouches < numberOfTouchesRequired) {
                        state = State.Failed
                        removeMessages(MESSAGE_RESET)
                    }
                }
            }

            MotionEvent.ACTION_DOWN -> {
                if (delegate?.shouldReceiveTouch?.invoke(this)!!) {
                    mStarted = false
                    mDown = true

                    mLastFocusLocation.set(mCurrentLocation)
                    mDownFocusLocation.set(mCurrentLocation)

                    mVelocityTracker?.clear()

                    setBeginFiringEvents(false)
                    removeMessages(MESSAGE_RESET)
                    state = State.Possible
                }
            }

            MotionEvent.ACTION_MOVE -> {
                scrollX = mLastFocusLocation.x - mCurrentLocation.x
                scrollY = mLastFocusLocation.y - mCurrentLocation.y

                mVelocityTracker?.computeCurrentVelocity(1000, scaledMaximumFlingVelocity.toFloat())
                yVelocity = mVelocityTracker!!.yVelocity
                xVelocity = mVelocityTracker!!.xVelocity

                if (state == State.Possible) {
                    val distance = mCurrentLocation.distance(mDownFocusLocation)
                    logMessage(Log.INFO, "started: $mStarted, distance: $distance, slop: $scaledTouchSlop")
                    if (!mStarted) {
                        if (distance > scaledTouchSlop) {

                            translationX -= scrollX
                            translationY -= scrollY

                            mLastFocusLocation.set(mCurrentLocation)
                            mStarted = true

                            if (numberOfTouches == numberOfTouchesRequired) {
                                val time = event.eventTime - event.downTime

                                logMessage(Log.VERBOSE, "time: $time, maximumTouchSlopTime: $maximumTouchSlopTime")

                                if (time > maximumTouchSlopTime) {
                                    logMessage(Log.WARN, "passed too much time 1 ($time > $maximumTouchSlopTime)")
                                    mStarted = false
                                    setBeginFiringEvents(false)
                                    state = State.Failed
                                } else {
                                    val direction =
                                            getTouchDirection(mDownFocusLocation.x, mDownFocusLocation.y, mCurrentLocation.x,
                                                    mCurrentLocation.y, xVelocity, yVelocity, 0f)

                                    logMessage(Log.VERBOSE, "(1) direction: $direction")

                                    // this is necessary because sometimes the velocityTracker
                                    // return 0, probably it needs more input events before computing
                                    // correctly the velocities

                                    if (xVelocity != 0f || yVelocity != 0f) {
                                        if (direction == -1 || (this.direction and direction) == 0) {
                                            logMessage(Log.WARN, "invalid direction: $direction")
                                            mStarted = false
                                            setBeginFiringEvents(false)
                                            state = State.Failed
                                        } else {
                                            logMessage(Log.DEBUG, "direction accepted: ${(this.direction and direction)}")
                                            mStarted = true
                                        }
                                    } else {
                                        logMessage(Log.WARN, "velocity is still 0, waiting for the next event...")
                                        mDownFocusLocation.set(mCurrentLocation)
                                        mStarted = false
                                    }
                                }
                            } else {
                                logMessage(Log.WARN, "invalid number of touches ($numberOfTouches != $numberOfTouchesRequired)")
                                mStarted = false
                                setBeginFiringEvents(false)
                                state = State.Failed
                            }
                        }
                    } else {
                        // touch has been recognized. now let's track the movement
                        val time = event.eventTime - event.downTime

                        if (time > maximumTouchFlingTime) {
                            logMessage(Log.WARN, "passed too much time 2 ($time > $maximumTouchFlingTime)")
                            mStarted = false
                            state = State.Failed
                        } else {
                            val direction = getTouchDirection(
                                    mDownFocusLocation.x, mDownFocusLocation.y, mCurrentLocation.x, mCurrentLocation.y, xVelocity,
                                    yVelocity, minimumSwipeDistance.toFloat())

                            if (direction != -1) {
                                if (this.direction and direction != 0) {
                                    if (delegate?.shouldBegin?.invoke(this)!!) {
                                        state = State.Ended
                                        if (null == requireFailureOf) {
                                            fireActionEventIfCanRecognizeSimultaneously()
                                        } else {
                                            when {
                                                requireFailureOf!!.state == State.Failed -> fireActionEventIfCanRecognizeSimultaneously()
                                                requireFailureOf!!.inState(State.Began, State.Ended, State.Changed) -> {
                                                    mStarted = false
                                                    setBeginFiringEvents(false)
                                                    state = State.Failed
                                                }
                                                else -> {
                                                    logMessage(Log.DEBUG, "waiting...")
                                                    listenForOtherStateChanges()
                                                    setBeginFiringEvents(false)
                                                }
                                            }
                                        }
                                    } else {
                                        state = State.Failed
                                        mStarted = false
                                        setBeginFiringEvents(false)
                                    }
                                } else {
                                    mStarted = false
                                    setBeginFiringEvents(false)
                                    state = State.Failed
                                }
                            }
                        }

                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                mVelocityTracker?.addMovement(event)

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
                state = State.Cancelled
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

        if (logEnabled) {
            logMessage(Log.INFO, "getTouchDirection")
            logMessage(Log.VERBOSE, "diff: $diffX, $diffY, distanceThreshold: $distanceThreshold")
            logMessage(Log.VERBOSE, "velocity: $velocityX, $velocityY, scaledMinimumFlingVelocity: $scaledMinimumFlingVelocity, " +
                    "scaledMaximumFlingVelocity: $scaledMaximumFlingVelocity")
        }

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > distanceThreshold && Math.abs(velocityX) > scaledMinimumFlingVelocity) {
                return if (diffX > 0) {
                    RIGHT
                } else {
                    LEFT
                }
            }
        } else if (Math.abs(diffY) > distanceThreshold && Math.abs(velocityY) > scaledMinimumFlingVelocity) {
            return if (diffY > 0) {
                DOWN
            } else {
                UP
            }
        }
        return -1
    }

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