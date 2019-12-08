package com.anwesh.uiprojects.boxfilltipview

/**
 * Created by anweshmishra on 08/12/19.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.content.Context

val nodes : Int = 5
val scGap : Float = 0.05f
val sizeFactor : Float = 2.9f
val strokeFactor : Int = 90
val delay : Long = 30
val foreColor : Int = Color.parseColor("#01579B")
val backColor : Int = Color.parseColor("#BDBDBD")
val tipSizeFactor : Float = 2.9f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawBoxFillTip(size : Float, scale : Float, paint : Paint) {
    val sf : Float = scale.sinify()
    val tipSize : Float = size / tipSizeFactor
    save()
    translate(0f, -(size - tipSize) * sf)
    paint.style = Paint.Style.FILL
    drawRect(RectF(-tipSize / 2, -tipSize / 2, tipSize / 2, tipSize / 2), paint)
    restore()
    paint.style = Paint.Style.STROKE
    drawRect(RectF(-size / 2, 0f, size / 2, size), paint)
    paint.style = Paint.Style.FILL
    drawRect(RectF(-size / 2, 0f, size / 2, size * scale), paint)
}

fun Canvas.drawBFTNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / (nodes + 1)
    val size : Float = gap / sizeFactor
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    save()
    translate(gap * (i + 1), h / 2)
    drawBoxFillTip(size, scale, paint)
    restore()
}

class BoxFillTipView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }
}
