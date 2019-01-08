package it.sephiroth.android.library.uigestures.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.sephiroth.android.library.uigestures.UIGestureRecognizer
import it.sephiroth.android.library.uigestures.UIPanGestureRecognizer
import it.sephiroth.android.library.uigestures.demo.R
import kotlinx.android.synthetic.main.content_uipangesturerecognizer.*
import timber.log.Timber
import java.lang.ref.WeakReference

class UIPanGestureRecognizerFragment(recognizer: WeakReference<UIGestureRecognizer>) :
        IRecognizerFragment<UIPanGestureRecognizer>(recognizer) {

    override fun getRecognizerStatus(): String? {
        getRecognizer()?.let {
            return "scroll: ${it.scrollX}, ${it.scrollY}, touches: ${it.numberOfTouches}"
        }
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_uipangesturerecognizer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getRecognizer()?.let {
            numberPicker1.value = it.minimumNumberOfTouches
            numberPicker2.value = it.maximumNumberOfTouches
        }

        numberPicker1.setListener {
            getRecognizer()?.minimumNumberOfTouches = it
        }

        numberPicker2.setListener {
            getRecognizer()?.maximumNumberOfTouches = it
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(recognizer: UIGestureRecognizer) =
                UIPanGestureRecognizerFragment(WeakReference(recognizer))
    }
}
