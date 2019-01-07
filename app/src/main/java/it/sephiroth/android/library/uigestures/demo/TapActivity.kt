package it.sephiroth.android.library.uigestures.demo

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import it.sephiroth.android.library.uigestures.UIGestureRecognizer
import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import it.sephiroth.android.library.uigestures.UIGestureRecognizerDelegate
import it.sephiroth.android.library.uigestures.UITapGestureRecognizer
import it.sephiroth.android.library.uigestures.setGestureDelegate
import kotlinx.android.synthetic.main.activity_tap.*
import timber.log.Timber

class TapActivity : AppCompatActivity() {

    val MESSAGE_CHANGE_COLOR = 1
    val delegate = UIGestureRecognizerDelegate()

    val handler = Handler(Handler.Callback { msg ->
        msg?.let {
            Timber.v("message: $it")
            when (it.what) {
                MESSAGE_CHANGE_COLOR -> {
                    testView.setBackgroundResource(it.arg1)
                }
            }
        }
        true
    })

    private lateinit var recognizer: UITapGestureRecognizer

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tap)
        setSupportActionBar(toolbar)

        UIGestureRecognizer.logEnabled = true

        setupRecognizer()

        numberPicker1.setListener { value ->
            recognizer.tapsRequired = value
        }

        numberPicker2.setListener { value ->
            recognizer.touchesRequired = value
        }
    }

    private fun changeColor(@ColorRes color: Int, timeout: Long = 16) {
//        val message = handler.obtainMessage(MESSAGE_CHANGE_COLOR)
//        message.arg1 = color
//        handler.sendMessageDelayed(message, timeout)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("RestrictedApi")
    protected fun setupRecognizer() {
        recognizer = UITapGestureRecognizer(this)
        recognizer.tapsRequired = 1
        recognizer.touchesRequired = 1
        recognizer.tapTimeout = 200

        recognizer.actionListener = {
            testView.drawableHotspotChanged(it.currentLocationX, it.currentLocationY)
            testView.isPressed = true
            testView.performClick()
            testView.isPressed = false
            Unit
        }

        recognizer.stateListener =
                { it: UIGestureRecognizer, oldState: State?, newState: State? ->
                    Timber.v("state changed: $oldState --> $newState")

                    when (newState) {
                        State.Failed -> {
                            changeColor(R.color.failedBackgroundColor)
                        }

                        null,
                        State.Possible -> {
                            if (oldState != State.Ended)
                                changeColor(R.color.defaultBackgroundColor)
                            else
                                changeColor(R.color.defaultBackgroundColor, 300)
                        }
                    }

                }

        delegate.addGestureRecognizer(recognizer)
        testView.setGestureDelegate(delegate)
    }

}
