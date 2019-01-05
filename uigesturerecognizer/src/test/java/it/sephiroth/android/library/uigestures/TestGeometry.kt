package it.sephiroth.android.library.uigestures

import android.graphics.Point
import android.graphics.PointF
import android.os.SystemClock
import android.view.MotionEvent
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.text.DecimalFormat
import java.text.NumberFormat

@RunWith(RobolectricTestRunner::class)
class TestGeometry {

    lateinit var formatter: NumberFormat

    @Before
    fun initialize() {
        formatter = DecimalFormat.getInstance()
        formatter.maximumFractionDigits = 4
        formatter.minimumFractionDigits = 4
    }

    @Test
    fun testDistancePoint2Point() {
        val pt1 = Point(0, 0)
        val pt2 = Point(100, 100)

        val distance1 = pt1.distance(pt2)
        val distance2 = pt2.distance(pt1)

        assertEquals(distance1, distance2)
        assertEquals("141.4214", formatter.format(distance1))
    }

    @Test
    fun testDistancePointF2PointF() {
        val pt1 = PointF(0f, 0f)
        val pt2 = PointF(100f, 100f)

        val distance1 = pt1.distance(pt2)
        val distance2 = pt2.distance(pt1)

        assertEquals(distance1, distance2)
        assertEquals("141.4214", formatter.format(distance1))
    }

    @Test
    fun testDistanceZero() {
        assertEquals(0f, PointF(0f, 0f).distance(PointF(0f, 0f)))
        assertEquals(0f, Point(0, 0).distance(Point(0, 0)))
    }

    @Test
    fun testDifferentTypes() {
        val pt1 = Point(0,0)
        val pt2 = Point(100,100)

        val ptf1 = PointF(0f,0f)
        val ptf2 = PointF(100f,100f)

        assertEquals(pt1.distance(pt2), pt1.distance(ptf2))
        assertEquals(ptf1.distance(pt2), ptf1.distance(ptf2))
        assertEquals(pt1.distance(pt2), ptf1.distance(ptf2))
    }
}