package com.ys.mydynamicapp.GameObjects

import android.graphics.drawable.Drawable
import com.ys.mydynamicapp.Vector2

abstract class StaticObject(position: Vector2, size: Vector2, image: Drawable?) : GameObject(position, size, image) {

//    var initialized = false
//
//    override fun draw(canvas: Canvas, frameCount: Int) {
//        if (!initialized)
//        {
//            //initialized = true;
//            super.draw(canvas, frameCount);
//        }
//    }
}