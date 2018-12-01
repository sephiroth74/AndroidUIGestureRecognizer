package it.sephiroth.android.library.uigestures

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.ViewConfiguration
import timber.log.Timber
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

abstract class UIGestureRecognizer(context: Context) : OnGestureRecognizerStateChangeListener {

    private val mStateListeners = Collections.synchronizedList(ArrayList<OnGestureRecognizerStateChangeListener>())

    var actionListener: ((UIGestureRecognizer) -> Any?)? = null

    /**
     * @return The current recognizer internal state
     * @since 1.0.0
     */
    var state: State? = null
        protected set(state) {
            logMessage(Log.INFO, "setState: ${state?.name}")

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
     * Set to false to prevent any motion event
     * to be intercepted by this recognizer
     * @since 1.0.0
     */
    var isEnabled: Boolean = false

    private var mBeganFiringEvents: Boolean = false

    /**
     * A Boolean value affecting whether touches are delivered to a view when a gesture is recognized
     * @see <a href='https://developer.apple.com/reference/uikit/uigesturerecognizer/1624218-cancelstouchesinview'>cancelstouchesinview</a>
     *
     * @since 1.0.0
     */
    var cancelsTouchesInView: Boolean = false

    var delegate: UIGestureRecognizerDelegate? = null

    /**
     * custom object the instance should keep
     * @since 1.0.0
     */
    var tag: Any? = null

    var id: Long = 0
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


    var lastEvent: MotionEvent? = null
        protected set(mLastEvent) {
            mLastEvent?.recycle()
            field = mLastEvent
        }
    private val mContextRef: WeakReference<Context>
    private val logger = Timber.asTree()

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

    @Suppress("unused")
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

    private fun addOnStateChangeListenerListener(listener: OnGestureRecognizerStateChangeListener) {
        if (!mStateListeners.contains(listener)) {
            mStateListeners.add(listener)
        }
    }

    private fun removeOnStateChangeListenerListener(listener: OnGestureRecognizerStateChangeListener): Boolean {
        return mStateListeners.remove(listener)
    }

    private fun hasOnStateChangeListenerListener(listener: OnGestureRecognizerStateChangeListener): Boolean {
        return mStateListeners.contains(listener)
    }

    open fun onTouchEvent(event: MotionEvent): Boolean {
        lastEvent = MotionEvent.obtain(event)
        return false
    }

    @Suppress("unused")
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
        logger.log(level, fmt, args)
    }

    @Suppress("unused")
    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    companion object {

        const val VERSION = BuildConfig.VERSION_NAME
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
