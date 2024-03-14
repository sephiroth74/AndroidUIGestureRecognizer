package it.sephiroth.android.library.uigestures

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import timber.log.Timber


class TestActivity : AppCompatActivity() {

    val delegate: UIGestureRecognizerDelegate = UIGestureRecognizerDelegate()
    val keyDelegate: UIKeyEventRecognizerDelegate = UIKeyEventRecognizerDelegate()
    private val timeSpan = System.currentTimeMillis()

    private lateinit var mTitleView: TextView
    private lateinit var mTextView: TextView
    private lateinit var mTextView2: TextView
    private lateinit var mMainView: View

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mMainView.setOnTouchListener { view, motionEvent ->
            val time = System.currentTimeMillis() - timeSpan
            val currentText = mTextView2.text
            mTextView2.text = ("$time ms, action: ${actionToString(motionEvent.actionMasked)}")
            mTextView2.append("\n")
            mTextView2.append(currentText)

            Timber.d("[$this] mainView onTouchEvent")
            delegate.onTouchEvent(view, motionEvent)
        }

        mMainView.setOnKeyListener { view, keyCode, event ->
            val time = System.currentTimeMillis() - timeSpan
            val currentText = mTextView2.text
            mTextView2.text = ("$time ms, action: ${UIKeyEventRecognizer.eventActionToString(event.action)}")
            mTextView2.append("\n")
            mTextView2.append(currentText)

            Timber.d("[$this] mainView onKeyEvent")
            keyDelegate.onKeyEvent(view, event)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        delegate.clear()
        mMainView.setOnTouchListener(null)
    }

    override fun onContentChanged() {
        super.onContentChanged()
        mMainView = findViewById<View>(R.id.activity_main)
        mTitleView = findViewById(R.id.title)
        mTextView = findViewById(R.id.text)
        mTextView2 = findViewById(R.id.text2)
    }

    fun setTitle(string: String) {
        runOnUiThread {
            mTitleView.text = string
        }
    }

    private fun actionToString(action: Int): String {
        return when (action) {
            MotionEvent.ACTION_DOWN -> "ACTION_DOWN"
            MotionEvent.ACTION_UP -> "ACTION_UP"
            MotionEvent.ACTION_CANCEL -> "ACTION_CANCEL"
            MotionEvent.ACTION_MOVE -> "ACTION_MOVE"
            MotionEvent.ACTION_POINTER_DOWN -> "ACTION_POINTER_DOWN"
            MotionEvent.ACTION_POINTER_UP -> "ACTION_POINTER_UP"
            else -> "ACTION_OTHER"
        }
    }

    val actionListener: Function1<UIGestureRecognizer, Unit> = fun(recognizer: UIGestureRecognizer) {
        Log.i(javaClass.simpleName, "onGestureRecognized: $recognizer")
        mTextView.text = "${recognizer.tag.toString()} : ${recognizer.state?.name}"
        Log.v(javaClass.simpleName, mTextView.text.toString())
    }

    val keyActionListener: Function1<UIKeyEventRecognizer, Unit> = fun(recognizer: UIKeyEventRecognizer) {
        Log.i(javaClass.simpleName, "onKeyGestureRecognized: $recognizer")
        mTextView.text = "${recognizer.tag.toString()} : ${recognizer.state?.name}"
        Log.v(javaClass.simpleName, mTextView.text.toString())
    }
}
