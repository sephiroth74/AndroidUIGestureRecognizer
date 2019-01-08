package it.sephiroth.android.library.uigestures.demo

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import it.sephiroth.android.library.uigestures.UIGestureRecognizer
import it.sephiroth.android.library.uigestures.UIGestureRecognizerDelegate
import it.sephiroth.android.library.uigestures.UIRotateGestureRecognizer
import it.sephiroth.android.library.uigestures.UITapGestureRecognizer
import it.sephiroth.android.library.uigestures.demo.fragments.UIRotateGestureRecognizerFragment
import it.sephiroth.android.library.uigestures.demo.fragments.UITapGestureRecognizerFragment
import kotlinx.android.synthetic.main.activity_tap.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.internal.impl.load.kotlin.KotlinClassFinder

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

    private fun setupContent(simpleClassName: String) {
        Timber.i("setupContent: $simpleClassName")

        val packageName = UIGestureRecognizer::class.java.`package`.name
        Timber.v("package: $packageName")

        val kClass = Class.forName("${packageName}.$simpleClassName").kotlin
        Timber.v("kClass: ${kClass.simpleName}")

        val newRecognizer = kClass.primaryConstructor?.call(this) as UIGestureRecognizer?

        newRecognizer?.let { rec ->
            var fragment: Fragment? = null

            when (kClass) {
                UITapGestureRecognizer::class -> fragment = UITapGestureRecognizerFragment.newInstance(rec)
                UIRotateGestureRecognizer::class -> fragment = UIRotateGestureRecognizerFragment.newInstance(rec)
            }

            fragment?.let { frag ->
                title = kClass.simpleName
                delegate.clear()
                recognizer = rec

                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, frag)
                        .commit()
                setupRecognizer(recognizer)
            } ?: kotlin.run {
            }
        } ?: kotlin.run {
            Toast.makeText(this, "Unable to find ${kClass.simpleName}", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setupSpinner() {

        recognizerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {}

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selected = parent.selectedItem.toString()
                setupContent(selected)
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
