package com.ys.mydynamicapp.GameObjects

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import com.ys.mydynamicapp.GameUpdateListener
import com.ys.mydynamicapp.Hitbox
import com.ys.mydynamicapp.SoundManager
import com.ys.mydynamicapp.Vector2
import kotlin.math.absoluteValue
import kotlin.random.Random

val wateringCanHeight = 140
val initialTravelSpeed = 10
val wateringCanBorderPadding = 50
val speedIncrement = 1

class WateringCan(normalWateringCanImage: Drawable, rotatedWateringCanImage: Drawable, val fillSound: SoundManager.LoadedSound) : DynamicObject(
    Vector2(0, wateringCanHeight), Vector2(1785, 1700).divide(4f), Vector2(0,0), normalWateringCanImage),
    Pourable {

//    var px:Int = x
//    override fun move(canvas: Canvas) {
////        if (px > position.x+5)
////            position.x = position.x + 5 else position.x = position.x - 5
//        draw(canvas)
//    }
    private val normalWateringCan: Bitmap = bitmap!!
    private val rotatedWateringCan: Bitmap

    init {
        val tempBitmap = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888)
        rotatedWateringCanImage.setBounds(0,0,size.x, size.y)
        rotatedWateringCanImage.draw(Canvas(tempBitmap))
        rotatedWateringCan = tempBitmap
    }

    override var tankLimit = 100
    var waterTank = tankLimit
    override var isPouring = false

    var isUnderFaucet = false

    var gameUpdateListener: GameUpdateListener? = null
    val framesPerDroplet = 2
    var lastDropletFrame = -framesPerDroplet

    var lastRefillFrame: Int = 0
    var refilling: Boolean = false

    var bonusSpeed = 0

    override fun onCollision(hitbox: Hitbox, frameCount: Int): Boolean {
        if (hitbox.gameObject is WaterDroplet)
        {
            if (tankLimit > waterTank)
            {
                var amount = 3
                if (amount + waterTank > tankLimit)
                    waterTank = tankLimit
                else
                    waterTank += amount
                gameUpdateListener?.onWaterTankUpdate(waterTank)
                hitbox.gameObject.isUsable = false
                fillSound.play(0.6f, 1f, 0, 0, 1f)
                lastRefillFrame = frameCount
                if (!refilling)
                    refilling = true
                return true
            }
        }
        return false
    }

    override fun getDropletSpreadX(): Int {
        return Random.nextInt(-5,5)
    }

    fun getDistanceToLeftBorder(): Int {
        return (position.x - wateringCanBorderPadding)
    }

    fun getDistanceToRightBorder(canvas: Canvas): Int {
        return canvas.width - wateringCanBorderPadding - (size.x + position.x)
    }

    fun rotateWateringCan()
    {
        if (!isPouring and !isUnderFaucet and (bitmap != rotatedWateringCan))
        {
            bitmap = rotatedWateringCan
            isPouring = true
        }
    }

    fun levelWateringCan()
    {
        if (isPouring and (bitmap != normalWateringCan))
        {
            bitmap = normalWateringCan
            isPouring = false
        }
    }


    override fun getSprinklerPosition(): Vector2 {
        var sprinklerPosition = position.clone()
        sprinklerPosition.update(sprinklerPosition.x + 75, sprinklerPosition.y+size.y-50)
        return sprinklerPosition
    }

    override fun getDropletSize(): Vector2 {
        return Vector2(30)
    }

    override fun drain(amount: Int, frameCount: Int): Boolean {
        if ((waterTank > 0) and ((frameCount - lastDropletFrame) > framesPerDroplet))
        {
            lastDropletFrame = frameCount
            if (amount > waterTank)
                waterTank = 0
            else
                waterTank -= amount
            gameUpdateListener?.onWaterTankUpdate(waterTank)
            return true
        }
        return false
    }

    override fun draw(canvas: Canvas, frameCount: Int) {
        super.draw(canvas, frameCount)
        if (refilling && (lastRefillFrame + 10  < frameCount))
        {
            fillSound.stop()
            refilling = false
        }
    }


    private fun checkIfUnderFaucet(canvas: Canvas)
    {
        var distanceToRightBorder = getDistanceToRightBorder(canvas)
        if (isUnderFaucet)
        {
            if (distanceToRightBorder > 10) {
                isUnderFaucet = false
                hitbox = null
                gameUpdateListener?.onChangePourMode(isUnderFaucet)
            }
        } else if (distanceToRightBorder <= 10) {
            isUnderFaucet = true
            gameUpdateListener?.onChangePourMode(isUnderFaucet)
            var hitboxRect = Rect(rect)
            hitboxRect.top = 230
            hitbox = Hitbox(this, hitboxRect)
            levelWateringCan()
        }
    }

    fun getXVelocityWithBonus(): Int {
        return bonusSpeed.absoluteValue + initialTravelSpeed
    }

    override fun move(canvas: Canvas, frameCount: Int)
    {
        var xChange = velocity.x + bonusSpeed
        if (velocity.x != 0)
        {
            var distanceToBorder = 0

            if (velocity.x > 0) // To the Right
            {
                distanceToBorder = getDistanceToRightBorder(canvas)
                if (distanceToBorder < xChange)
                    xChange = distanceToBorder
                else if (bonusSpeed < 0)
                    bonusSpeed = 0
                else
                    bonusSpeed += speedIncrement

            } else if (velocity.x < 0) // To the left
            {
                distanceToBorder = getDistanceToLeftBorder()
                if (distanceToBorder < -xChange)
                    xChange = -distanceToBorder
                else if (bonusSpeed > 0)
                    bonusSpeed = 0
                else
                    bonusSpeed -= speedIncrement
            }

            checkIfUnderFaucet(canvas)


            if (distanceToBorder <= 0) {
                return velocity.update(0, 0)
            }



            position.x = position.x+xChange
            if (isUnderFaucet)
                super.move(canvas, frameCount)
        } else bonusSpeed = 0
    }
}