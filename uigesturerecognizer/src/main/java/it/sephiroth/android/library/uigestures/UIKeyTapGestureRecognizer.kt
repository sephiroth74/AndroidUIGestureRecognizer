package it.sephiroth.android.library.uigestures

import android.content.Context
import android.os.Message
import android.util.Log
import android.view.KeyEvent
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
open class UIKeyTapGestureRecognizer(context: Context, val keyCode: Int) : UIKeyEventRecognizer(context), UIDiscreteGestureRecognizer {


    /**
     * Change the number of required touches for this recognizer to succeed.<br></br>
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
    var tapTimeout: Long = TAP_TIMEOUT * 2

    /**
     * the duration in milliseconds between the first tap's up event and the second tap's
     * down event for an interaction to be considered a double-tap.
     * @since 1.2.5
     */
    var doubleTapTimeout: Long = DOUBLE_TAP_TIMEOUT

    private var mStarted: Boolean = false
    private var mNumTaps = 0

    init {
        mStarted = false
        val configuration = ViewConfiguration.get(context)

        if (logEnabled) {
            logMessage(Log.INFO, "tapTimeout: $tapTimeout")
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
            }

            MESSAGE_LONG_PRESS -> {
                logMessage(Log.INFO, "handleMessage(MESSAGE_LONG_PRESS)")
                handleFailed()
            }

            else -> {
                logMessage(Log.WARN, "message ${msg.what} not handled")
            }
        }
    }

    override fun onStateChanged(recognizer: UIKeyEventRecognizer) {
        if (logEnabled) {
            logMessage(Log.VERBOSE, "onStateChanged(${recognizer.state?.name})")
            logMessage(Log.VERBOSE, "mStarted: $mStarted")
        }

        if (recognizer.state == State.Failed && state == State.Ended) {
            stopListenForOtherStateChanges()
            postReset()
        } else if (recognizer.inState(State.Began, State.Ended) && mStarted && inState(State.Possible, State.Ended)) {
            stopListenForOtherStateChanges()
            removeMessages()
            state = State.Failed
            mStarted = false
        }
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (logEnabled) {
            logMessage(Log.INFO, "onKeyEvent(action=${event.action}, code=${event.keyCode}, longPress=${event.isLongPress}, repeat=${event.repeatCount})")
            logMessage(Log.VERBOSE, "mStarted=$mStarted, state=$state")
        }

        super.onKeyEvent(event)

        if (!isEnabled) {
            return false
        }

        when (event.action) {
            KeyEvent.ACTION_DOWN -> {
                removeMessages()

                if (!mStarted) {
                    if (delegate?.shouldReceiveKeyEvents?.invoke(this) == false) {
                        return false
                    }

                    if ((event.keyCode != keyCode || event.isLongPress || event.repeatCount > 0)) {
                        return false
                    }
                }

                state = State.Possible
                setBeginFiringEvents(false)

                if (!mStarted) {
                    stopListenForOtherStateChanges()
                    mNumTaps = 0
                    mStarted = true
                }

                mHandler.sendEmptyMessageDelayed(MESSAGE_LONG_PRESS, tapTimeout + TIMEOUT_DELAY_MILLIS)
                return cancelsKeyEventsInView
            }


            KeyEvent.ACTION_UP -> {
                removeMessages(MESSAGE_RESET, MESSAGE_POINTER_UP, MESSAGE_LONG_PRESS)

                if (state == State.Possible && mStarted) {
                    if (event.keyCode != keyCode) {
                        handleFailed()
                    } else {
                        mNumTaps++

                        if(logEnabled) {
                            logMessage(Log.VERBOSE, "numTaps = $mNumTaps, tapsRequired = $tapsRequired")
                        }

                        if (mNumTaps < tapsRequired) {
                            delayedFail()
                        } else {
                            // nailed!
                            if (delegate?.shouldBegin?.invoke(this) == true) {
                                state = State.Ended

                                if (null == requireFailureOf) {
                                    fireActionEventIfCanRecognizeSimultaneously()
                                    postReset()
                                } else {
                                    when {
                                        requireFailureOf?.state === State.Failed -> {
                                            fireActionEventIfCanRecognizeSimultaneously()
                                            postReset()
                                        }

                                        requireFailureOf?.inState(State.Began, State.Ended, State.Changed) == true -> state = State.Failed
                                        else -> listenForOtherStateChanges()

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
        }
        return cancelsKeyEventsInView
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
