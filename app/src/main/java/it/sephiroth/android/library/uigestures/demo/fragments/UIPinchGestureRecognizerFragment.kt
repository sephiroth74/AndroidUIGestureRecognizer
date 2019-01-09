package it.sephiroth.android.library.uigestures.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.sephiroth.android.library.uigestures.UIGestureRecognizer
import it.sephiroth.android.library.uigestures.UIPinchGestureRecognizer
import it.sephiroth.android.library.uigestures.demo.R
import timber.log.Timber
import java.lang.ref.WeakReference

class UIPinchGestureRecognizerFragment(rec: WeakReference<UIGestureRecognizer>) : IRecognizerFragment<UIPinchGestureRecognizer>(rec) {

    override fun getRecognizerStatus(): String? {
        getRecognizer()?.let {
            val scale = String.format("%.2f", it.scale)
            return "scale: $scale, span: ${it.currentSpan}"
        }
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_uipinchgesturerecognizer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance(recognizer: UIGestureRecognizer) =
                UIPinchGestureRecognizerFragment(WeakReference(recognizer))
    }
}
