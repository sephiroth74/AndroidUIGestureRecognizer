package it.sephiroth.android.library.uigestures.demo

import android.os.Bundle
import android.os.Handler
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import it.sephiroth.android.library.uigestures.*
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var mRoot: ViewGroup
    private lateinit var mDelegate: UIGestureRecognizerDelegate
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        UIGestureRecognizer.logEnabled = BuildConfig.DEBUG

        mDelegate = UIGestureRecognizerDelegate()

        val recognizer1 = UITapGestureRecognizer(this)
        recognizer1.tapsRequired = 2
        recognizer1.touchesRequired = 2
        recognizer1.actionListener = actionListener
        recognizer1.stateListener = stateListener

        mDelegate.addGestureRecognizer(recognizer1)

        mRoot.setGestureDelegate(mDelegate)

        mDelegate.shouldReceiveTouch = { true }
        mDelegate.shouldBegin = { true }

        mDelegate.shouldRecognizeSimultaneouslyWithGestureRecognizer = { recognizer, other ->
            Timber.v("shouldRecognizeSimultaneouslyWithGestureRecognizer: ${recognizer.tag}, ${other.tag}")
            when (other.tag) {
                "single-tap" -> false
                "double-tap" -> false
                else -> {
                    true
                }
            }
        }
    }

    override fun onContentChanged() {
        super.onContentChanged()

        mRoot = findViewById(R.id.activity_main)
    }

    private val runner = Runnable {
        text2.text = ""
        text.text = ""
        text3.text = ""
    }

    private val stateListener =
            { recognizer: UIGestureRecognizer, oldState: UIGestureRecognizer.State?, newState: UIGestureRecognizer.State? ->
                text3.text = "${recognizer.javaClass.simpleName}: $oldState --> $newState"
            }

    private val actionListener = { recognizer: UIGestureRecognizer ->
        val dateTime = dateFormat.format(recognizer.lastEvent!!.eventTime)
        Timber.d("**********************************************")
        Timber.d("onGestureRecognized($recognizer)")
        Timber.d("**********************************************")

        text.text = "${recognizer.javaClass.simpleName}: ${recognizer.state}"
        text2.append("[$dateTime] tag: ${recognizer.tag}, state: ${recognizer.state?.name} \n")
        text2.append("[coords] ${recognizer.currentLocationX.toInt()}, ${recognizer.currentLocationY.toInt()}\n")

        if (recognizer is UIPanGestureRecognizer) {
            text2.append("[origin] ${recognizer.startLocationX}, ${recognizer.startLocationY}\n")
        } else if (recognizer is UILongPressGestureRecognizer) {
            text2.append("[origin] ${recognizer.startLocationX}, ${recognizer.startLocationY}\n")
        }

        handler.removeCallbacks(runner)
        handler.postDelayed(runner, 5000)
    }
}
