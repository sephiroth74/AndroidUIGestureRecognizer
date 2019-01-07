package it.sephiroth.android.library.uigestures.demo

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import it.sephiroth.android.library.uigestures.UIGestureRecognizer
import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import it.sephiroth.android.library.uigestures.UIGestureRecognizerDelegate
import it.sephiroth.android.library.uigestures.UITapGestureRecognizer
import kotlinx.android.synthetic.main.activity_tap.*
import timber.log.Timber

class TapActivity : AppCompatActivity() {

    val delegate = UIGestureRecognizerDelegate()


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


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("RestrictedApi")
    protected fun setupRecognizer() {
        recognizer = UITapGestureRecognizer(this)
        recognizer.tapsRequired = 1
        recognizer.touchesRequired = 1
        recognizer.tapTimeout = 200

        recognizer.actionListener = {
            Timber.d("actionListener: ${it.currentLocationX}, ${it.currentLocationY}")
            testView.drawableHotspotChanged(it.currentLocationX, it.currentLocationY)
            testView.isPressed = true
            testView.performClick()
            testView.isPressed = false
            Unit
        }

        recognizer.stateListener =
                { it: UIGestureRecognizer, oldState: State?, newState: State? ->
                    Timber.v("state changed: $oldState --> $newState")
                }

        delegate.addGestureRecognizer(recognizer)

        testView.setOnTouchListener { v, event ->
            v.onTouchEvent(event)
            delegate.onTouchEvent(v, event)
        }
    }

}
