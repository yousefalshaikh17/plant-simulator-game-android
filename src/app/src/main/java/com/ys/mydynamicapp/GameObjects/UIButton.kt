// DEPRECATED

package com.ys.mydynamicapp.GameObjects

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import com.ys.mydynamicapp.Vector2


@Deprecated(message = "UIButton gameobject was deprecated in favor of actual buttons handled by the fragment")
class UIButton(position: Vector2, size: Vector2, image: Drawable, var pushedCallback:()->Unit, var releasedCallback: (() -> Unit)?) : StaticObject(position, size, image) {

    var isPushed = false
    var touchCoordinates: Vector2? = null

    fun onTouch(touchCoordinates: Vector2)
    {
        this.touchCoordinates = touchCoordinates
        isPushed = true
        pushedCallback()
    }

    fun onRelease()
    {
        isPushed = false
        touchCoordinates = null
        releasedCallback?.let { it() }
    }

    override fun draw(canvas: Canvas, frameCount: Int) {
        var paint = Paint()
        paint.color = Color.BLUE
        canvas.drawRect(updateRect(), paint)
    }
}