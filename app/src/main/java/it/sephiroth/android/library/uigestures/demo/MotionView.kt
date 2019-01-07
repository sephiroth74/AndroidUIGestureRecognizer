package it.sephiroth.android.library.uigestures.demo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import timber.log.Timber

class MotionView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    var pointersMap = hashMapOf<Int, PointerDrawable>()

    init {
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val action = event.actionMasked
        val pointerIndex = event.actionIndex
        val pointerId = event.getPointerId(pointerIndex)

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                if (!pointersMap.containsKey(pointerId)) {
                    val pointerDrawable = PointerDrawable(pointerId)
                    pointerDrawable.drawCallback = { postInvalidateOnAnimation() }
                    pointersMap[pointerId] = pointerDrawable
                }

                val pointerDrawable = pointersMap[pointerId]
                pointerDrawable?.let {
                    it.reset()
                    it.moveTo(event.x, event.y)
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                if (!pointersMap.containsKey(pointerId)) {
                    val pointerDrawable = PointerDrawable(pointerId)
                    pointerDrawable.drawCallback = { postInvalidateOnAnimation() }
                    pointersMap[pointerId] = pointerDrawable
                }

                val pointerDrawable = pointersMap[pointerId]
                pointerDrawable?.let {
                    it.reset()
                    it.moveTo(event.getX(pointerIndex), event.getY(pointerIndex))
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val pointerDrawable = pointersMap[pointerId]
                pointerDrawable?.fadeout()
            }

            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    val id = event.getPointerId(i)
                    val pointerDrawable = pointersMap[id]
                    pointerDrawable?.lineTo(event.getX(i), event.getY(i))
                }
            }

            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                for (item in pointersMap.values)
                    item.fadeout()
            }
        }

        return super.onTouchEvent(event)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        pointersMap.filter { it.value.enabled }.map { it.value }.forEach { it.draw(canvas) }
    }

}

class PointerDrawable(val pointerIndex: Int) {
    var enabled = false
        private set

    var drawCallback: ((p: PointerDrawable) -> Unit)? = null

    private var x: Float = 0f
    private var y: Float = 0f
    private val path = Path()
    private var isFadingOut: Boolean = false
    private var alpha = 1f

    override fun toString(): String {
        return "PointerDrawable($pointerIndex)"
    }

    fun reset() {
        path.reset()
        isFadingOut = false
        alpha = 1f
        enabled = false
    }

    fun moveTo(x: Float, y: Float) {
        this.x = x
        this.y = y
        path.moveTo(x, y)
        enabled = true

        drawCallback?.invoke(this)
    }

    fun lineTo(x: Float, y: Float) {
        this.x = x
        this.y = y
        path.lineTo(x, y)
        drawCallback?.invoke(this)
    }

    fun fadeout() {
        isFadingOut = true
        fadeOutInternal()
    }


    private fun fadeOutInternal() {
        if (!enabled || !isFadingOut) return

        handler.postDelayed({
            if (alpha > 0) {
                alpha -= ALPHA_DECREASE_STEP
            }

            drawCallback?.invoke(this)

            if (alpha > 0) {
                fadeOutInternal()
            } else {
                reset()
            }
        }, FADE_OUT_DELAY_ANIM)
    }

    fun draw(canvas: Canvas) {
        pathPaint.alpha = (alpha * PATH_ALPHA_MAX).toInt()
        canvas.drawPath(path, pathPaint)

        pointerPaint.style = Paint.Style.FILL
        pointerPaint.color = Color.WHITE
        pointerPaint.alpha = (alpha * POINTER_STROKE_MAX).toInt()
        canvas.drawCircle(x, y, radius, pointerPaint)

        pointerPaint.style = Paint.Style.STROKE
        pointerPaint.color = Color.BLACK
        pointerPaint.alpha = (alpha * POINTER_FILL_MAX).toInt()
        canvas.drawCircle(x, y, radius, pointerPaint)
    }

    companion object {
        private const val PATH_ALPHA_MAX = 255
        private const val POINTER_STROKE_MAX = 201
        private const val POINTER_FILL_MAX = 100

        private const val FADE_OUT_DELAY_ANIM: Long = 16
        private const val ALPHA_DECREASE_STEP = .1f
        private val handler = android.os.Handler()
        private val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var radius = 40f

        init {
            pointerPaint.strokeWidth = 6f
            pathPaint.style = Paint.Style.STROKE
            pathPaint.strokeWidth = 1f
            pathPaint.color = Color.RED
        }
    }
}