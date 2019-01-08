package it.sephiroth.android.library.uigestures.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.sephiroth.android.library.uigestures.UIGestureRecognizer
import it.sephiroth.android.library.uigestures.UILongPressGestureRecognizer
import it.sephiroth.android.library.uigestures.demo.R
import kotlinx.android.synthetic.main.content_uilongpressgesturerecognizer.*
import timber.log.Timber
import java.lang.ref.WeakReference

class UILongPressGestureRecognizerFragment(recognizer: WeakReference<UIGestureRecognizer>) :
        IRecognizerFragment<UILongPressGestureRecognizer>(recognizer) {

    override fun getRecognizerStatus(): String? {
        getRecognizer()?.let {
            return "touches: ${it.numberOfTouches}"
        }
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_uilongpressgesturerecognizer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getRecognizer()?.let {
            numberPicker1.value = it.tapsRequired
            numberPicker2.value = it.touchesRequired
            numberPicker3.value = it.longPressTimeout.toInt()
        }

        numberPicker1.setListener {
            getRecognizer()?.tapsRequired = it
        }

        numberPicker2.setListener {
            getRecognizer()?.touchesRequired = it
        }

        numberPicker3.setListener {
            getRecognizer()?.longPressTimeout = it.toLong()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(recognizer: UIGestureRecognizer) =
                UILongPressGestureRecognizerFragment(WeakReference(recognizer))
    }
}
