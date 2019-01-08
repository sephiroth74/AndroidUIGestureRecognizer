package it.sephiroth.android.library.uigestures.demo

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import it.sephiroth.android.library.uigestures.UIGestureRecognizer
import it.sephiroth.android.library.uigestures.UIGestureRecognizerDelegate
import it.sephiroth.android.library.uigestures.UITapGestureRecognizer
import it.sephiroth.android.library.uigestures.demo.fragments.UITapGestureRecognizerFragment
import kotlinx.android.synthetic.main.activity_tap.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

open class TapActivity : AppCompatActivity() {

    private val delegate = UIGestureRecognizerDelegate()
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    private lateinit var recognizer: UIGestureRecognizer

    init {
        UIGestureRecognizer.logEnabled = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tap)
        setSupportActionBar(toolbar)

        setupTouchListener()
        setupSpinner()
    }

    private fun setupTouchListener() {
        testView.setOnTouchListener { v, event ->
            v.onTouchEvent(event)
            delegate.onTouchEvent(v, event)
        }
    }

    private fun setupContent(kClass: KClass<UITapGestureRecognizer>) {
        Timber.i("setupContent: $kClass")

        title = kClass.simpleName

        delegate.clear()

        recognizer = kClass.primaryConstructor?.call(this)!!

        var fragment: Fragment? = null

        when (kClass) {
            UITapGestureRecognizer::class -> fragment = UITapGestureRecognizerFragment.newInstance(recognizer)
        }

        fragment?.let {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, it)
                    .commit()
        }

        setupRecognizer(recognizer)

    }

    private fun setupSpinner() {

        recognizerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selected = parent.selectedItem.toString()
                if (selected == UITapGestureRecognizer::class.simpleName) {
                    setupContent(UITapGestureRecognizer::class)
                }

            }

        }
    }


    private fun setupRecognizer(recognizer: UIGestureRecognizer) {
        recognizer.actionListener = {
            Timber.d("actionListener: ${it.currentLocationX}, ${it.currentLocationY}")
            testView.drawableHotspotChanged(it.currentLocationX, it.currentLocationY)
            testView.isPressed = true
            testView.performClick()
            testView.isPressed = false

            val dateTime = dateFormat.format(recognizer.lastEvent!!.eventTime)

            textState.append("[$dateTime] ${it.javaClass.simpleName}: ${it.state} \n")
            textState.append("[$dateTime] ${it.currentLocationX.toInt()}, ${it.currentLocationY.toInt()}\n")
        }

        recognizer.stateListener =
                { it: UIGestureRecognizer, oldState: UIGestureRecognizer.State?, newState: UIGestureRecognizer.State? ->
                    Timber.v("state changed: $oldState --> $newState")
                }

        delegate.addGestureRecognizer(recognizer)

    }

}
