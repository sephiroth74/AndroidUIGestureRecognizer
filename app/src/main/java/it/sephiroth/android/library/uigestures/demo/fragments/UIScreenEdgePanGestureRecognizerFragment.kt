package it.sephiroth.android.library.uigestures.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import it.sephiroth.android.library.uigestures.UIGestureRecognizer
import it.sephiroth.android.library.uigestures.UIRectEdge
import it.sephiroth.android.library.uigestures.UIScreenEdgePanGestureRecognizer
import it.sephiroth.android.library.uigestures.demo.R
import kotlinx.android.synthetic.main.content_uiscreenedgepangesturerecognizer.*
import java.lang.ref.WeakReference

class UIScreenEdgePanGestureRecognizerFragment(recognizer: WeakReference<UIGestureRecognizer>) :
        IRecognizerFragment<UIScreenEdgePanGestureRecognizer>(recognizer) {

    override fun getRecognizerStatus(): String? {
        getRecognizer()?.let {
            return "scroll: ${it.scrollX}, ${it.scrollY}"
        }
        return null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_uiscreenedgepangesturerecognizer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        numberPicker1.setListener {
            getRecognizer()?.minimumNumberOfTouches = it
            if (it > numberPicker2.value) numberPicker2.value = it
        }

        numberPicker2.setListener {
            getRecognizer()?.maximumNumberOfTouches = it
            if (it < numberPicker1.value) numberPicker1.value = it
        }

        spinnerEdge.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                getRecognizer()?.edge = UIRectEdge.valueOf(parent.selectedItem.toString())
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(recognizer: UIGestureRecognizer) =
                UIScreenEdgePanGestureRecognizerFragment(WeakReference(recognizer))
    }
}
