package it.sephiroth.android.library.uigestures

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import it.sephiroth.android.library.simplelogger.LoggerFactory
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
 * @see [
 * https://developer.apple.com/reference/uikit/uigesturerecognizer](https://developer.apple.com/reference/uikit/uigesturerecognizer)
 */

abstract class UIGestureRecognizer(context: Context) : OnGestureRecognizerStateChangeListener {

    private val mStateListeners = Collections.synchronizedList(ArrayList<OnGestureRecognizerStateChangeListener>())

    var actionListener: OnActionListener? = null

    /**
     * @return The current recognizer internal state
     * @since 1.0.0
     */
    var state: State? = null
        protected set(state) {
            logMessage(Log.INFO, "setState: %s", state ?: "null")

            val changed = this.state != state || state == State.Changed
            field = state

            if (changed) {
                val iterator = mStateListeners.listIterator()
                while (iterator.hasNext()) {
                    iterator.next().onStateChanged(this)
                }
            }
        }

    /**
     * Toggle the recognizer enabled state.
     *
     * @param enabled Set to false to prevent any motion event
     * to be intercepted by this recognizer
     * @since 1.0.0
     */
    var isEnabled: Boolean = false

    private var mBeganFiringEvents: Boolean = false

    /**
     * @param value A Boolean value affecting whether touches are delivered to a view when a gesture is recognized
     * @see [
     * https://developer.apple.com/reference/uikit/uigesturerecognizer/1624218-cancelstouchesinview](https://developer.apple.com/reference/uikit/uigesturerecognizer/1624218-cancelstouchesinview)
     *
     * @since 1.0.0
     */
    var cancelsTouchesInView: Boolean = false
    internal var delegate: UIGestureRecognizerDelegate? = null
        set


    /**
     * @return current tag assigned to this instance
     * @since 1.0.0
     */
    /**
     * @param mTag custom object the instance should keep
     * @since 1.0.0
     */
    var tag: Any? = null
        set(mTag) {
            field = mTag

            if (sDebug) {
                logger.tag = mTag.toString()
            }
        }

    var id: Long = 0
        protected set

    /**
     * @param other Creates a dependency relationship between the receiver and another gesture recognizer when the objects
     * are created
     * @see [
     * https://developer.apple.com/reference/uikit/uigesturerecognizer/1624203-require](https://developer.apple.com/reference/uikit/uigesturerecognizer/1624203-require)
     *
     * @since 1.0.0
     */
    var requireFailureOf: UIGestureRecognizer? = null
        set(other) {
            field?.removeOnStateChangeListenerListener(this)
            field = other
        }


    var lastEvent: MotionEvent? = null
        protected set(mLastEvent) {
            mLastEvent?.recycle()
            field = mLastEvent
        }
    private val mContextRef: WeakReference<Context>
    private val logger = LoggerFactory.getLogger(javaClass.simpleName)

    protected val mHandler: GestureHandler

    val context: Context?
        get() = mContextRef.get()

    /**
     * @return Returns the number of touches involved in the gesture represented by the receiver.
     * @since 1.0.0
     */
    abstract val numberOfTouches: Int

    /**
     * @return Returns the X computed as the location in a given view of the gesture represented by the receiver.
     * @since 1.0.0
     */
    abstract val currentLocationX: Float

    /**
     * @return Returns the Y computed as the location in a given view of the gesture represented by the receiver.
     * @since 1.0.0
     */
    abstract val currentLocationY: Float

    protected val isListeningForOtherStateChanges: Boolean
        get() = null != requireFailureOf && requireFailureOf!!.hasOnStateChangeListenerListener(this)

    enum class State {
        Possible,
        Began,
        Changed,
        Failed,
        Cancelled,
        Ended
    }

    interface OnActionListener {
        fun onGestureRecognized(recognizer: UIGestureRecognizer)
    }

    init {
        mHandler = GestureHandler(Looper.getMainLooper())
        cancelsTouchesInView = true
        isEnabled = true
        id = generateId()
        mContextRef = WeakReference(context)
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

    /**
     * @return Has began firing events
     */
    internal open fun hasBeganFiringEvents(): Boolean {
        return mBeganFiringEvents
    }

    internal fun setBeginFiringEvents(value: Boolean) {
        mBeganFiringEvents = value
    }

    protected abstract fun removeMessages()

    internal fun removeMessages(vararg messages: Int) {
        for (message in messages) {
            mHandler.removeMessages(message)
        }
    }

    internal fun hasMessages(vararg messages: Int): Boolean {
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
        if (null != actionListener) {
            actionListener!!.onGestureRecognized(this)
        }
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

    internal open fun onTouchEvent(event: MotionEvent): Boolean {
        lastEvent = MotionEvent.obtain(event)
        return false
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        //        if (null != mLastEvent) {
        //            mLastEvent.recycle();
        //        }
    }

    protected abstract fun handleMessage(msg: Message)

    fun inState(vararg states: State): Boolean {
        if (states.isEmpty()) {
            return false
        }
        for (state in states) {
            if (this.state == state) {
                return true
            }
        }
        return false
    }

    protected fun stopListenForOtherStateChanges() {
        requireFailureOf?.removeOnStateChangeListenerListener(this)
    }

    protected fun listenForOtherStateChanges() {
        requireFailureOf?.addOnStateChangeListenerListener(this)
    }

    override fun toString(): String {
        return javaClass.simpleName + "[state: " + state + ", tag:" + tag + "]"
    }

    protected fun logMessage(level: Int, fmt: String, vararg args: Any) {
        if (!sDebug) {
            return
        }

        when (level) {
            Log.INFO -> logger.info(fmt, *args)
            Log.DEBUG -> logger.debug(fmt, *args)
            Log.ASSERT, Log.ERROR -> logger.error(fmt, *args)
            Log.WARN -> logger.warn(fmt, *args)
            Log.VERBOSE -> logger.verbose(fmt, *args)
            else -> {
            }
        }
    }

    companion object {

        val VERSION = BuildConfig.VERSION_NAME
        /**
         * @return the instance id
         * @since 1.0.0
         */
        var id = 0
            private set
        protected var sDebug = false

        val LONG_PRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout().toLong()
        val TAP_TIMEOUT = ViewConfiguration.getTapTimeout().toLong()
        val DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout().toLong()
        val TOUCH_SLOP = 8
        val DOUBLE_TAP_SLOP = 100
        val DOUBLE_TAP_TOUCH_SLOP = TOUCH_SLOP

        var logEnabled: Boolean
            get() = sDebug
            set(value) {
                sDebug = value
            }

    }
}
