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
fun Float.snify() : Float = Math.sin(this * Math.PI).toFloat()
