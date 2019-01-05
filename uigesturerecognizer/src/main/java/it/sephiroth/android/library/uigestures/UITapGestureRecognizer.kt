package it.sephiroth.android.library.uigestures

import android.content.Context
import android.graphics.PointF
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import kotlin.math.sqrt

/**
 * UITapGestureRecognizer looks for single or multiple taps.
 * For the gesture to be recognized, the specified number of fingers must tap the view a specified number of times.
 *
 * @author alessandro crugnola
 * @see [
 * https://developer.apple.com/reference/uikit/uitapgesturerecognizer](https://developer.apple.com/reference/uikit/uitapgesturerecognizer)
 */
@Suppress("MemberVisibilityCanBePrivate")
open class UITapGestureRecognizer(context: Context) : UIGestureRecognizer(context), UIDiscreteGestureRecognizer {

    /**
     * Change the number of required touches for this recognizer to succeed.<br></br>
     * Default value is 1
     *
     * @since 1.0.0
     */
    var touchesRequired = 1

    override var numberOfTouches: Int = 0
        internal set

    /**
     * Change the number of required taps for this recognizer to succeed.<br></br>
     * Default value is 1
     *
     * @since 1.0.0
     */
    var tapsRequired = 1

    var tapTimeout: Long = TAP_TIMEOUT

    var doubleTapTimeout: Long = DOUBLE_TAP_TIMEOUT


    private var mAlwaysInTapRegion: Boolean = false
    private var mDownFocus = PointF()
    private val mTouchSlopSquare: Int
    private val mDoubleTapTouchSlopSquare: Int
    private var mStarted: Boolean = false
    private var mNumTaps = 0

    init {
        mStarted = false
        val configuration = ViewConfiguration.get(context)
        val touchSlop: Int = configuration.scaledTouchSlop
        val doubleTapTouchSlop: Int = configuration.scaledDoubleTapSlop

        logMessage(Log.INFO, "touchSlop: $touchSlop")
        logMessage(Log.INFO, "doubleTapTouchSlop: $doubleTapTouchSlop")

        mTouchSlopSquare = touchSlop
        mDoubleTapTouchSlopSquare = doubleTapTouchSlop

        if (logEnabled) {
            logMessage(Log.INFO, "tapTimeout: $tapTimeout")
            logMessage(Log.INFO, "doubleTapTimeout: $doubleTapTimeout")
            logMessage(Log.INFO, "touchSlopSquare: $mTouchSlopSquare")
            logMessage(Log.INFO, "doubleTapTouchSlopSquare: $mDoubleTapTouchSlopSquare")
            logMessage(Log.INFO, "touchSlop: $touchSlop")
        }
    }

    /**
     * Returns the maximum movement allowed before the recognizer fails
     */
    val touchSlop: Int get() = if (tapsRequired > 1) mDoubleTapTouchSlopSquare else mTouchSlopSquare

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
                handleFailed()
            }

            else -> {
            }
        }
    }

    override fun onStateChanged(recognizer: UIGestureRecognizer) {
        if (UIGestureRecognizer.logEnabled) {
            logMessage(Log.VERBOSE, "onStateChanged(${recognizer.state?.name})")
            logMessage(Log.VERBOSE, "mStarted: $mStarted")
        }

        if (recognizer.state === State.Failed && state === State.Ended) {
            stopListenForOtherStateChanges()
            fireActionEventIfCanRecognizeSimultaneously()
            postReset()
        } else if (recognizer.inState(State.Began, State.Ended) && mStarted && inState(State.Possible, State.Ended)) {
            stopListenForOtherStateChanges()
            removeMessages()
            state = State.Failed
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

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                if (!mStarted && !delegate?.shouldReceiveTouch?.invoke(this)!!) {
                    return cancelsTouchesInView
                }

                removeMessages()
                mAlwaysInTapRegion = true
                numberOfTouches = count

                state = State.Possible
                setBeginFiringEvents(false)

                if (!mStarted) {
                    stopListenForOtherStateChanges()
                    mNumTaps = 0
                    mStarted = true
                }

                mHandler.sendEmptyMessageDelayed(MESSAGE_LONG_PRESS, tapTimeout + TIMEOUT_DELAY_MILLIS)

                mNumTaps++
                mDownFocus.set(mCurrentLocation)
            }

            MotionEvent.ACTION_POINTER_DOWN -> if (state == State.Possible && mStarted) {
                removeMessages(MESSAGE_POINTER_UP)
                numberOfTouches = count

                if (numberOfTouches > 1) {
                    if (numberOfTouches > touchesRequired) {
                        logMessage(Log.WARN, "too many touches: $numberOfTouches > $touchesRequired")
                        state = State.Failed
                    }
                }

                mDownFocus.set(mCurrentLocation)
            }

            MotionEvent.ACTION_POINTER_UP -> if (state == State.Possible && mStarted) {
                removeMessages(MESSAGE_FAILED, MESSAGE_RESET, MESSAGE_POINTER_UP)
                mDownFocus.set(mCurrentLocation)

                val message = mHandler.obtainMessage(MESSAGE_POINTER_UP)
                message.arg1 = numberOfTouches - 1
                mHandler.sendMessageDelayed(message, UIGestureRecognizer.TAP_TIMEOUT)
            }

            MotionEvent.ACTION_MOVE -> if (state == State.Possible && mStarted) {
                if (mAlwaysInTapRegion) {
                    val distance = mDownFocus.distance(mCurrentLocation)

                    logMessage(Log.VERBOSE, "distance: $distance, slop: $touchSlop")

                    if (distance > touchSlop) {
                        logMessage(Log.WARN, "moved too much")
                        mAlwaysInTapRegion = false
                        removeMessages()
                        state = State.Failed
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                removeMessages(MESSAGE_RESET, MESSAGE_POINTER_UP, MESSAGE_LONG_PRESS)

                if (state == State.Possible && mStarted) {
                    if (numberOfTouches != touchesRequired) {
                        logMessage(Log.WARN, "number touches not correct: $numberOfTouches != $touchesRequired")
                        handleFailed()
                    } else {
                        if (mNumTaps < tapsRequired) {
                            delayedFail()
                        } else {
                            // nailed!
                            if (delegate?.shouldBegin?.invoke(this)!!) {
                                state = State.Ended

                                if (null == requireFailureOf) {
                                    fireActionEventIfCanRecognizeSimultaneously()
                                    postReset()
                                } else {
                                    when {
                                        requireFailureOf!!.state === State.Failed -> {
                                            fireActionEventIfCanRecognizeSimultaneously()
                                            postReset()
                                        }
                                        requireFailureOf!!.inState(State.Began, State.Ended, State.Changed) -> state =
                                                State.Failed
                                        else -> {
                                            listenForOtherStateChanges()
                                        }
                                    }
                                }
                            } else {
                                state = State.Failed
                            }
                            mStarted = false
                        }
                    }
                } else {
                    handleReset()
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                removeMessages()
                mStarted = false
                state = State.Cancelled
                setBeginFiringEvents(false)
                postReset()
            }

            else -> {
            }
        }
        return cancelsTouchesInView
    }

    private fun fireActionEventIfCanRecognizeSimultaneously() {
        if (delegate?.shouldRecognizeSimultaneouslyWithGestureRecognizer(this)!!) {
            setBeginFiringEvents(true)
            fireActionEvent()
        }
    }

    override fun hasBeganFiringEvents(): Boolean {
        return super.hasBeganFiringEvents() && inState(State.Ended)
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
        state = State.Failed
        setBeginFiringEvents(false)
        removeMessages()
        mStarted = false
    }

    private fun handleReset() {
        state = State.Possible
        setBeginFiringEvents(false)
        mStarted = false
    }

    override fun reset() {
        super.reset()
        handleReset()
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
        // a long press will make this gesture to fail
        private const val MESSAGE_LONG_PRESS = 4
    }
}
