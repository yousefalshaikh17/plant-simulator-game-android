package com.ys.mydynamicapp.GameObjects

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import com.ys.mydynamicapp.Vector2

abstract class DynamicObject(position: Vector2, size: Vector2, var velocity: Vector2, image: Drawable?) : GameObject(position, size, image) {

//    var redraw = true

    override fun draw(canvas: Canvas, frameCount: Int) {

        if (!checkIfInScreen(canvas))
        {
            isUsable = false
            return
        }
        super.draw(canvas, frameCount)
    }

    open fun move(canvas: Canvas, frameCount: Int)
    {
        updateRect()
//        position.add(velocity)
//        draw(canvas)
    }

    override fun tick(canvas: Canvas, frameCount: Int) {
        super.tick(canvas, frameCount)
        move(canvas, frameCount)
    }

    fun checkIfInScreen(canvas :Canvas): Boolean {
//        return canvasWidth - wateringCanBorderPadding - (size.x + position.x)
//        var objectRegion = rect//Rect(position.x, position.y, position.x+size.x, )

        var canvasRect = Rect(0,0,canvas.width,canvas.height)
        return Rect.intersects(canvasRect, updateRect())
    }

    fun getCenter(): Vector2 {
        return position.clone().add( size.clone().divide(2f))
    }
}