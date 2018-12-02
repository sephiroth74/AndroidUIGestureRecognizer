package it.sephiroth.android.library.uigestures

import android.content.Context
import android.graphics.PointF
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import kotlin.math.atan2

/**
 * UIRotationGestureRecognizer is a subclass of UIGestureRecognizer that looks for rotation gestures involving two
 * touches. When the user moves the fingers opposite each other in a circular motion, the underlying view should rotate in a
 * corresponding direction and speed.
 *
 * @author alessandro crugnola
 * @see [
 * https://developer.apple.com/reference/uikit/uirotationgesturerecognizer](https://developer.apple.com/reference/uikit/uirotationgesturerecognizer)
 */

open class UIRotateGestureRecognizer(context: Context) : UIGestureRecognizer(context), UIContinuousRecognizer {

    /**
     * Change the minimum rotation threshold (in radians)
     *
     * @since 1.0.0
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var rotationThreshold: Double = 0.toDouble()

    /**
     * Returns the rotation in radians
     *
     * @return the current rotation in radians
     * @see .getRotationInDegrees
     * @since 1.0.0
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var rotationInRadians: Double = 0.0
        private set

    private var mInitialRotation: Double = 0.toDouble()

    private var x1: Float = 0.toFloat()
    private var y1: Float = 0.toFloat()
    private var x2: Float = 0.toFloat()
    private var y2: Float = 0.toFloat()

    private var mPreviousAngle: Double = 0.toDouble()

    /**
     * @return The velocity of the rotation gesture in radians per second.
     * @since 1.0.0
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var velocity: Double = 0.toDouble()
        private set

    private var mValid: Boolean = false
    private var mStarted: Boolean = false
    private var mDown: Boolean = false
    private var mPtrID1: Int = 0
    private var mPtrID2: Int = 0
    private var mPreviousEvent: MotionEvent? = null
    private val mCurrentLocation = PointF()

    final override var numberOfTouches: Int = 0
        internal set

    /**
     * Returns the rotation in degrees
     *
     * @return the current rotation in degrees
     * @see .getRotationInRadians
     * @since 1.0.0
     */
    @Suppress("unused")
    val rotationInDegrees: Double
        get() {
            var angle = Math.toDegrees(rotationInRadians) % 360
            if (angle < -180) {
                angle += 360.0
            }
            if (angle > 180) {
                angle -= 360.0
            }
            return angle
        }

    override val currentLocationX: Float
        get() = mCurrentLocation.x

    override val currentLocationY: Float
        get() = mCurrentLocation.y

    override fun handleMessage(msg: Message) {
        when (msg.what) {
            MESSAGE_RESET -> {
                stopListenForOtherStateChanges()
                state = UIGestureRecognizer.State.Possible
                mInitialRotation = 0.0
                setBeginFiringEvents(false)
            }
            else -> {
            }
        }
    }

    init {
        rotationThreshold = ROTATION_SLOP
        mPtrID1 = INVALID_POINTER_ID
        mPtrID2 = INVALID_POINTER_ID
        velocity = 0.toDouble()
        numberOfTouches = 0
    }

    override fun onStateChanged(recognizer: UIGestureRecognizer) {
        logMessage(Log.VERBOSE, "onStateChanged(${recognizer.state?.name})")

        if (recognizer.state === UIGestureRecognizer.State.Failed && state === UIGestureRecognizer.State.Began) {
            stopListenForOtherStateChanges()
            fireActionEventIfCanRecognizeSimultaneously()

        } else if (recognizer.inState(UIGestureRecognizer.State.Began, UIGestureRecognizer.State.Ended) && inState(UIGestureRecognizer.State.Began, UIGestureRecognizer.State.Possible) && mStarted) {
            stopListenForOtherStateChanges()
            removeMessages()
            state = UIGestureRecognizer.State.Failed
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        if (!isEnabled) {
            return false
        }

        val action = event.action
        var count = event.pointerCount

        val pointerUp = action == MotionEvent.ACTION_POINTER_UP
        val skipIndex = if (pointerUp) event.actionIndex else -1

        // Determine focal point
        var sumX = 0f
        var sumY = 0f
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
        numberOfTouches = if (pointerUp) count - 1 else count
        mCurrentLocation.set(focusX, focusY)

        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                if (!delegate?.shouldReceiveTouch?.invoke(this)!!) {
                    return cancelsTouchesInView
                }
                mValid = false
                mStarted = false
                mDown = true
                mInitialRotation = 0.toDouble()
                stopListenForOtherStateChanges()
                state = UIGestureRecognizer.State.Possible
                setBeginFiringEvents(false)
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (mDown && count == 2 && state != UIGestureRecognizer.State.Failed) {
                    mPtrID1 = event.getPointerId(0)
                    mPtrID2 = event.getPointerId(1)

                    x1 = event.getX(event.findPointerIndex(mPtrID1))
                    y1 = event.getY(event.findPointerIndex(mPtrID1))
                    x2 = event.getX(event.findPointerIndex(mPtrID2))
                    y2 = event.getY(event.findPointerIndex(mPtrID2))
                    mValid = true
                } else {
                    mValid = false
                }

                mInitialRotation = 0.toDouble()
                mStarted = false
            }

            MotionEvent.ACTION_POINTER_UP -> {
                count -= 1

                if (mDown && count == 2 && state !== UIGestureRecognizer.State.Failed) {
                    val pointerIndex = action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                    val pointerId = event.getPointerId(pointerIndex)

                    var found = false
                    for (i in 0 until event.pointerCount) {
                        val id = event.getPointerId(i)
                        if (id == pointerId) {
                            continue
                        } else {
                            if (!found) {
                                mPtrID1 = id
                            } else {
                                mPtrID2 = id
                            }
                            found = true
                        }
                    }

                    x1 = event.getX(event.findPointerIndex(mPtrID1))
                    y1 = event.getY(event.findPointerIndex(mPtrID1))
                    x2 = event.getX(event.findPointerIndex(mPtrID2))
                    y2 = event.getY(event.findPointerIndex(mPtrID2))
                    mValid = true
                } else {
                    mValid = false
                }
                mStarted = false
                mInitialRotation = 0.toDouble()
            }

            MotionEvent.ACTION_UP -> {
                mValid = false
                mStarted = false
                mDown = false
                mPreviousAngle = 0.toDouble()
                mInitialRotation = 0.toDouble()
                velocity = 0.toDouble()

                if (null != mPreviousEvent) {
                    mPreviousEvent!!.recycle()
                    mPreviousEvent = null
                }

                if (inState(UIGestureRecognizer.State.Began, UIGestureRecognizer.State.Changed)) {
                    val began = hasBeganFiringEvents()
                    state = UIGestureRecognizer.State.Ended
                    if (began) {
                        fireActionEvent()
                    }
                    mHandler.sendEmptyMessage(MESSAGE_RESET)
                }
            }

            MotionEvent.ACTION_MOVE -> if (mValid && state !== UIGestureRecognizer.State.Failed) {
                val nx1 = event.getX(event.findPointerIndex(mPtrID1))
                val ny1 = event.getY(event.findPointerIndex(mPtrID1))
                val nx2 = event.getX(event.findPointerIndex(mPtrID2))
                val ny2 = event.getY(event.findPointerIndex(mPtrID2))


                if (!mStarted && mDown) {
                    mInitialRotation += angleBetweenLines(x2, y2, x1, y1, nx2, ny2, nx1, ny1)
                    logMessage(Log.INFO, "mInitialRotation, $mInitialRotation, $rotationThreshold")

                    if (Math.abs(mInitialRotation) > rotationThreshold) {
                        mStarted = true

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
                    }
                } else if (mStarted && mDown) {
                    rotationInRadians = angleBetweenLines(x2, y2, x1, y1, nx2, ny2, nx1, ny1)

                    if (state == UIGestureRecognizer.State.Began) {
                        if (hasBeganFiringEvents()) {
                            state = UIGestureRecognizer.State.Changed
                            fireActionEvent()
                        }
                    } else if (state == UIGestureRecognizer.State.Changed) {
                        state = UIGestureRecognizer.State.Changed
                        fireActionEvent()
                    }
                }

                mPreviousEvent?.let {
                    val diff = Math.max(rotationInRadians, mPreviousAngle) - Math.min(rotationInRadians, mPreviousAngle)
                    val time = event.eventTime - it.eventTime
                    velocity = if (time > 0) {
                        1000 / time * diff
                    } else {
                        0.toDouble()
                    }
                    it.recycle()
                }

                x1 = event.getX(event.findPointerIndex(mPtrID1))
                y1 = event.getY(event.findPointerIndex(mPtrID1))
                x2 = event.getX(event.findPointerIndex(mPtrID2))
                y2 = event.getY(event.findPointerIndex(mPtrID2))

                mPreviousEvent = MotionEvent.obtain(event)
                mPreviousAngle = rotationInRadians
            }

            MotionEvent.ACTION_CANCEL -> {
                mPtrID1 = INVALID_POINTER_ID
                mPtrID2 = INVALID_POINTER_ID

                mValid = false
                mStarted = false
                mDown = false
                mPreviousAngle = 0.toDouble()
                velocity = 0.toDouble()

                if (null != mPreviousEvent) {
                    mPreviousEvent!!.recycle()
                    mPreviousEvent = null
                }

                state = UIGestureRecognizer.State.Cancelled
                setBeginFiringEvents(false)
                mHandler.sendEmptyMessage(MESSAGE_RESET)
            }

            else -> {
            }
        }

        return cancelsTouchesInView
    }

    private fun angleBetweenLines(fX: Float, fY: Float, sX: Float, sY: Float, nfX: Float, nfY: Float, nsX: Float,
                                  nsY: Float): Double {
        val angle1 = atan2((fY - sY), (fX - sX)).toDouble()
        val angle2 = atan2((nfY - nsY), (nfX - nsX)).toDouble()
        return if ((angle1 >= 0 && angle2 >= 0) || (angle1 < 0 && angle2 < 0)) {
            angle2 - angle1
        } else if (angle2 < 0) {
            angle1 + angle2
        } else {
            angle2 + angle1
        }
    }

    override fun removeMessages() {
        removeMessages(MESSAGE_RESET)
    }

    companion object {
        private const val MESSAGE_RESET = 1
        private const val INVALID_POINTER_ID = -1
        private const val ROTATION_SLOP = 0.008
    }
}
