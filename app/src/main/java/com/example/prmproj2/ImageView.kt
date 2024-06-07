package com.example.prmproj2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.prmproj2.model.imageSettings
import com.example.prmproj2.model.pathWithSettings

class ImageView(context: Context, attrs: AttributeSet) : View(context, attrs) {


    var color: Int = Color.BLACK
    var size: Float = 30f
    var background: Bitmap? = null
        set(value) {
            field = value
            invalidate()
        }
    private val paths = mutableListOf<pathWithSettings>()
    private val defaultPaint = Paint()
    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    override fun onDraw(canvas: Canvas) {
        drawBackground(canvas)
        drawPaths(canvas)
    }

    private fun drawBackground(canvas: Canvas) {
        background?.let {
            val rect = Rect(0, 0, width, height)
            canvas.drawBitmap(it, null, rect, defaultPaint)
        }
    }

    private fun drawPaths(canvas: Canvas) {
        paths.forEach {
            paint.apply {
                strokeWidth = it.settings.size
                color = it.settings.color
            }
            canvas.drawPath(it.path, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return true

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pathWithSettings(settings = imageSettings(size = size, color = color)).let {
                    it.path.moveTo(event.x, event.y)
                    it.path.lineTo(event.x, event.y)
                    paths.add(it)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                paths.last().path.lineTo(event.x, event.y)
            }
        }

        invalidate()

        return true
    }

    fun generateBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        drawBackground(canvas)
        drawPaths(canvas)

        return bitmap
    }


}