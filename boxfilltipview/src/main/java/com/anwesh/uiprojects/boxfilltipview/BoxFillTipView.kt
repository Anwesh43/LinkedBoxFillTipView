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

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BFTNode(var i : Int, val state : State = State()) {

        private var next : BFTNode? = null
        private var prev : BFTNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = BFTNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBFTNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : BFTNode {
            var curr : BFTNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class BoxFillTip(var i : Int) {

        private val root : BFTNode = BFTNode(0)
        private var curr : BFTNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : BoxFillTipView) {

        private val animator : Animator = Animator(view)
        private val bft : BoxFillTip = BoxFillTip(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            bft.draw(canvas, paint)
            animator.animate {
                bft.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            bft.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : BoxFillTipView {
            val view : BoxFillTipView = BoxFillTipView(activity)
            activity.setContentView(view)
            return view
        }
    }
}
