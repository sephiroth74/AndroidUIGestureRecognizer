package it.sephiroth.android.library.uigestures

import android.content.Context
import android.graphics.PointF
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration

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

    /**
     * the duration in milliseconds we will wait to see if a touch event is a tap or a scroll.
     * If the user does not move within this interval, it is considered to be a tap.
     * @since 1.2.5
     */
    var tapTimeout: Long = TAP_TIMEOUT

    /**
     * the duration in milliseconds between the first tap's up event and the second tap's
     * down event for an interaction to be considered a double-tap.
     * @since 1.2.5
     */
    var doubleTapTimeout: Long = DOUBLE_TAP_TIMEOUT

    /**
     * Distance in pixels a touch can wander before we think the user is scrolling
     * @since 1.2.5
     */
    var scaledTouchSlop: Int

    /**
     * Distance in pixels between the first touch and second
     * touch to still be considered a double tap
     */
    var scaledDoubleTapSlop: Int

    private var mAlwaysInTapRegion: Boolean = false
    private var mDownFocus = PointF()
    private var mStarted: Boolean = false
    private var mNumTaps = 0

    init {
        mStarted = false
        val configuration = ViewConfiguration.get(context)

        scaledTouchSlop = configuration.scaledTouchSlop
        scaledDoubleTapSlop = configuration.scaledDoubleTapSlop

        if (logEnabled) {
            logMessage(Log.INFO, "tapTimeout: $tapTimeout")
            logMessage(Log.INFO, "doubleTapTimeout: $doubleTapTimeout")
            logMessage(Log.INFO, "scaledTouchSlop: $scaledTouchSlop")
            logMessage(Log.INFO, "scaledDoubleTapSlop: $scaledDoubleTapSlop")
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

    private val mPreviousTapLocation = PointF()

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
                } else {

                    // if second tap is too far wawy from the first
                    // and only 1 finger is required
                    if (touchesRequired == 1 && tapsRequired > 1) {
                        val distance = mDownLocation.distance(mPreviousDownLocation)
                        logMessage(Log.VERBOSE, "distance: $distance")
                        if (distance > scaledDoubleTapSlop) {
                            logMessage(Log.WARN, "second touch too far away ($distance > $scaledDoubleTapSlop)")
                            handleFailed()
                            return cancelsTouchesInView
                        }
                    }
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

                    } else if (numberOfTouches == touchesRequired && tapsRequired > 1) {
                        // let's check if the current tap is too far away from the previous
                        if (mNumTaps < tapsRequired) {
                            mPreviousTapLocation.set(mCurrentLocation)
                        } else if (mNumTaps == tapsRequired) {
                            val distance = mCurrentLocation.distance(mPreviousTapLocation)

                            // moved too much from the previous tap
                            if (distance > scaledDoubleTapSlop) {
                                logMessage(Log.WARN, "distance is $distance > $scaledDoubleTapSlop")
                                handleFailed()
                                return cancelsTouchesInView
                            }
                        }

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

                    // if taps and touches > 1 then we need to be less strict
                    val slop = if (touchesRequired > 1 && tapsRequired > 1) scaledDoubleTapSlop else scaledTouchSlop

                    if (distance > slop) {
                        logMessage(Log.WARN, "distance: $distance, slop: $slop")
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
