package com.ys.mydynamicapp.GameObjects

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.ys.mydynamicapp.Hitbox
import com.ys.mydynamicapp.Vector2


class WaterDroplet(position: Vector2, size: Vector2, velocity: Vector2, image: Drawable) : DynamicObject(position, size, velocity, image) {

    private val maxSpeed = 30
    private val gravityRate = 2

    init {
        hitbox = Hitbox(this, updateRect())
    }

    private fun applyGravity()
    {
        if (velocity.y < maxSpeed)
            velocity.y += gravityRate
    }

    override fun move(canvas: Canvas, frameCount: Int) {
        applyGravity()
        position.add(velocity)
        // Commented out because the call to updateRect in hitbox is unnecessary.
//        hitbox?.updateRect(updateRect())
        updateRect()
    }
}