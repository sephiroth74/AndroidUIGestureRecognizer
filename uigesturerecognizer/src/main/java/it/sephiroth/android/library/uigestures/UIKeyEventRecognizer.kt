package it.sephiroth.android.library.uigestures

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.KeyEvent
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
abstract class UIKeyEventRecognizer(context: Context) : OnKeyEventRecognizerStateChangeListener {

    private val mStateListeners = Collections.synchronizedList(ArrayList<OnKeyEventRecognizerStateChangeListener>())

    private var mBeganFiringEvents: Boolean = false

    private val mContextRef: WeakReference<Context> = WeakReference(context)

    // current ACTION_DOWN time
    protected var mDownTime: Long = 0L

    // previous ACTION_DOWN time
    protected var mPreviousDownTime: Long = 0L

    protected val mHandler: GestureHandler = GestureHandler(Looper.getMainLooper())

    protected val isListeningForOtherStateChanges: Boolean get() = null != requireFailureOf && requireFailureOf!!.hasOnStateChangeListenerListener(this)

    internal var delegate: UIKeyEventRecognizerDelegate? = null

    /**
     * UIGestureRecognizer callback
     */
    var actionListener: ((UIKeyEventRecognizer) -> Unit)? = null

    /**
     * UIGestureRecognizer's state change callback
     * @since 1.2.5
     */
    var stateListener: ((UIKeyEventRecognizer, State?, State?) -> Unit)? = null

    /**
     * @return The current recognizer internal state
     * @since 1.0.0
     */
    var state: State? = null
        protected set(value) {

            val oldValue = field
            val changed = this.state != value || value == State.Changed
            field = value

            if (changed) {
                logMessage(Log.INFO, "setState: ${oldValue?.name} --> ${value?.name}")
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
     * A Boolean value affecting whether touches are delivered to a view when a gesture is recognized
     * @see <a href='https://developer.apple.com/reference/uikit/uigesturerecognizer/1624218-cancelstouchesinview'>cancelstouchesinview</a>
     *
     * @since 1.0.0
     */
    var cancelsKeyEventsInView: Boolean = true

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
    var requireFailureOf: UIKeyEventRecognizer? = null
        set(other) {
            field?.removeOnStateChangeListenerListener(this)
            field = other
        }

    /**
     * Returns the last recorded event
     * @since 1.0.0
     */
    var lastEvent: KeyEvent? = null
        protected set(mLastEvent) {
            field = mLastEvent
        }

    val context: Context? get() = mContextRef.get()

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
            this@UIKeyEventRecognizer.handleMessage(msg)
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

    protected fun addOnStateChangeListenerListener(listener: OnKeyEventRecognizerStateChangeListener) {
        if (!mStateListeners.contains(listener)) {
            mStateListeners.add(listener)
        }
    }

    protected fun removeOnStateChangeListenerListener(listener: OnKeyEventRecognizerStateChangeListener): Boolean {
        return mStateListeners.remove(listener)
    }

    protected fun hasOnStateChangeListenerListener(listener: OnKeyEventRecognizerStateChangeListener): Boolean {
        return mStateListeners.contains(listener)
    }

    open fun onKeyEvent(event: KeyEvent): Boolean {
        lastEvent = KeyEvent(event)
        if (event.action == KeyEvent.ACTION_DOWN) {
            mPreviousDownTime = mDownTime
            mDownTime = event.downTime
        }
        return false
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
        return javaClass.simpleName + "[state: " + state + ", tag:" + tag + "]"
    }

    protected fun logMessage(level: Int, fmt: String) {
        if (!sDebug) {
            return
        }
        Log.println(level, LOG_TAG, "[${javaClass.simpleName}:$tag] $fmt")
    }

    @Suppress("unused")
    companion object {
        val LOG_TAG: String = UIKeyEventRecognizer::class.java.simpleName

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

        fun eventActionToString(action: Int): String {
            return when (action) {
                KeyEvent.ACTION_DOWN -> "ACTION_DOWN"
                KeyEvent.ACTION_UP -> "ACTION_UP"
                else -> "ACTION_OTHER"
            }
        }
    }
}
