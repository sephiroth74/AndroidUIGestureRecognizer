package com.example.sample

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.ComponentActivity
import it.sephiroth.android.library.uigestures.UIKeyEventRecognizerDelegate
import it.sephiroth.android.library.uigestures.UIKeyTapGestureRecognizer
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContentView(R.layout.main_activity)
    }

    override fun onContentChanged() {
        super.onContentChanged()

        val recognizer = UIKeyTapGestureRecognizer(this, KeyEvent.KEYCODE_DPAD_CENTER).also {
            it.tag = "double-click"
            it.tapsRequired = 2
            it.actionListener = { event ->
                Timber.d("**** event recognized! $event ***")
            }
        }

        val delegate = UIKeyEventRecognizerDelegate()
        delegate.addGestureRecognizer(recognizer)


        val button = this.findViewById<View>(R.id.button_01)
        button.setOnKeyListener { view, _, event ->
            Timber.v("onKeyEvent($event)")
            delegate.onKeyEvent(view, event)
        }
    }
}
