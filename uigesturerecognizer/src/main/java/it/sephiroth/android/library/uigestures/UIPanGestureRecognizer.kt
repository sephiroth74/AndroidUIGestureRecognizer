package it.sephiroth.android.library.uigestures

import android.content.Context
import android.graphics.PointF
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration

/**
 * UIPanGestureRecognizer is a subclass of UIGestureRecognizer that looks for panning (dragging) gestures. The user must
 * be pressing one or more fingers on a view while they pan it. Clients implementing the action method for this gesture
 * recognizer can ask it for the current translation and velocity of the gesture.
 *
 * @author alessandro crugnola
 * @see [
 * https://developer.apple.com/reference/uikit/uipangesturerecognizer](https://developer.apple.com/reference/uikit/uipangesturerecognizer)
 */
@Suppress("MemberVisibilityCanBePrivate")
open class UIPanGestureRecognizer(context: Context) : UIGestureRecognizer(context), UIContinuousRecognizer {

    /**
     * Minimum finger movement before the touch can be considered a pan
     * @since 1.2.5
     */
    var scaledTouchSlop: Int

    /**
     * Minimum fling velocity when calling {#isFling}
     */
    val minimumFlingVelocity: Int

    private val maximumFlingVelocity: Int

    /**
     * The minimum number of fingers that can be touching the view for this gesture to be recognized.
     * The default value is 1
     *
     * @since 1.0.0
     */
    var minimumNumberOfTouches: Int = 1

    /**
     * The maximum number of fingers that can be touching the view for this gesture to be recognized.
     * @since 1.0.0
     */
    var maximumNumberOfTouches: Int = Integer.MAX_VALUE

    var scrollX: Float = 0.toFloat()
        private set

    var scrollY: Float = 0.toFloat()
        private set

    /**
     * @return the relative scroll x between gestures
     * @since 1.1.2
     */
    @Suppress("unused")
    val relativeScrollX: Float
        get() = -scrollX


    /**
     * @return the relative scroll y between gestures
     * @since 1.1.2
     */
    @Suppress("unused")
    val relativeScrollY: Float
        get() = -scrollY

    /**
     * @return The translation X of the pan gesture
     * @since 1.0.0
     */
    var translationX: Float = 0.toFloat()
        private set

    /**
     * @return The translation Y of the pan gesture
     * @since 1.0.0
     */
    var translationY: Float = 0.toFloat()
        private set

    /**
     * @return The y velocity of the pan gesture
     * @since 1.0.0
     */
    var yVelocity: Float = 0.toFloat()
        private set

    /**
     * @return The x velocity of the pan gesture
     * @since 1.0.0
     */
    var xVelocity: Float = 0.toFloat()
        private set

    /**
     * Once the gesture completes (State.Ended) this can be called
     * to know if the gesture was a fling gesture
     * @since 1.0.0
     */
    @Suppress("unused")
    val isFling: Boolean
        get() = state == State.Ended && (Math.abs(xVelocity) > minimumFlingVelocity || Math.abs(yVelocity) > minimumFlingVelocity)

    /**
     * Returns focus x value that originates the gesture
     * @since 1.0.0
     */
    val startLocationX: Float get() = mStartLocation.x

    /**
     * Returns focus y value that originates the gesture
     * @since 1.0.0
     */
    val startLocationY: Float get() = mStartLocation.y

    private var mVelocityTracker: VelocityTracker? = null
    private var mDown: Boolean = false
    private var mStarted: Boolean = false
    private var mLastFocusLocation = PointF()
    private var mDownFocusLocation = PointF()
    private var mStartLocation = PointF()

    init {
        val configuration = ViewConfiguration.get(context)
        minimumFlingVelocity = configuration.scaledMinimumFlingVelocity
        maximumFlingVelocity = configuration.scaledMaximumFlingVelocity
        scaledTouchSlop = configuration.scaledTouchSlop

        if (logEnabled) {
            logMessage(Log.INFO, "scaledTouchSlop: $scaledTouchSlop")
            logMessage(Log.INFO, "minimumFlingVelocity: $minimumFlingVelocity")
            logMessage(Log.INFO, "maximumFlingVelocity: $maximumFlingVelocity")
        }
    }

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            MESSAGE_RESET -> handleReset()
            else -> {
            }
        }
    }

    private fun handleReset() {
        mStarted = false
        mDown = false
        setBeginFiringEvents(false)
        state = State.Possible
    }

    override fun reset() {
        super.reset()
        handleReset()
    }

    override fun onStateChanged(recognizer: UIGestureRecognizer) {
        if (recognizer.state === State.Failed && state === State.Began) {
            stopListenForOtherStateChanges()
            fireActionEventIfCanRecognizeSimultaneously()

        } else if (recognizer.inState(State.Began, State.Ended) && mStarted && inState(State.Possible, State.Began)) {
            stopListenForOtherStateChanges()
            removeMessages()
            state = State.Failed
            setBeginFiringEvents(false)
            mStarted = false
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

        val tracker = mVelocityTracker!!

        when (action) {

            MotionEvent.ACTION_POINTER_DOWN -> {
                mLastFocusLocation.set(mCurrentLocation)
                mDownFocusLocation.set(mLastFocusLocation)

                if (mDown && state == State.Possible) {
                    if (numberOfTouches in minimumNumberOfTouches..maximumNumberOfTouches) {
                        computeFocusPoint(event, mStartLocation)
                    } else
                        if (numberOfTouches > maximumNumberOfTouches) {
                            state = State.Failed
                            removeMessages(MESSAGE_RESET)
                        }
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                mLastFocusLocation.set(mCurrentLocation)
                mDownFocusLocation.set(mLastFocusLocation)

                tracker.computeCurrentVelocity(1000, maximumFlingVelocity.toFloat())
                val upIndex = event.actionIndex

                val id1 = event.getPointerId(upIndex)
                val x1 = tracker.getXVelocity(id1)
                val y1 = tracker.getYVelocity(id1)
                for (i in 0 until numberOfTouches) {
                    if (i == upIndex) {
                        continue
                    }

                    val id2 = event.getPointerId(i)
                    val x = x1 * tracker.getXVelocity(id2)
                    val y = y1 * tracker.getYVelocity(id2)

                    val dot = x + y

                    if (dot < 0) {
                        tracker.clear()
                        break
                    }
                }

                if (mDown && state == State.Possible) {
                    computeFocusPoint(event, mStartLocation)
                    if (numberOfTouches - 1 < minimumNumberOfTouches) {
                        state = State.Failed
                        removeMessages(MESSAGE_RESET)
                    }
                }
            }

            MotionEvent.ACTION_DOWN -> {
                if (!mStarted && !delegate?.shouldReceiveTouch?.invoke(this)!!) {
                    mDown = false
                    mStarted = false
                    return cancelsTouchesInView
                }

                mLastFocusLocation.set(mCurrentLocation)
                mDownFocusLocation.set(mLastFocusLocation)
                mStartLocation.set(mCurrentLocation)

                tracker.clear()
                tracker.addMovement(event)

                mStarted = false
                mDown = true

                stopListenForOtherStateChanges()
                removeMessages(MESSAGE_RESET)
                state = State.Possible
                setBeginFiringEvents(false)
            }

            MotionEvent.ACTION_MOVE -> {
                scrollX = mLastFocusLocation.x - mCurrentLocation.x
                scrollY = mLastFocusLocation.y - mCurrentLocation.y
                tracker.addMovement(event)

                if (state == State.Possible && !mStarted && mDown) {
                    val distance = mCurrentLocation.distance(mDownFocusLocation)

                    logMessage(Log.VERBOSE, "distance: $distance > $scaledTouchSlop")

                    if (distance > scaledTouchSlop) {
                        tracker.computeCurrentVelocity(1000, maximumFlingVelocity.toFloat())
                        yVelocity = tracker.yVelocity
                        xVelocity = tracker.xVelocity

                        translationX -= scrollX
                        translationY -= scrollY

                        mLastFocusLocation.set(mCurrentLocation)
                        mStarted = true

                        logMessage(Log.VERBOSE, "touches: $numberOfTouches in $minimumNumberOfTouches .. $maximumNumberOfTouches")

                        if (numberOfTouches in minimumNumberOfTouches..maximumNumberOfTouches && delegate?.shouldBegin?.invoke(this)!!) {
                            state = State.Began

                            if (null == requireFailureOf) {
                                fireActionEventIfCanRecognizeSimultaneously()
                            } else {
                                when {
                                    requireFailureOf!!.state == State.Failed -> fireActionEventIfCanRecognizeSimultaneously()
                                    requireFailureOf!!.inState(State.Began, State.Ended, State.Changed) -> state =
                                            State.Failed
                                    else -> {
                                        listenForOtherStateChanges()
                                        setBeginFiringEvents(false)
                                        logMessage(Log.DEBUG, "waiting...")
                                    }
                                }
                            }
                        } else {
                            state = State.Failed
                        }
                    }
                } else if (inState(State.Began, State.Changed) && mDown) {
                    translationX -= scrollX
                    translationY -= scrollY

                    val pointerId = event.getPointerId(0)
                    tracker.computeCurrentVelocity(1000, maximumFlingVelocity.toFloat())
                    yVelocity = tracker.getYVelocity(pointerId)
                    xVelocity = tracker.getXVelocity(pointerId)

                    if (hasBeganFiringEvents()) {
                        state = State.Changed
                        fireActionEvent()
                    }

                    mLastFocusLocation.set(mCurrentLocation)
                }
            }

            MotionEvent.ACTION_UP -> {
                if (inState(State.Began, State.Changed)) {
                    if (state == State.Changed) {
                        scrollX = mLastFocusLocation.x - mCurrentLocation.x
                        scrollY = mLastFocusLocation.y - mCurrentLocation.y
                        translationX -= scrollX
                        translationY -= scrollY
                    }

                    val began = hasBeganFiringEvents()
                    state = State.Ended
                    if (began) {
                        fireActionEvent()
                    }
                }

                if (state == State.Possible || !mStarted) {
                    yVelocity = 0f
                    xVelocity = yVelocity
                } else {
                    // TODO: verify this. VelocityTracker seems to send random values
                    // VelocityTracker velocityTracker = mVelocityTracker;
                    // final int pointerId = ev.getPointerId(0);
                    // velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                    // mVelocityY = velocityTracker.getYVelocity(pointerId);
                    // mVelocityX = velocityTracker.getXVelocity(pointerId);
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker!!.recycle()
                    mVelocityTracker = null
                }

                mHandler.sendEmptyMessage(MESSAGE_RESET)
            }

            MotionEvent.ACTION_CANCEL -> {
                removeMessages(MESSAGE_RESET)
                state = State.Cancelled
                setBeginFiringEvents(false)
                mHandler.sendEmptyMessage(MESSAGE_RESET)
            }

            else -> {
            }
        }

        return cancelsTouchesInView
    }

    private fun fireActionEventIfCanRecognizeSimultaneously() {
        if (inState(State.Changed, State.Ended)) {
            setBeginFiringEvents(true)
            fireActionEvent()
        } else {
            if (delegate!!.shouldRecognizeSimultaneouslyWithGestureRecognizer(this)) {
                setBeginFiringEvents(true)
                fireActionEvent()
            }
        }
    }

    override fun hasBeganFiringEvents(): Boolean {
        return super.hasBeganFiringEvents() && inState(State.Began, State.Changed)
    }

    override fun removeMessages() {
        removeMessages(MESSAGE_RESET)
    }

    companion object {
        private const val MESSAGE_RESET = 4
    }
}