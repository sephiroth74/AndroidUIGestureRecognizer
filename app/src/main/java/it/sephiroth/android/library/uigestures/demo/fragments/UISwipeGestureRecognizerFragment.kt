package it.sephiroth.android.library.uigestures.demo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import it.sephiroth.android.library.uigestures.UIGestureRecognizer
import it.sephiroth.android.library.uigestures.UIRectEdge
import it.sephiroth.android.library.uigestures.UISwipeGestureRecognizer
import it.sephiroth.android.library.uigestures.demo.R
import kotlinx.android.synthetic.main.content_uiswipegesturerecognizer.*
import java.lang.ref.WeakReference

class UISwipeGestureRecognizerFragment(recognizer: WeakReference<UIGestureRecognizer>) :
        IRecognizerFragment<UISwipeGestureRecognizer>(recognizer) {

    override fun getRecognizerStatus(): String? {
        getRecognizer()?.let {
            return "scroll: ${it.scrollX}, ${it.scrollY}"
        }
        return null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.content_uiswipegesturerecognizer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getRecognizer()?.let {
            numberPicker1.value = it.numberOfTouchesRequired
            numberPicker2.value = it.minimumSwipeDistance
            numberPicker3.value = it.scaledMinimumFlingVelocity
        }

        numberPicker1.setListener {
            getRecognizer()?.numberOfTouchesRequired = it
        }

        numberPicker2.setListener {
            getRecognizer()?.minimumSwipeDistance = it
        }

        numberPicker3.setListener {
            getRecognizer()?.scaledMinimumFlingVelocity = it
        }

        spinnerEdge.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                getRecognizer()?.direction = if (parent.selectedItem.toString() == "LEFT") UISwipeGestureRecognizer.LEFT
                else if (parent.selectedItem.toString() == "RIGHT") UISwipeGestureRecognizer.RIGHT
                else if (parent.selectedItem.toString() == "UP") UISwipeGestureRecognizer.UP
                else if (parent.selectedItem.toString() == "DOWN") UISwipeGestureRecognizer.DOWN
                else UISwipeGestureRecognizer.LEFT
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(recognizer: UIGestureRecognizer) =
                UISwipeGestureRecognizerFragment(WeakReference(recognizer))
    }
}
