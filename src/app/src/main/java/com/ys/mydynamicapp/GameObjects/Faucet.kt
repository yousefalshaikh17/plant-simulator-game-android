package com.ys.mydynamicapp.GameObjects

import android.graphics.drawable.Drawable
import com.ys.mydynamicapp.Vector2
import kotlin.random.Random

class Faucet(position: Vector2, image: Drawable) : StaticObject(position, Vector2(990,440).divide(3f), image),
    Pourable {

    override var isPouring = false
    override var tankLimit = 0

    var lastDropletFrame = 0
    val framesPerDroplet = 2

    override fun getSprinklerPosition(): Vector2 {
        var sprinklerPosition = position.clone()
        sprinklerPosition.update(sprinklerPosition.x + 35, sprinklerPosition.y+size.y-50)
        return sprinklerPosition
    }

    override fun getDropletSize(): Vector2 {
        return Vector2(40)
    }

    override fun drain(amount: Int, frameCount: Int): Boolean {
        if (amount > 0 && (frameCount - lastDropletFrame) > framesPerDroplet)
        {
            lastDropletFrame = frameCount
            return true
        }
        return false
    }

    override fun getDropletSpreadX(): Int {
        return Random.nextInt(-2,2)
    }

    fun enable()
    {
        isPouring = true
    }

    fun disable()
    {
        isPouring = false
    }

}