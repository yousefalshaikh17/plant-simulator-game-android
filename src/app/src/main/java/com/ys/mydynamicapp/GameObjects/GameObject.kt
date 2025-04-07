package com.ys.mydynamicapp.GameObjects

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import com.ys.mydynamicapp.Hitbox
import com.ys.mydynamicapp.Vector2

abstract class GameObject(var position: Vector2, var size: Vector2, image: Drawable?) {

    var isVisible = true
    var isUsable = true
    var hitbox: Hitbox? = null
    protected var rect: Rect? = null
    protected var bitmap: Bitmap? = null

//    open fun draw(canvas: Canvas, frameCount: Int) {
//        if (isRendering)
//        {
//            image?.setBounds(position.x, position.y, position.x + size.x, position.y + size.y)
//            image?.draw(canvas)
//        }
//    }
    init {
        if (image != null)
        {
            val bitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888)
//            Canvas(bitmap).drawBitmap(image!!, null, Rect(0,0,size.x, size.y), null)
            image.setBounds(0,0,size.x,size.y)
            image.draw(Canvas(bitmap))
            this.bitmap = bitmap
        }
    }

    open fun updateRect(): Rect {
        if (rect == null)
            rect = Rect()
        if (rect!!.left != position.x || rect!!.top != position.y)
        {
            rect!!.left = position.x
            rect!!.top = position.y
            rect!!.right = position.x + size.x
            rect!!.bottom = position.y + size.y
        }
        return rect!!
    }

    open fun draw(canvas: Canvas, frameCount: Int) {
        if (isVisible)
        {
            bitmap?.let {
                canvas.drawBitmap(it, position.x.toFloat(), position.y.toFloat(), null)
            }
        }
    }

    open fun tick(canvas: Canvas, frameCount: Int)
    {

    }


    open fun checkForIntersection(x: Int, y: Int): Boolean {
        return updateRect().contains(x,y)
    }

    open fun checkForIntersection(vector2: Vector2): Boolean {
        return checkForIntersection(vector2.x, vector2.y)
    }


    open fun onCollision(hitbox: Hitbox, frameCount: Int): Boolean {
        // Subclasses can use this.
        return false
    }

    open fun destroy()
    {
        isUsable = false
    }
}