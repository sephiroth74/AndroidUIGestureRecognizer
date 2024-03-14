package it.sephiroth.android.library.uigestures

import android.view.KeyEvent
import android.view.View
import java.util.*

/**
 * @author alessandro crugnola
 */
@Suppress("unused")
class UIKeyEventRecognizerDelegate {

    private var mView: View? = null

    /**
     * Enable/Disable any registered gestures
     */
    var isEnabled = true
        set(value) {
            field = value
            mSet.forEach { it.isEnabled = value }
        }

    private val mSet = LinkedHashSet<UIKeyEventRecognizer>()

    /**
     * Asks the delegate if a gesture recognizer should begin interpreting touches.
     *
     * @param recognizer the current recognizer
     * @return true if the recognizer should begin interpreting touches.
     * @see  <a href='https://developer.apple.com/reference/uikit/uigesturerecognizerdelegate/1624213-gesturerecognizershouldbegin'>gesturerecognizershouldbegin</a>
     */
    var shouldBegin: ((recognizer: UIKeyEventRecognizer) -> Boolean) = { true }

    /**
     * Ask the delegate if a gesture recognizer should receive an object representing a touch.
     *
     * @param recognizer the recognizer that should receive the touch
     * @return true if the recognizer should receive the motion event
     * @see <a href='https://developer.apple.com/reference/uikit/uigesturerecognizerdelegate/1624214-gesturerecognizer'>1624214-gesturerecognizer</a>
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var shouldReceiveKeyEvents: ((recognizer: UIKeyEventRecognizer) -> Boolean) = { true }


    /**
     * Asks the delegate if two gesture recognizers should be allowed to recognize gestures simultaneously.
     * true to allow both gestureRecognizer and otherGestureRecognizer to recognize their gestures simultaneously.
     *
     * @param recognizer the first recognizer
     * @param other      the second recognizer
     * @return true if both recognizers shouls be recognized simultaneously
     * @see <a href='https://developer.apple.com/reference/uikit/uigesturerecognizerdelegate/1624208-gesturerecognizer'>1624208-gesturerecognizer</a>
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var shouldRecognizeSimultaneouslyWithGestureRecognizer: (recognizer: UIKeyEventRecognizer, other: UIKeyEventRecognizer) ->
    Boolean = { _, _ -> true }

    /**
     * @param recognizer add a new gesture recognizer to the chain
     * @since 1.0.0
     */
    fun addGestureRecognizer(recognizer: UIKeyEventRecognizer) {
        recognizer.delegate = this
        mSet.add(recognizer)
    }

    /**
     * @param recognizer remove a previously added gesture recognizer
     * @return true if succesfully removed from the list
     * @since 1.0.0
     */
    fun removeGestureRecognizer(recognizer: UIKeyEventRecognizer): Boolean {
        if (mSet.remove(recognizer)) {
            recognizer.delegate = null
            recognizer.clearStateListeners()
            return true
        }
        return false
    }

    /**
     * Returns the number of UIGestureRecognizer currently registered
     */
    fun size() = mSet.size

    /**
     * Remove all the gesture recognizers currently associated with the delegate
     *
     * @since 1.0.0
     */
    fun clear() {
        for (uiGestureRecognizer in mSet) {
            uiGestureRecognizer.delegate = null
            uiGestureRecognizer.clearStateListeners()
        }
        mSet.clear()
    }

    /**
     * Forward the view's touchEvent
     *
     * @param view  the view that generated the event
     * @param event the motion event
     * @return true if handled
     * @since 1.0.0
     */
    fun onKeyEvent(view: View, event: KeyEvent): Boolean {
        if (!isEnabled) return false
        var handled = false

        for (recognizer in mSet) {
            handled = handled or recognizer.onKeyEvent(event)
        }
        return handled
    }


    internal fun shouldRecognizeSimultaneouslyWithGestureRecognizer(recognizer: UIKeyEventRecognizer): Boolean {
        if (mSet.size == 1) {
            return true
        }

        var result = true
        for (other in mSet) {
            if (other != recognizer) {
                if (other.hasBeganFiringEvents()) {
                    result = result and (shouldRecognizeSimultaneouslyWithGestureRecognizer.invoke(recognizer, other))
                }
            }
        }
        return result
    }
}
