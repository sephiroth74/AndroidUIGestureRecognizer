package it.sephiroth.android.library.uigestures

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.PointF
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import java.lang.ref.WeakReference
import java.util.*

/**
 * AndroidGestureRecognizer is an Android implementation
 * of the Apple's UIGestureRecognizer framework. There's not guarantee, however, that
 * this library works 100% in the same way as the Apple version.<br></br>
 * This is the base class for all the UI gesture implementations.
 *
 * @author alessandro crugnola
 * @version 1.0.0
 * @see <a href='https://developer.apple.com/reference/uikit/uigesturerecognizer'>uigesturerecognizer</a>
 */

@Suppress("MemberVisibilityCanBePrivate")
abstract class UIGestureRecognizer(context: Context) : OnGestureRecognizerStateChangeListener {

    private val mStateListeners = Collections.synchronizedList(ArrayList<OnGestureRecognizerStateChangeListener>())

    private var mBeganFiringEvents: Boolean = false

    private val mContextRef: WeakReference<Context> = WeakReference(context)

    private var mNumberOfTouches: Int = 0

    // current ACTION_DOWN location
    protected val mDownLocation = PointF()

    // previous ACTION_DOWN location
    protected val mPreviousDownLocation = PointF()

    // current ACTION_DOWN time
    protected var mDownTime: Long = 0L

    // previous ACTION_DOWN time
    protected var mPreviousDownTime: Long = 0L

    // current event x,y location
    protected val mCurrentLocation = PointF()

    protected val mHandler: GestureHandler = GestureHandler(Looper.getMainLooper())

    protected val isListeningForOtherStateChanges: Boolean get() = null != requireFailureOf && requireFailureOf!!.hasOnStateChangeListenerListener(this)

    internal var delegate: UIGestureRecognizerDelegate? = null

    /**
     * UIGestureRecognizer callback
     */
    var actionListener: ((UIGestureRecognizer) -> Any?)? = null

    /**
     * UIGestureRecognizer's state change callback
     * @since 1.2.5
     */
    var stateListener: ((UIGestureRecognizer, State?, State?) -> Unit)? = null

    /**
     * @return The current recognizer internal state
     * @since 1.0.0
     */
    var state: State? = null
        protected set(value) {
            logMessage(Log.INFO, "setState: ${this.state?.name} --> ${value?.name}")

            val oldValue = field
            val changed = this.state != value || value == State.Changed
            field = value

            if (changed) {
                stateListener?.invoke(this, oldValue, value)
                val iterator = mStateListeners.listIterator()
                while (iterator.hasNext()) {
                    iterator.next().onStateChanged(this)
                }
            }
        }

    /**
     * Toggle the recognizer enabled state.
     *
     * Set to false to prevent any motion event
     * to be intercepted by this recognizer
     * @since 1.0.0
     */
    var isEnabled: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                if (!value) {
                    reset()
                }
            }
        }

    /**
     * Returns the time (in ms) when the user originally pressed down to start a stream of position events
     * @since 1.2.5
     */
    @Suppress("unused")
    val downTime: Long
        get() = mDownTime

    /**
     * @return Returns the X computed as the location of the original down event.
     * @since 1.2.5
     */
    val downLocationX: Float get() = mDownLocation.x

    /**
     * @return Returns the Y computed as the location of the original down event.
     * @since 1.2.5
     */
    val downLocationY: Float get() = mDownLocation.y

    /**
     * @return Returns the X computed as the location in a given view of the gesture represented by the receiver.
     * @since 1.0.0
     */
    open val currentLocationX: Float get() = mCurrentLocation.x

    /**
     * @return Returns the Y computed as the location in a given view of the gesture represented by the receiver.
     * @since 1.0.0
     */
    open val currentLocationY: Float get() = mCurrentLocation.y

    /**
     * A Boolean value affecting whether touches are delivered to a view when a gesture is recognized
     * @see <a href='https://developer.apple.com/reference/uikit/uigesturerecognizer/1624218-cancelstouchesinview'>cancelstouchesinview</a>
     *
     * @since 1.0.0
     */
    var cancelsTouchesInView: Boolean = true

    /**
     * custom object the instance should keep
     * @since 1.0.0
     */
    var tag: Any? = null

    var id: Long = generateId()
        protected set

    /**
     * Creates a dependency relationship between the receiver and another gesture recognizer when the objects are created
     * @see <a href='https://developer.apple.com/reference/uikit/uigesturerecognizer/1624203-require'>1624203-require</a>
     *
     * @since 1.0.0
     */
    var requireFailureOf: UIGestureRecognizer? = null
        set(other) {
            field?.removeOnStateChangeListenerListener(this)
            field = other
        }

    /**
     * Returns the last recorded event
     * @since 1.0.0
     */
    var lastEvent: MotionEvent? = null
        protected set(mLastEvent) {
            mLastEvent?.recycle()
            field = mLastEvent
        }

    val context: Context? get() = mContextRef.get()

    /**
     * @return Returns the number of touches involved in the gesture represented by the receiver.
     * @since 1.0.0
     */
    open val numberOfTouches: Int get() = mNumberOfTouches

    enum class State {
        Possible,
        Began,
        Changed,
        Failed,
        Cancelled,
        Ended
    }

    private fun generateId(): Long {
        return id++
    }

    @SuppressLint("HandlerLeak")
    protected inner class GestureHandler(mainLooper: Looper) : Handler(mainLooper) {
        override fun handleMessage(msg: Message) {
            this@UIGestureRecognizer.handleMessage(msg)
        }
    }

    open fun reset() {
        state = null
        stopListenForOtherStateChanges()
        setBeginFiringEvents(false)
        removeMessages()
    }

    /**
     * @return Has began firing events
     */
    open fun hasBeganFiringEvents(): Boolean {
        return mBeganFiringEvents
    }

    protected fun setBeginFiringEvents(value: Boolean) {
        mBeganFiringEvents = value
    }

    protected abstract fun removeMessages()

    protected fun removeMessages(vararg messages: Int) {
        for (message in messages) {
            mHandler.removeMessages(message)
        }
    }

    @Suppress("unused")
    protected fun hasMessages(vararg messages: Int): Boolean {
        for (message in messages) {
            if (mHandler.hasMessages(message)) {
                return true
            }
        }
        return false
    }

    internal fun clearStateListeners() {
        mStateListeners.clear()
    }

    protected fun fireActionEvent() {
        actionListener?.invoke(this)
    }

    protected fun addOnStateChangeListenerListener(listener: OnGestureRecognizerStateChangeListener) {
        if (!mStateListeners.contains(listener)) {
            mStateListeners.add(listener)
        }
    }

    protected fun removeOnStateChangeListenerListener(listener: OnGestureRecognizerStateChangeListener): Boolean {
        return mStateListeners.remove(listener)
    }

    protected fun hasOnStateChangeListenerListener(listener: OnGestureRecognizerStateChangeListener): Boolean {
        return mStateListeners.contains(listener)
    }

    @SuppressLint("Recycle")
    open fun onTouchEvent(event: MotionEvent): Boolean {
        lastEvent = MotionEvent.obtain(event)

        // action down
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            mPreviousDownLocation.set(mDownLocation)
            mDownLocation.set(event.x, event.y)
            mPreviousDownTime = mDownTime
            mDownTime = event.downTime
        }

        // compute current location
        mNumberOfTouches = computeFocusPoint(event, mCurrentLocation)

        logMessage(Log.VERBOSE, "event.action: ${event.action}, focusPoint: $mCurrentLocation")
        return false
    }

    protected fun computeFocusPoint(event: MotionEvent, out: PointF): Int {
        val actionMasked = event.actionMasked
        val count = event.pointerCount
        val pointerUp = actionMasked == MotionEvent.ACTION_POINTER_UP
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
        out.x = sumX / div
        out.y = sumY / div
        return if (pointerUp) count - 1 else count
    }

    @Suppress("unused")
    @Throws(Throwable::class)
    protected fun finalize() {
    }

    protected abstract fun handleMessage(msg: Message)

    fun inState(vararg states: State): Boolean {
        return states.contains(state)
    }

    protected fun stopListenForOtherStateChanges() {
        requireFailureOf?.removeOnStateChangeListenerListener(this)
    }

    protected fun listenForOtherStateChanges() {
        requireFailureOf?.addOnStateChangeListenerListener(this)
    }

    override fun toString(): String {
        return javaClass.simpleName + "[state: " + state + ", tag:" + tag + "], touches: $numberOfTouches"
    }

    protected fun logMessage(level: Int, fmt: String) {
        if (!sDebug) {
            return
        }
        Log.println(level, LOG_TAG, "[${javaClass.simpleName}] $fmt")
    }

    @Suppress("unused")
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    companion object {

        const val VERSION = BuildConfig.VERSION_NAME

        val LOG_TAG: String = UIGestureRecognizer::class.java.simpleName

        /**
         * @return the instance id
         * @since 1.0.0
         */
        var id = 0
            private set

        protected var sDebug = false

        const val TIMEOUT_DELAY_MILLIS = 5
        val LONG_PRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout().toLong()
        val TAP_TIMEOUT = ViewConfiguration.getTapTimeout().toLong()
        val DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout().toLong()
        const val TOUCH_SLOP = 8
        const val DOUBLE_TAP_SLOP = 100
        const val DOUBLE_TAP_TOUCH_SLOP = TOUCH_SLOP

        var logEnabled: Boolean
            get() = sDebug
            set(value) {
                sDebug = value
            }
    }
}
