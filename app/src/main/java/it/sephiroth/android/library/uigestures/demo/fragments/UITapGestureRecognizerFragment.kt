package it.sephiroth.android.library.uigestures.demo.fragments

import android.content.Context
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

class UITapGestureRecognizerFragment(private val recognizer: WeakReference<UIGestureRecognizer>) : Fragment() {

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
            (recognizer.get() as UITapGestureRecognizer?)?.tapsRequired = it
        }

        numberPicker2.setListener {
            (recognizer.get() as UITapGestureRecognizer?)?.touchesRequired = it
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

    companion object {
        @JvmStatic
        fun newInstance(recognizer: UIGestureRecognizer) =
                UITapGestureRecognizerFragment(WeakReference(recognizer))
    }
}
