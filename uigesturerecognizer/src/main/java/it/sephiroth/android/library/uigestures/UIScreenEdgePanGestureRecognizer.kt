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
open class UIScreenEdgePanGestureRecognizer(context: Context) : UIGestureRecognizer(context), UIContinuousRecognizer {

    /**
     * The minimum number of fingers that can be touching the view for this gesture to be recognized.
     * The default value is 1
     *
     * @since 1.0.0
     */
    var minimumNumberOfTouches: Int = 1

    /**
     * @ The maximum number of fingers that can be touching the view for this gesture to be recognized.
     * @since 1.0.0
     */
    var maximumNumberOfTouches: Int = Integer.MAX_VALUE

    var scrollX: Float = 0.toFloat()
        private set

    var scrollY: Float = 0.toFloat()
        private set

    /**
     * @since 1.0.0
     */
    var translationX: Float = 0.toFloat()
        private set

    /**
     * @since 1.0.0
     */
    var translationY: Float = 0.toFloat()
        private set

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
     * @return the relative scroll x between gestures
     * @since 1.1.2
     */
    val relativeScrollX: Float get() = -scrollX

    /**
     * @return the relative scroll y between gestures
     * @since 1.0.0
     */
    val relativeScrollY: Float get() = -scrollY

    /**
     * Screen sdge to be considered for this gesture to be recognized
     * @since 1.0.0
     */
    var edge = UIRectEdge.LEFT

    /**
     * Minimum finger movement before the touch can be considered a pan
     * @since 1.2.5
     */
    var scaledTouchSlop: Int

    /**
     * Edge limits (in pixels) after which the gesture will fail
     */
    var edgeLimit: Float

    private var mStarted: Boolean = false
    private var mDown: Boolean = false
    private var mVelocityTracker: VelocityTracker? = null
    private var mLastFocusLocation = PointF()
    private var mDownFocusLocation = PointF()


    init {
        val configuration = ViewConfiguration.get(context)
        scaledTouchSlop = configuration.scaledTouchSlop
        edgeLimit = context.resources.getDimension(R.dimen.gestures_screen_edge_limit)
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
        mDown = false
        setBeginFiringEvents(false)
        state = State.Possible
    }

    override fun onStateChanged(recognizer: UIGestureRecognizer) {
        if (recognizer.state === State.Failed && state === State.Began) {
            stopListenForOtherStateChanges()
            fireActionEventIfCanRecognizeSimultaneously()

        } else if (recognizer.inState(State.Began, State.Ended) &&
                mStarted && mDown && inState(State.Possible, State.Began)) {
            stopListenForOtherStateChanges()
            removeMessages()
            state = State.Failed
            setBeginFiringEvents(false)
            mStarted = false
            mDown = false
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


        val rawX = event.rawX
        val rawY = event.rawY

        when (action) {

            MotionEvent.ACTION_POINTER_DOWN -> {
                mLastFocusLocation.set(mCurrentLocation)
                mDownFocusLocation.set(mCurrentLocation)

                if (state == State.Possible) {
                    if (numberOfTouches > maximumNumberOfTouches) {
                        state = State.Failed
                        removeMessages(MESSAGE_RESET)
                    }
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                mLastFocusLocation.set(mCurrentLocation)
                mDownFocusLocation.set(mCurrentLocation)

                mVelocityTracker!!.computeCurrentVelocity(1000, 0f)
                val upIndex = event.actionIndex

                val id1 = event.getPointerId(upIndex)
                val x1 = mVelocityTracker!!.getXVelocity(id1)
                val y1 = mVelocityTracker!!.getYVelocity(id1)
                for (i in 0 until event.pointerCount) {
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

                if (state == State.Possible) {
                    if (numberOfTouches < minimumNumberOfTouches) {
                        state = State.Failed
                        removeMessages(MESSAGE_RESET)
                    }
                }
            }

            MotionEvent.ACTION_DOWN -> {
                if (delegate?.shouldReceiveTouch?.invoke(this)!!) {
                    mLastFocusLocation.set(mCurrentLocation)
                    mDownFocusLocation.set(mCurrentLocation)

                    mVelocityTracker!!.clear()
                    mVelocityTracker!!.addMovement(event)

                    mStarted = false
                    mDown = true

                    stopListenForOtherStateChanges()
                    removeMessages(MESSAGE_RESET)

                    state = if (!computeState(rawX, rawY)) {
                        logMessage(Log.WARN, "outside edge limits")
                        State.Failed
                    } else {
                        State.Possible
                    }

                    setBeginFiringEvents(false)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                scrollX = mLastFocusLocation.x - mCurrentLocation.x
                scrollY = mLastFocusLocation.y - mCurrentLocation.y

                mVelocityTracker!!.addMovement(event)

                if (mDown && state == State.Possible && !mStarted) {
                    val distance = mCurrentLocation.distance(mDownFocusLocation)

                    if (distance > scaledTouchSlop) {

                        mVelocityTracker!!.computeCurrentVelocity(1000, java.lang.Float.MAX_VALUE)
                        yVelocity = mVelocityTracker!!.yVelocity
                        xVelocity = mVelocityTracker!!.xVelocity

                        logMessage(Log.INFO, "velocity: $xVelocity, $yVelocity")

                        translationX -= scrollX
                        translationY -= scrollY

                        mLastFocusLocation.set(mCurrentLocation)
                        mStarted = true

                        if (numberOfTouches in minimumNumberOfTouches..maximumNumberOfTouches
                                && delegate?.shouldBegin?.invoke(this)!!
                                && getTouchDirection(mDownFocusLocation.x, mDownFocusLocation.y,
                                        mCurrentLocation.x, mCurrentLocation.y, xVelocity, yVelocity) == edge) {

                            state = State.Began

                            if (null == requireFailureOf) {
                                fireActionEventIfCanRecognizeSimultaneously()
                            } else {
                                when {
                                    requireFailureOf!!.state === State.Failed -> fireActionEventIfCanRecognizeSimultaneously()
                                    requireFailureOf!!.inState(State.Began, State.Ended, State.Changed) ->
                                        state = State.Failed
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
                } else if (inState(State.Began, State.Changed)) {
                    translationX -= scrollX
                    translationY -= scrollY

                    val pointerId = event.getPointerId(0)
                    mVelocityTracker!!.computeCurrentVelocity(1000, java.lang.Float.MAX_VALUE)
                    yVelocity = mVelocityTracker!!.getYVelocity(pointerId)
                    xVelocity = mVelocityTracker!!.getXVelocity(pointerId)

                    if (hasBeganFiringEvents()) {
                        state = State.Changed
                        fireActionEvent()
                    }

                    mLastFocusLocation.set(mCurrentLocation)
                }
            }

            MotionEvent.ACTION_UP -> {
                mDown = false

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

    @Suppress("UNUSED_PARAMETER")
    private fun getTouchDirection(
            x1: Float, y1: Float, x2: Float, y2: Float, velocityX: Float, velocityY: Float): UIRectEdge {
        val diffY = y2 - y1
        val diffX = x2 - x1

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > 0.toFloat()) {
                return if (diffX > 0) {
                    UIRectEdge.LEFT
                } else {
                    UIRectEdge.RIGHT
                }
            }
        } else if (Math.abs(diffY) > 0.toFloat()) {
            return if (diffY > 0) {
                UIRectEdge.TOP
            } else {
                UIRectEdge.BOTTOM
            }
        }
        return UIRectEdge.NONE
    }

    private fun computeState(x: Float, y: Float): Boolean {
        val context = context ?: return false

        if (edge == UIRectEdge.LEFT && x > edgeLimit) {
            return false
        } else if (edge == UIRectEdge.RIGHT) {
            val w = context.resources.displayMetrics.widthPixels
            return x >= w - edgeLimit
        } else if (edge == UIRectEdge.TOP && y > edgeLimit) {
            return false
        } else if (edge == UIRectEdge.BOTTOM) {
            val h = context.resources.displayMetrics.heightPixels
            return y >= h - edgeLimit
        } else if (edge == UIRectEdge.NONE) {
            return false
        }
        return true
    }

    companion object {
        private const val MESSAGE_RESET = 4
    }
}