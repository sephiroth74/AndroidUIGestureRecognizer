package it.sephiroth.android.library.uigestures

import android.graphics.Point
import android.graphics.PointF
import kotlin.math.sqrt

fun PointF.distance(other: PointF): Float {
    return sqrt((other.y - y) * (other.y - y) + (other.x - x) * (other.x - x))
}

fun PointF.distance(other: Point): Float {
    return sqrt((other.y - y) * (other.y - y) + (other.x - x) * (other.x - x))
}

fun Point.distance(other: Point): Float {
    return sqrt(((other.y - y) * (other.y - y) + (other.x - x) * (other.x - x)).toFloat())
}

fun Point.distance(other: PointF): Float {
    return sqrt(((other.y - y) * (other.y - y) + (other.x - x) * (other.x - x)))
}