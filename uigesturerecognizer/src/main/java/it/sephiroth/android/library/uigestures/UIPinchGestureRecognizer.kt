package it.sephiroth.android.library.uigestures

import android.content.Context
import android.os.Build
import android.os.Message
import android.util.Log
import android.view.MotionEvent

/**
 * UIPinchGestureRecognizer is a subclass of UIGestureRecognizer that looks for pinching gestures involving two touches.
 * When the user moves the two fingers toward each other, the conventional meaning is zoom-out; when the user moves the two
 * fingers away from each other, the conventional meaning is zoom-in.
 *
 * @author alessandro crugnola
 * @see [
 * https://developer.apple.com/reference/uikit/uipinchgesturerecognizer](https://developer.apple.com/reference/uikit/uipinchgesturerecognizer)
 */

@Suppress("unused")
open class UIPinchGestureRecognizer(context: Context) : UIGestureRecognizer(context),
        UIContinuousRecognizer,
        ScaleGestureDetector.OnScaleGestureListener {
    private val mScaleGestureDetector: ScaleGestureDetector = ScaleGestureDetector(context, this)

    /**
     * @return The total scale factor since the gesture began
     */
    var scale: Float = 0.toFloat()
        private set

    override val numberOfTouches: Int
        get() = mScaleGestureDetector.numberOfTouches

    /**
     * Returns the current scale factor
     *
     * @return
     * @since 1.0.0
     */
    val scaleFactor: Float
        get() = mScaleGestureDetector.scaleFactor

    override val currentLocationX: Float
        get() = mScaleGestureDetector.focusX

    override val currentLocationY: Float
        get() = mScaleGestureDetector.focusY

    /**
     * @see ScaleGestureDetector.currentSpan
     * @since 1.0.0
     */
    val currentSpan: Float
        get() = mScaleGestureDetector.currentSpan

    /**
     * @see ScaleGestureDetector.currentSpanX
     * @since 1.0.0
     */
    val currentSpanX: Float
        get() = mScaleGestureDetector.currentSpanX

    /**
     * @see ScaleGestureDetector.currentSpanY
     * @since 1.0.0
     */
    val currentSpanY: Float
        get() = mScaleGestureDetector.currentSpanY

    /**
     * @see ScaleGestureDetector.previousSpan
     * @since 1.0.0
     */
    val previousSpan: Float
        get() = mScaleGestureDetector.previousSpan

    /**
     * @see ScaleGestureDetector.previousSpanX
     * @since 1.0.0
     */
    val previousSpanX: Float
        get() = mScaleGestureDetector.previousSpanX

    /**
     * @see ScaleGestureDetector.previousSpanY
     * @since 1.0.0
     */
    val previousSpanY: Float
        get() = mScaleGestureDetector.previousSpanY

    /**
     * @see ScaleGestureDetector.timeDelta
     * @since 1.0.0
     */
    val timeDelta: Long
        get() = mScaleGestureDetector.timeDelta

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            MESSAGE_RESET -> handleReset()
            else -> {
            }
        }
    }

    private fun handleReset() {
        state = UIGestureRecognizer.State.Possible
    }

    override fun reset() {
        super.reset()
        handleReset()
    }

    /**
     * Set whether the associated [ScaleGestureDetector.OnScaleGestureListener] should receive onScale callbacks
     * when the user performs a doubleTap followed by a swipe. Note that this is enabled by default
     * if the app targets API 19 and newer.<br></br>
     * Default value is false.
     *
     * true to enable quick scaling, false to disable
     * @since 1.0.0
     */
    var isQuickScaleEnabled: Boolean
        get() {
            return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) mScaleGestureDetector.isQuickScaleEnabled else false
        }
        set(value) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mScaleGestureDetector.isQuickScaleEnabled = value
            }
        }

    /**
     * enable/disable stylus scaling. Note: it is only available for android 23 and above
     * @since 1.0.0
     */
    @Suppress("unused")
    var isStylusScaleEnabled: Boolean
        get() = mScaleGestureDetector.isStylusScaleEnabled
        set(value) {
            mScaleGestureDetector.isStylusScaleEnabled = value
        }

    init {
        isQuickScaleEnabled = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        if (isEnabled) {
            mScaleGestureDetector.onTouchEvent(event)
            return cancelsTouchesInView
        }
        return false
    }

    override fun onStateChanged(recognizer: UIGestureRecognizer) {
        logMessage(Log.VERBOSE, "onStateChanged(${recognizer.state?.name})")

        if (recognizer.state === UIGestureRecognizer.State.Failed && state === UIGestureRecognizer.State.Began) {
            stopListenForOtherStateChanges()
            fireActionEventIfCanRecognizeSimultaneously()

        } else if (recognizer.inState(UIGestureRecognizer.State.Began, UIGestureRecognizer.State.Ended) && inState(UIGestureRecognizer.State.Possible, UIGestureRecognizer.State.Began)) {
            stopListenForOtherStateChanges()
            removeMessages()
            state = UIGestureRecognizer.State.Failed
        }
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        if (isEnabled && inState(UIGestureRecognizer.State.Began, UIGestureRecognizer.State.Changed)) {
            scale += detector.scaleFactor - 1
            if (state === UIGestureRecognizer.State.Began) {
                if (hasBeganFiringEvents()) {
                    state = UIGestureRecognizer.State.Changed
                    fireActionEvent()
                }
            } else if (state === UIGestureRecognizer.State.Changed) {
                state = UIGestureRecognizer.State.Changed
                fireActionEvent()
            }
        }
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        state = UIGestureRecognizer.State.Possible

        if (isEnabled && state === UIGestureRecognizer.State.Possible && delegate?.shouldReceiveTouch?.invoke(this)!!) {
            scale = detector.scaleFactor
            removeMessages(MESSAGE_RESET)

            if (delegate?.shouldBegin?.invoke(this)!!) {
                state = UIGestureRecognizer.State.Began

                if (null == requireFailureOf) {
                    fireActionEventIfCanRecognizeSimultaneously()
                } else {
                    when {
                        requireFailureOf!!.state === UIGestureRecognizer.State.Failed -> fireActionEventIfCanRecognizeSimultaneously()
                        requireFailureOf!!.inState(UIGestureRecognizer.State.Began, UIGestureRecognizer.State.Ended, UIGestureRecognizer.State.Changed) -> state =
                                UIGestureRecognizer.State.Failed
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
            return true
        }
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        if (inState(UIGestureRecognizer.State.Began, UIGestureRecognizer.State.Changed)) {
            val began = hasBeganFiringEvents()
            state = UIGestureRecognizer.State.Ended
            if (began) {
                fireActionEvent()
            }
            mHandler.sendEmptyMessage(MESSAGE_RESET)
        }
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
        private const val MESSAGE_RESET = 1
    }
}
