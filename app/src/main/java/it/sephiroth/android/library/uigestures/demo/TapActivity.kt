package it.sephiroth.android.library.uigestures.demo

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import it.sephiroth.android.library.uigestures.*
import it.sephiroth.android.library.uigestures.demo.fragments.*
import kotlinx.android.synthetic.main.activity_tap.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.full.primaryConstructor

open class TapActivity : AppCompatActivity() {

    private var currentRecognizerClassName: String = ""
    private val handler = Handler()
    private val delegate = UIGestureRecognizerDelegate()
    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)
    private lateinit var recognizer: UIGestureRecognizer

    private val clearTextRunnable: Runnable = Runnable {
        textState.text = "Test me!"
    }

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

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
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
                UIPinchGestureRecognizer::class -> fragment = UIPinchGestureRecognizerFragment.newInstance(rec)
                UIScreenEdgePanGestureRecognizer::class -> fragment = UIScreenEdgePanGestureRecognizerFragment.newInstance(rec)
            }

            fragment?.let { frag ->
                title = kClass.simpleName
                delegate.clear()
                recognizer = rec

                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, frag, simpleClassName)
                        .commit()

                setupRecognizer(recognizer)
                currentRecognizerClassName = simpleClassName

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
        recognizer.actionListener = { rec ->
            Timber.d("actionListener: ${rec.currentLocationX}, ${rec.currentLocationY}")
            testView.drawableHotspotChanged(rec.currentLocationX, rec.currentLocationY)
            testView.isPressed = true
            testView.performClick()
            testView.isPressed = false

            val dateTime = dateFormat.format(recognizer.lastEvent!!.eventTime)

            textState.append("[$dateTime] ${rec.javaClass.simpleName}: ${rec.state} \n")
            textState.append("[$dateTime] location: ${rec.currentLocationX.toInt()}, ${rec.currentLocationY.toInt()}\n")

            supportFragmentManager.findFragmentByTag(currentRecognizerClassName)?.let { frag ->
                val status = (frag as IRecognizerFragment<*>).getRecognizerStatus()
                status?.let {
                    textState.append("[$dateTime] $it\n")
                }
            }

            textState.append("\n")

            handler.removeCallbacks(clearTextRunnable)
            handler.postDelayed(clearTextRunnable, 4000)


        }

        recognizer.stateListener =
                { it: UIGestureRecognizer, oldState: UIGestureRecognizer.State?, newState: UIGestureRecognizer.State? ->
                    Timber.v("state changed: $oldState --> $newState")
                }

        delegate.addGestureRecognizer(recognizer)

    }

}
