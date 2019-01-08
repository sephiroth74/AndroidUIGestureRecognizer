package it.sephiroth.android.library.uigestures.demo.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.sephiroth.android.library.uigestures.UIGestureRecognizer
import it.sephiroth.android.library.uigestures.UIRotateGestureRecognizer
import it.sephiroth.android.library.uigestures.UITapGestureRecognizer
import it.sephiroth.android.library.uigestures.demo.R
import kotlinx.android.synthetic.main.content_uitapgesturerecognizer.*
import timber.log.Timber
import java.lang.ref.WeakReference

class UIRotateGestureRecognizerFragment(private val recognizer: WeakReference<UIGestureRecognizer>) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_uirotationgesturerecognizer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (recognizer.get() as UIRotateGestureRecognizer?)?.rotationThreshold?.let {
            Timber.v("rotationThreshold: $it --> ${Math.toDegrees(it)}")
            numberPicker1.value = Math.toDegrees(it).toInt()
        }

        numberPicker1.setListener {
            Timber.v("rotationThreshold degress = $it")
            (recognizer.get() as UIRotateGestureRecognizer?)?.rotationThreshold = Math.toRadians(it.toDouble())
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(recognizer: UIGestureRecognizer) =
                UIRotateGestureRecognizerFragment(WeakReference(recognizer))
    }
}
