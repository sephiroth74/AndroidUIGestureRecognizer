package it.sephiroth.android.library.uigestures.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.sephiroth.android.library.uigestures.UIGestureRecognizer
import it.sephiroth.android.library.uigestures.UITapGestureRecognizer
import it.sephiroth.android.library.uigestures.demo.R
import kotlinx.android.synthetic.main.content_uitapgesturerecognizer.*
import timber.log.Timber
import java.lang.ref.WeakReference

class UITapGestureRecognizerFragment(recognizer: WeakReference<UIGestureRecognizer>) :
        IRecognizerFragment<UITapGestureRecognizer>(recognizer) {

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
        return inflater.inflate(R.layout.content_uitapgesturerecognizer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        numberPicker1.setListener {
            getRecognizer()?.tapsRequired = it
        }

        numberPicker2.setListener {
            getRecognizer()?.touchesRequired = it
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(recognizer: UIGestureRecognizer) =
                UITapGestureRecognizerFragment(WeakReference(recognizer))
    }
}
