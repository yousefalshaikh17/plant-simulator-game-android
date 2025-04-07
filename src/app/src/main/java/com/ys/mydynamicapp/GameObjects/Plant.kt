package com.ys.mydynamicapp.GameObjects

import android.graphics.*
import android.graphics.drawable.Drawable
import com.ys.mydynamicapp.Hitbox
import com.ys.mydynamicapp.SoundManager
import com.ys.mydynamicapp.Vector2
import kotlin.random.Random


class Plant(position: Vector2, potImage: Drawable, aliveImage: Drawable, deadImage: Drawable, private var dripSounds: Array<SoundManager.LoadedSound?>, private val deathCallback: (Plant)->Unit) : StaticObject(position, Vector2(200,190), null) {

    private val plantSizeY = 300
    private val healthGauge = Gauge(Vector2(position.x,position.y+(size.y/3)), Vector2(size.x,70), 3f, 20f, 100, ::fetchHealth)
    private var health = 100
    private var lastDry: Int = 0

    private val aliveBitmap: Bitmap = generatePlantImage(potImage, aliveImage)
    private val deadBitmap: Bitmap = generatePlantImage(potImage, deadImage)

    // For the use of drip sounds
    var canvasWidth = 0

    val isAlive: Boolean
        get() = (health > 0)

    init {
        hitbox = Hitbox(this, getHitboxCoordinates())
        bitmap = aliveBitmap
    }

    fun updateHealthGaugePos()
    {
        healthGauge.position.update(position.x,position.y+(size.y/3))
    }


    private fun playDripSound()
    {
        // calculate left volume using positioning of the plant on the canvas
        val leftVolume = (position.x+(size.x/2)).toFloat() / canvasWidth.toFloat()
        // Play drip sound using left volume and calculating right volume by subtracting 1 by left volume
        dripSounds[Random.nextInt(0,dripSounds.size)]!!.play(leftVolume, 1-leftVolume, 0,0,1f)
    }

    private fun generatePlantImage(potImage: Drawable, plantImage: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(size.x, plantSizeY+size.y-30, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        plantImage.setBounds(0, 0, size.x, plantSizeY)
        plantImage.draw(canvas)
//        canvas.drawBitmap(plantImage, null, Rect(0, 0, plantSize.x, plantSize.y), null)
        potImage.setBounds(0, plantSizeY-30, size.x, size.y + plantSizeY-30)
        potImage.draw(canvas)
//        canvas.drawBitmap(potImage, null, Rect(0, plantSize.y-30, size.x, size.y + plantSize.y-30), null)
        return bitmap
    }

    fun fetchHealth(): Int {
        return health
    }


    private fun die()
    {
        if (bitmap != deadBitmap){
            bitmap = deadBitmap
            deathCallback(this)
        }
    }

    private fun dry(frameCount: Int)
    {
        if (isAlive)
        {
            if (lastDry + 8 < frameCount)
            {
                health -= Random.nextInt(0,3)
                if (health <= 0)
                {
                    health = 0
                    die()
                }
                lastDry = frameCount
            }
        }
    }

    private fun water()
    {
        if (isAlive)
            if (health + 2 > 100)
                die()
            else
                health += 2
    }

    fun updateHitbox()
    {
        hitbox!!.updateRect(getHitboxCoordinates())
    }

    private fun getHitboxCoordinates(): Rect {
        return Rect(position.x, position.y-15, position.x + size.x, position.y+40)
    }

    override fun draw(canvas: Canvas, frameCount: Int) {
        if (canvas.width != canvasWidth)
            canvasWidth = canvas.width

        canvas.drawBitmap(bitmap!!, position.x.toFloat(), (position.y-plantSizeY+30).toFloat(), null)

        if (isAlive)
            healthGauge.draw(canvas, frameCount)
    }

    override fun tick(canvas: Canvas, frameCount: Int) {
        super.tick(canvas, frameCount)
        if (isAlive)
            dry(frameCount)
    }

    override fun onCollision(hitbox: Hitbox, frameCount: Int): Boolean {
        if (hitbox.gameObject is WaterDroplet)
        {
            water()
            hitbox.gameObject.isUsable = false
            playDripSound()
            return true
        }
        return false
    }
}