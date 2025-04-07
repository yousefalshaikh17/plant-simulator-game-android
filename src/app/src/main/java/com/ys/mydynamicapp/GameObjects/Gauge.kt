package com.ys.mydynamicapp.GameObjects

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.ys.mydynamicapp.Vector2


class Gauge(position: Vector2, size: Vector2, var outline: Float, var cornerRadius: Float, var maxValue :Int, var getValue: ()->Int) : StaticObject(position, size, null) {

    var paint = Paint()

    init {
        val tempBitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(tempBitmap)

        paint.color = Color.BLACK
        canvas.drawRoundRect(0f,0f,size.x.toFloat(),size.y.toFloat(),cornerRadius,cornerRadius, paint)

        paint.color = Color.WHITE
        canvas.drawRoundRect((2*outline),(2*outline),size.x -(2*outline),size.y -(2*outline),cornerRadius-(2*outline),cornerRadius-(2*outline), paint)
        bitmap = tempBitmap
    }

    override fun draw(canvas: Canvas, frameCount: Int) {

        super.draw(canvas, frameCount)

        val percentage = getValue().toFloat()/maxValue.toFloat()

        var barLeft = position.x + (4*outline)
        var barRight = barLeft + ((size.x- (8*outline))* percentage)

        paint.color = Color.BLUE
        canvas.drawRoundRect(barLeft,position.y + (4*outline),barRight,position.y + size.y -(4*outline),cornerRadius-(4*outline),cornerRadius-(4*outline), paint)


    }
}