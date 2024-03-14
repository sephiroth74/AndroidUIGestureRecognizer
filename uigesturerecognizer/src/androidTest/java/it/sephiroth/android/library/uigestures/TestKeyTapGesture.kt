package it.sephiroth.android.library.uigestures

import android.os.SystemClock
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.test.core.view.PointerCoordsBuilder
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.filters.MediumTest
import it.sephiroth.android.library.uigestures.UIGestureRecognizer.State
import org.junit.Assert.*
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@MediumTest
class TestKeyTapGesture : TestBaseClass() {

    @Test
    fun test01Tap() {
        setTitle("Tap")
        val latch = CountDownLatch(1)

        delegate.clear()
        assertEquals(0, delegate.size())

        val recognizer = UIKeyTapGestureRecognizer(context, KeyEvent.KEYCODE_DPAD_DOWN)
        recognizer.tag = "key-tap"
        recognizer.tapsRequired = 1
        recognizer.tapTimeout = TEST_TAP_TIMEOUT

        recognizer.actionListener = { it ->
            activity.keyActionListener.invoke(it)

            assertEquals(State.Ended, it.state)
            latch.countDown()
        }

        keyDelegate.addGestureRecognizer(recognizer)

        assertEquals(1, keyDelegate.size())
        assertTrue(keyDelegate.isEnabled)

        onView(ViewMatchers.withId(R.id.activity_main)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_DPAD_DOWN))
        latch.await(2, TimeUnit.SECONDS)
        assertEquals(0, latch.count)
    }


}
