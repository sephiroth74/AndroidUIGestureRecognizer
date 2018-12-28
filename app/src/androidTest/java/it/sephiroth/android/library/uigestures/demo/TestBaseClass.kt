package it.sephiroth.android.library.uigestures.demo

import android.app.Instrumentation
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.graphics.Point
import android.os.PowerManager
import android.os.PowerManager.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.*
import it.sephiroth.android.library.uigestures.UIGestureRecognizer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import timber.log.Timber

open class TestBaseClass {

    lateinit var instrumentation: Instrumentation
    lateinit var context: Context
    lateinit var device: UiDevice
    lateinit var wakeLock: PowerManager.WakeLock
    lateinit var interaction: Interaction

    var screenWidth: Int = 0
    var screenHeight: Int = 0

    @get:Rule
    var activityTestRule = ActivityTestRule(BaseTest::class.java)

    internal val mainView: UiObject
        get() = device.findObject(UiSelector().resourceId("$PACKAGE_NAME:id/activity_main"))

    internal val textView: UiObject
        get() = device.findObject(UiSelector().resourceId("$PACKAGE_NAME:id/text"))

    fun randomPointOnScreen(): Point {
        val top = screenHeight.toFloat() * 0.2f
        val a = screenHeight.toFloat() * 0.7f
        val w = (Math.random() * screenWidth.toFloat()).toInt()
        val h = (Math.random() * a).toInt() + top
        return Point(w, h.toInt())
    }

    @Before
    open fun setup() {
        instrumentation = InstrumentationRegistry.getInstrumentation()
        device = UiDevice.getInstance(instrumentation)
        context = instrumentation.targetContext
        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT.toLong())

        screenWidth = device.displayWidth
        screenHeight = device.displayHeight

        UIGestureRecognizer.logEnabled = true

        val power = context.getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = power.newWakeLock(FULL_WAKE_LOCK or ACQUIRE_CAUSES_WAKEUP or ON_AFTER_RELEASE, "test")
        wakeLock.acquire()

        interaction = Interaction()

        Timber.plant(Timber.DebugTree())
    }

    @After
    fun tearDown() {
        wakeLock.release()
    }

    companion object {
        internal const val PACKAGE_NAME = "it.sephiroth.android.library.uigestures.demo"
        internal const val LAUNCH_TIMEOUT = 5000
        internal const val TAG = "TestBaseClass"
    }
}
