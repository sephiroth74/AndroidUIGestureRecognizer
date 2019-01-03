package it.sephiroth.android.library.uigestures

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class TestActivity : AppCompatActivity() {

    val delegate: UIGestureRecognizerDelegate = UIGestureRecognizerDelegate()
    val timeSpan = System.currentTimeMillis()

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
            mTextView2.text = ("${timeSpan} ms, action: ${actionToString(motionEvent.actionMasked)}")
            mTextView2.append("\n")
            mTextView2.append(currentText)

            delegate.onTouchEvent(view, motionEvent)
        }
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
        when (action) {
            MotionEvent.ACTION_DOWN -> return "ACTION_DOWN"
            MotionEvent.ACTION_UP -> return "ACTION_UP"
            MotionEvent.ACTION_CANCEL -> return "ACTION_CANCEL"
            MotionEvent.ACTION_MOVE -> return "ACTION_MOVE"
            MotionEvent.ACTION_POINTER_DOWN -> return "ACTION_POINTER_DOWN"
            MotionEvent.ACTION_POINTER_UP -> return "ACTION_POINTER_UP"
            else -> return "ACTION_OTHER"
        }
    }

    val actionListener: Function1<UIGestureRecognizer, Unit> = fun(recognizer: UIGestureRecognizer): Unit {
        Log.i(javaClass.simpleName, "onGestureRecognized: $recognizer")
        mTextView.text = "${recognizer.tag.toString()} : ${recognizer.state?.name}"
        Log.v(javaClass.simpleName, mTextView.text.toString())
    }
}
