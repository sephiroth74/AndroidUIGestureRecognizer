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
class UIPanGestureRecognizer(context: Context) : UIGestureRecognizer(context), UIContinuousRecognizer {
    private val mTouchSlopSquare: Int
    val minimumFlingVelocity: Int
    val maximumFlingVelocity: Int

    private var mStarted: Boolean = false

    private var mLastFocusX: Float = 0.toFloat()
    private var mLastFocusY: Float = 0.toFloat()
    private var mDownFocusX: Float = 0.toFloat()
    private var mDownFocusY: Float = 0.toFloat()

    private var mVelocityTracker: VelocityTracker? = null
    /**
     * The minimum number of fingers that can be touching the view for this gesture to be recognized.
     * The default value is 1
     *
     * @since 1.0.0
     */
    var minimumNumberOfTouches: Int = 0

    /**
     * The maximum number of fingers that can be touching the view for this gesture to be recognized.
     * @since 1.0.0
     */
    var maximumNumberOfTouches: Int = 0

    var scrollX: Float = 0.toFloat()
        private set

    var scrollY: Float = 0.toFloat()
        private set

    /**
     * @return the relative scroll x between gestures
     * @since 1.1.2
     */
    fun getRelativeScrollX(): Float {
        return -scrollX
    }

    /**
     * @return the relative scroll y between gestures
     * @since 1.1.2
     */
    fun getRelativeScrollY(): Float {
        return -scrollY
    }

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

    private val mCurrentLocation: PointF

    override var numberOfTouches: Int = 0
        private set

    val isFling: Boolean
        get() = state === UIGestureRecognizer.State.Ended && (Math.abs(xVelocity) > minimumFlingVelocity || Math.abs(yVelocity) > minimumFlingVelocity)

    override val currentLocationX: Float
        get() = mCurrentLocation.x

    override val currentLocationY: Float
        get() = mCurrentLocation.y

    init {
        minimumNumberOfTouches = 1
        maximumNumberOfTouches = Integer.MAX_VALUE

        val touchSlop: Int
        val configuration = ViewConfiguration.get(context)
        touchSlop = configuration.scaledTouchSlop
        minimumFlingVelocity = configuration.scaledMinimumFlingVelocity
        maximumFlingVelocity = configuration.scaledMaximumFlingVelocity
        mTouchSlopSquare = touchSlop * touchSlop
        mCurrentLocation = PointF()
    }

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            MESSAGE_RESET -> {
                mStarted = false
                setBeginFiringEvents(false)
                state = UIGestureRecognizer.State.Possible
            }
            else -> {
            }
        }
    }

    override fun onStateChanged(recognizer: UIGestureRecognizer) {
        logMessage(Log.VERBOSE, "onStateChanged(%s, %s)", recognizer, recognizer.state?.name!!)
        logMessage(Log.VERBOSE, "started: %b, state: %s", mStarted, state?.name!!)

        if (recognizer.state === UIGestureRecognizer.State.Failed && state === UIGestureRecognizer.State.Began) {
            stopListenForOtherStateChanges()
            fireActionEventIfCanRecognizeSimultaneously()

        } else if (recognizer.inState(UIGestureRecognizer.State.Began, UIGestureRecognizer.State.Ended) && mStarted && inState(UIGestureRecognizer.State.Possible, UIGestureRecognizer.State.Began)) {
            stopListenForOtherStateChanges()
            removeMessages()
            state = UIGestureRecognizer.State.Failed
            setBeginFiringEvents(false)
            mStarted = false
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

        numberOfTouches = count

        when (action and MotionEvent.ACTION_MASK) {

            MotionEvent.ACTION_POINTER_DOWN -> {
                mLastFocusX = focusX
                mDownFocusX = mLastFocusX
                mLastFocusY = focusY
                mDownFocusY = mLastFocusY

                if (state === UIGestureRecognizer.State.Possible) {
                    if (count > maximumNumberOfTouches) {
                        state = UIGestureRecognizer.State.Failed
                        removeMessages(MESSAGE_RESET)
                    }
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                mLastFocusX = focusX
                mDownFocusX = mLastFocusX
                mLastFocusY = focusY
                mDownFocusY = mLastFocusY
                numberOfTouches = count - 1

                mVelocityTracker!!.computeCurrentVelocity(1000, maximumFlingVelocity.toFloat())
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

                if (state === UIGestureRecognizer.State.Possible) {
                    if (count - 1 < minimumNumberOfTouches) {
                        state = UIGestureRecognizer.State.Failed
                        removeMessages(MESSAGE_RESET)
                    }
                }
            }

            MotionEvent.ACTION_DOWN -> {
                mLastFocusX = focusX
                mDownFocusX = mLastFocusX
                mLastFocusY = focusY
                mDownFocusY = mLastFocusY

                mVelocityTracker!!.clear()
                mVelocityTracker!!.addMovement(event)

                mStarted = false

                stopListenForOtherStateChanges()
                removeMessages(MESSAGE_RESET)
                state = UIGestureRecognizer.State.Possible
                setBeginFiringEvents(false)
            }

            MotionEvent.ACTION_MOVE -> {
                scrollX = mLastFocusX - focusX
                scrollY = mLastFocusY - focusY

                mVelocityTracker!!.addMovement(event)

                if (state === UIGestureRecognizer.State.Possible && !mStarted) {
                    val deltaX = (focusX - mDownFocusX).toInt()
                    val deltaY = (focusY - mDownFocusY).toInt()
                    val distance = deltaX * deltaX + deltaY * deltaY
                    if (distance > mTouchSlopSquare) {

                        mVelocityTracker!!.computeCurrentVelocity(1000, maximumFlingVelocity.toFloat())
                        yVelocity = mVelocityTracker!!.yVelocity
                        xVelocity = mVelocityTracker!!.xVelocity

                        translationX -= scrollX
                        translationY -= scrollY

                        mLastFocusX = focusX
                        mLastFocusY = focusY
                        mStarted = true

                        if (count in minimumNumberOfTouches..maximumNumberOfTouches && delegate?.shouldBegin(this)!!) {
                            state = UIGestureRecognizer.State.Began

                            if (null == requireFailureOf) {
                                fireActionEventIfCanRecognizeSimultaneously()
                            } else {
                                when {
                                    requireFailureOf!!.state === UIGestureRecognizer.State.Failed -> fireActionEventIfCanRecognizeSimultaneously()
                                    requireFailureOf!!.inState(UIGestureRecognizer.State.Began, UIGestureRecognizer.State.Ended, UIGestureRecognizer.State.Changed) -> state = UIGestureRecognizer.State.Failed
                                    else -> {
                                        listenForOtherStateChanges()
                                        setBeginFiringEvents(false)
                                        logMessage(Log.DEBUG, "waiting...")
                                    }
                                }
                            }
                        } else {
                            state = UIGestureRecognizer.State.Failed
                        }
                    }
                } else if (inState(UIGestureRecognizer.State.Began, UIGestureRecognizer.State.Changed)) {
                    //if ((Math.abs(scrollX) >= 1 || Math.abs(scrollY) >= 1)) {
                    translationX -= scrollX
                    translationY -= scrollY

                    val pointerId = event.getPointerId(0)
                    mVelocityTracker!!.computeCurrentVelocity(1000, maximumFlingVelocity.toFloat())
                    yVelocity = mVelocityTracker!!.getYVelocity(pointerId)
                    xVelocity = mVelocityTracker!!.getXVelocity(pointerId)

                    if (hasBeganFiringEvents()) {
                        state = UIGestureRecognizer.State.Changed
                        fireActionEvent()
                    }

                    mLastFocusX = focusX
                    mLastFocusY = focusY
                }
            }

            MotionEvent.ACTION_UP -> {

                if (inState(UIGestureRecognizer.State.Began, UIGestureRecognizer.State.Changed)) {
                    val began = hasBeganFiringEvents()
                    state = UIGestureRecognizer.State.Ended
                    if (began) {
                        fireActionEvent()
                    }
                }

                if (state === UIGestureRecognizer.State.Possible || !mStarted) {
                    yVelocity = 0f
                    xVelocity = yVelocity
                } else {
                    // TODO: verify this. it seems to send random values here
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
                state = UIGestureRecognizer.State.Cancelled
                setBeginFiringEvents(false)
                mHandler.sendEmptyMessage(MESSAGE_RESET)
            }

            else -> {
            }
        }

        return cancelsTouchesInView
    }

    private fun fireActionEventIfCanRecognizeSimultaneously() {
        if (inState(UIGestureRecognizer.State.Changed, UIGestureRecognizer.State.Ended)) {
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
        return super.hasBeganFiringEvents() && inState(UIGestureRecognizer.State.Began, UIGestureRecognizer.State.Changed)
    }

    override fun removeMessages() {
        removeMessages(MESSAGE_RESET)
    }

    companion object {
        private const val MESSAGE_RESET = 4
    }
}