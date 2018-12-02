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

    private var mRoot: ViewGroup? = null
    private lateinit var mDelegate: UIGestureRecognizerDelegate
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        UIGestureRecognizer.logEnabled = BuildConfig.DEBUG

        mDelegate = UIGestureRecognizerDelegate()

        val recognizer1 = UITapGestureRecognizer(this)
        recognizer1.tapsRequired = 1
        recognizer1.touchesRequired = 1
        recognizer1.tag = "single-tap"
        recognizer1.tapTimeout = 200
        recognizer1.actionListener = actionListener

        val recognizer2 = UITapGestureRecognizer(this)
        recognizer2.tag = "double-tap"
        recognizer2.tapsRequired = 2
        recognizer2.touchesRequired = 1
        recognizer2.actionListener = actionListener

        val recognizer3 = UILongPressGestureRecognizer(this)
        recognizer3.tag = "long-press"
        recognizer3.tapsRequired = 0
        recognizer3.touchesRequired = 1
        recognizer3.actionListener = actionListener

        val recognizer4 = UILongPressGestureRecognizer(this)
        recognizer4.tag = "long-press-2"
        recognizer4.tapsRequired = 0
        recognizer4.minimumPressDuration = 4000
        recognizer4.allowableMovement = 500f
        recognizer4.actionListener = actionListener

        val recognizer5 = UIPanGestureRecognizer(this)
        recognizer5.tag = "pan"
        recognizer5.actionListener = actionListener
        recognizer5.minimumNumberOfTouches = 2
        recognizer5.maximumNumberOfTouches = 5

        val recognizer6 = UIPinchGestureRecognizer(this)
        recognizer6.tag = "pinch"
        recognizer6.actionListener = actionListener

        val recognizer7 = UIRotateGestureRecognizer(this)
        recognizer7.tag = "rotation"
        recognizer7.actionListener = actionListener

        val recognizer8 = UISwipeGestureRecognizer(this)
        recognizer8.tag = "swipe"
        recognizer8.actionListener = actionListener
        recognizer8.direction = UISwipeGestureRecognizer.UP or UISwipeGestureRecognizer.RIGHT

        val recognizer9 = UIScreenEdgePanGestureRecognizer(this)
        recognizer9.tag = "screenEdges"
        recognizer9.actionListener = actionListener

        //recognizer3.requireFailureOf(recognizer4);

        mDelegate.addGestureRecognizer(recognizer1)
//        mDelegate.addGestureRecognizer(recognizer2)
//        mDelegate.addGestureRecognizer(recognizer3)
//        mDelegate.addGestureRecognizer(recognizer5)
        //        mDelegate.addGestureRecognizer(recognizer3);
        //        mDelegate.addGestureRecognizer(recognizer5);
        //        mDelegate.addGestureRecognizer(recognizer6);
        //        mDelegate.addGestureRecognizer(recognizer8);
        //        mDelegate.addGestureRecognizer(recognizer9);

        // start listening for MotionEvent

        mDelegate.startListeningView(mRoot)

        mDelegate.shouldReceiveTouch = { true }

        mDelegate.shouldBegin = { true }

        mDelegate.shouldRecognizeSimultaneouslyWithGestureRecognizer = {recognizer, other ->
            Timber.v("shouldRecognizeSimultaneouslyWithGestureRecognizer: ${recognizer.tag}, ${other.tag}")
            when(other.tag) {
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

    val runner = Runnable {
        text2.text = ""
    }

    private val actionListener = { recognizer: UIGestureRecognizer ->
        val dateTime = dateFormat.format(recognizer.lastEvent!!.eventTime)
        Timber.d("onGestureRecognized($recognizer)")

        text.setText(recognizer.state?.name)
        text2.append("[$dateTime] tag: ${recognizer.tag}, state: ${recognizer.state?.name} \n")
        text2.append("[coords] ${recognizer.currentLocationX.toInt()}, ${recognizer.currentLocationY.toInt()}\n")

        handler.removeCallbacks(runner)
        handler.postDelayed(runner, 5000)
    }
}
