package it.sephiroth.android.library.uigestures

import android.content.Context
import android.graphics.PointF
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import kotlin.math.max

/**
 * UILongPressGestureRecognizer looks for long-press gestures. The user must
 * press one or more fingers on a view and hold them there for a minimum period of time before the action triggers. While down,
 * the user’s fingers may not move more than a specified distance; if they move beyond the specified distance, the gesture fails.
 *
 * @author alessandro crugnola
 * @see [
 * https://developer.apple.com/reference/uikit/uilongpressgesturerecognizer](https://developer.apple.com/reference/uikit/uilongpressgesturerecognizer)
 */
@Suppress("MemberVisibilityCanBePrivate")
open class UILongPressGestureRecognizer(context: Context) : UIGestureRecognizer(context), UIContinuousRecognizer {

    /**
     * Maximum allowed timeout between consecutive taps (when tapsRequired > 1)
     */
    var doubleTapTimeout = DOUBLE_TAP_TIMEOUT
        set(value) {
            field = value

            if (value > longPressTimeout) {
                longPressTimeout = value + TIMEOUT_DELAY_MILLIS
            }
        }

    override var numberOfTouches: Int = 0
        internal set

    /**
     * The minimum period fingers must press on the view for the gesture to be recognized.<br></br>
     * Value is in milliseconds
     * @since 1.0.0
     */
    var longPressTimeout = max(LONG_PRESS_TIMEOUT, doubleTapTimeout)

    /**
     * The number of required touches for this recognizer to succeed.<br></br>
     * Default value is 1
     * @since 1.0.0
     */
    var touchesRequired = 1

    /**
     * The number of required taps for this recognizer to succeed.<br></br>
     * Default value is 1
     * @since 1.0.0
     */
    var tapsRequired = 1


    private var mAlwaysInTapRegion: Boolean = false
    private var mStarted: Boolean = false
    private val mStartLocation = PointF()
    private val mDownFocusLocation = PointF()
    private var mNumTaps = 1
    private var mBegan: Boolean = false

    val startLocationX: Float
        get() = mStartLocation.x

    val startLocationY: Float
        get() = mStartLocation.y

    /**
     * @return The maximum allowed movement of the fingers on the view before the gesture fails.
     * @since 1.2.4
     */
    var allowableMovement: Int

    /**
     * Minimum movement before we start collecting movements
     * @since 1.2.5
     */
    var scaledTouchSlop: Int

    /**
     * the duration in milliseconds between the first tap's up event and the second tap's down
     * event for an interaction to be considered a double-tap
     * @since 1.2.5
     */
    var scaledDoubleTapSlop: Int

    init {
        mStarted = false
        mBegan = false

        val configuration = ViewConfiguration.get(context)

        scaledTouchSlop = configuration.scaledTouchSlop
        scaledDoubleTapSlop = configuration.scaledDoubleTapSlop
        allowableMovement = configuration.scaledTouchSlop

        if (logEnabled) {
            logMessage(Log.INFO, "allowableMovement: $allowableMovement")
            logMessage(Log.INFO, "scaledTouchSlop: $scaledTouchSlop")
            logMessage(Log.INFO, "scaledDoubleTapSlop: $scaledDoubleTapSlop")
            logMessage(Log.INFO, "longPressTimeout: $longPressTimeout")
            logMessage(Log.INFO, "doubleTapTimeout: $doubleTapTimeout")
        }
    }

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            MESSAGE_RESET -> {
                logMessage(Log.INFO, "handleMessage(MESSAGE_RESET)")
                handleReset()
            }

            MESSAGE_FAILED -> {
                logMessage(Log.INFO, "handleMessage(MESSAGE_FAILED)")
                handleFailed()
            }

            MESSAGE_POINTER_UP -> {
                logMessage(Log.INFO, "handleMessage(MESSAGE_POINTER_UP)")
                numberOfTouches = msg.arg1
            }

            MESSAGE_LONG_PRESS -> {
                logMessage(Log.INFO, "handleMessage(MESSAGE_LONG_PRESS)")
                handleLongPress()
            }

            else -> {
            }
        }
    }

    override fun onStateChanged(recognizer: UIGestureRecognizer) {
        logMessage(Log.VERBOSE, "onStateChanged(${recognizer.state?.name}, started: $mStarted)")

        if (recognizer.state === State.Failed && state === State.Began) {
            stopListenForOtherStateChanges()
            fireActionEventIfCanRecognizeSimultaneously()

            if (mBegan && hasBeganFiringEvents()) {
                state = State.Changed
            }

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
        val count = event.pointerCount

        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                if (!mStarted && !delegate?.shouldReceiveTouch?.invoke(this)!!) {
                    return cancelsTouchesInView
                }

                removeMessages()

                mAlwaysInTapRegion = true
                numberOfTouches = count
                mBegan = false

                if (!mStarted) {
                    stopListenForOtherStateChanges()
                    state = State.Possible
                    setBeginFiringEvents(false)
                    mNumTaps = 1
                    mStarted = true
                } else {
                    mNumTaps++

                    // if second tap is too far wawy from the first
                    val distance = mDownLocation.distance(mPreviousDownLocation)
                    logMessage(Log.VERBOSE, "distance: $distance")
                    if (distance > scaledDoubleTapSlop) {
                        logMessage(Log.WARN, "second touch too far away ($distance > $scaledDoubleTapSlop)")
                        handleFailed()
                        return cancelsTouchesInView
                    }
                }

                logMessage(Log.VERBOSE, "num taps: $mNumTaps, tapsRequired: $tapsRequired")

                if (mNumTaps == tapsRequired) {
                    mHandler.sendEmptyMessageAtTime(MESSAGE_LONG_PRESS, event.downTime + longPressTimeout)
                } else {
                    delayedFail()
                }

                mDownFocusLocation.set(mCurrentLocation)
                mStartLocation.set(mCurrentLocation)
            }

            MotionEvent.ACTION_POINTER_DOWN -> if (state == State.Possible && mStarted) {
                removeMessages(MESSAGE_POINTER_UP)
                numberOfTouches = count

                if (numberOfTouches > 1) {
                    if (numberOfTouches > touchesRequired) {
                        removeMessages()
                        state = State.Failed
                    }
                }

                mDownFocusLocation.set(mCurrentLocation)
                computeFocusPoint(event, mStartLocation)

            } else if (inState(State.Began, State.Changed) && mStarted) {
                numberOfTouches = count
            }

            MotionEvent.ACTION_POINTER_UP -> if (state == State.Possible && mStarted) {
                removeMessages(MESSAGE_POINTER_UP)

                mDownFocusLocation.set(mCurrentLocation)

                val message = mHandler.obtainMessage(MESSAGE_POINTER_UP)
                message.arg1 = numberOfTouches - 1
                mHandler.sendMessageDelayed(message, UIGestureRecognizer.TAP_TIMEOUT)

                computeFocusPoint(event, mStartLocation)


            } else if (inState(State.Began, State.Changed)) {
                if (numberOfTouches - 1 < touchesRequired) {
                    val began = hasBeganFiringEvents()
                    state = State.Ended

                    if (began) {
                        fireActionEvent()
                    }

                    setBeginFiringEvents(false)
                }
            }

            MotionEvent.ACTION_MOVE -> if (state == State.Possible && mStarted) {

                if (mAlwaysInTapRegion) {
                    val distance = mCurrentLocation.distance(mDownFocusLocation)
                    logMessage(Log.VERBOSE, "distance: $distance, allowableMovement: $allowableMovement")

                    if (distance > allowableMovement) {
                        logMessage(Log.WARN, "moved too much!: $distance > $allowableMovement")
                        mAlwaysInTapRegion = false
                        removeMessages()
                        state = State.Failed
                    }
                }
            } else if (state == State.Began) {
                if (!mBegan) {
                    val distance = mCurrentLocation.distance(mDownFocusLocation)
                    logMessage(Log.VERBOSE, "distance: $distance, allowableMovement: $scaledTouchSlop")

                    if (distance > scaledTouchSlop) {
                        mBegan = true

                        if (hasBeganFiringEvents()) {
                            state = State.Changed
                            fireActionEvent()
                        }
                    }
                }
            } else if (state == State.Changed) {
                state = State.Changed
                if (hasBeganFiringEvents()) {
                    fireActionEvent()
                }
            }

            MotionEvent.ACTION_UP -> {
                removeMessages(MESSAGE_RESET, MESSAGE_POINTER_UP, MESSAGE_LONG_PRESS)

                if (state == State.Possible && mStarted) {
                    if (numberOfTouches != touchesRequired) {
                        mStarted = false
                        removeMessages()
                        state = State.Failed
                        postReset()
                    } else {
                        if (mNumTaps < tapsRequired) {
                            removeMessages(MESSAGE_FAILED)
                            delayedFail()
                        } else {
                            mNumTaps = 1
                            mStarted = false
                            removeMessages()
                            state = State.Failed
                        }
                    }
                } else if (inState(State.Began, State.Changed)) {
                    mNumTaps = 1
                    mStarted = false
                    val began = hasBeganFiringEvents()
                    state = State.Ended
                    if (began) {
                        fireActionEvent()
                    }
                    postReset()
                } else {
                    mStarted = false
                    postReset()
                }
                setBeginFiringEvents(false)
            }

            MotionEvent.ACTION_CANCEL -> {
                removeMessages()
                mStarted = false
                mNumTaps = 1
                state = State.Cancelled
                postReset()
            }

            else -> {
            }
        }

        return cancelsTouchesInView
    }

    override fun removeMessages() {
        removeMessages(MESSAGE_FAILED, MESSAGE_RESET, MESSAGE_POINTER_UP, MESSAGE_LONG_PRESS)
    }

    private fun postReset() {
        mHandler.sendEmptyMessage(MESSAGE_RESET)
    }

    private fun delayedFail() {
        mHandler.sendEmptyMessageDelayed(MESSAGE_FAILED, doubleTapTimeout)
    }

    private fun handleFailed() {
        removeMessages()
        state = State.Failed
        setBeginFiringEvents(false)
        mStarted = false
    }

    private fun handleReset() {
        state = State.Possible
        mStarted = false
    }

    override fun reset() {
        super.reset()
        handleReset()
    }

    private fun handleLongPress() {
        logMessage(Log.INFO, "handleLongPress")

        removeMessages(MESSAGE_FAILED)

        if (state === State.Possible && mStarted) {
            if (numberOfTouches == touchesRequired && delegate?.shouldBegin?.invoke(this)!!) {
                state = State.Began
                if (null == requireFailureOf) {
                    fireActionEventIfCanRecognizeSimultaneously()
                } else {
                    when {
                        requireFailureOf!!.state === State.Failed -> fireActionEventIfCanRecognizeSimultaneously()
                        requireFailureOf!!.inState(State.Began, State.Changed, State.Ended) -> {
                            state = State.Failed
                            setBeginFiringEvents(false)
                            mStarted = false
                            mNumTaps = 1
                        }
                        else -> {
                            listenForOtherStateChanges()
                            setBeginFiringEvents(false)
                            logMessage(Log.DEBUG, "waiting...")
                        }
                    }
                }
            } else {
                state = State.Failed
                setBeginFiringEvents(false)
                mStarted = false
                mNumTaps = 1
            }
        }
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

    companion object {

        // request to change the current state to Failed
        private const val MESSAGE_FAILED = 1
        // request to change the current state to Possible
        private const val MESSAGE_RESET = 2
        // we handle the action_pointer_up received in the onTouchEvent with a delay
        // in order to check how many fingers were actually down when we're checking them
        // in the action_up.
        private const val MESSAGE_POINTER_UP = 3
        // post handle the long press event
        private const val MESSAGE_LONG_PRESS = 4
    }

}
