package com.ys.mydynamicapp

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.res.ResourcesCompat
import com.ys.mydynamicapp.GameObjects.*
import kotlin.concurrent.thread


class GameSurfaceView(context: Context?, attrs: AttributeSet?) : SurfaceView(context, attrs), Runnable {
    private var isRunning = true
    private var initialized = false
    private val myThread: Thread = Thread(this)
    private val myHolder: SurfaceHolder = holder
    private var initializationList = ArrayList<(canvas: Canvas)->Unit>()


    var gamePlaying = true
    var frameCount = 0


    private var bgBitmap :Bitmap? = null
    private var gameUpdateListener: GameUpdateListener? = null

    val faucet = Faucet(Vector2(0), getDrawable(R.drawable.faucet))

    // Game Objects in layering system.
    private val gameObjectLayers = arrayOf(ArrayList<GameObject>(), ArrayList())

    private val soundManager: SoundManager = SoundManager(context!!)
//    private val plantDeathSounds = arrayOf(soundManager.load(R.raw.gasp, 1, true), soundManager.load(R.raw.gasp2, 1, true))
    private val gameEndSound = soundManager.load(R.raw.whistle, 1, false)

    val wateringCan: WateringCan = WateringCan(
        getDrawable(R.drawable.watering_can),
        getDrawable(R.drawable.watering_can_rotated),
        soundManager.load(R.raw.watering_can_refill, 1, false)!!
    )

    // Load early to save on resources
    val dropletImage = getDrawable(R.drawable.water_droplet)

    init {

        initializationList.add { canvas: Canvas -> wateringCan.position.x = (canvas.width/2)-(wateringCan.size.x/2) }
        gameObjectLayers[0].add(wateringCan)

        gameObjectLayers[1].add(faucet)
        initializationList.add { canvas: Canvas -> faucet.position.update(canvas.width-faucet.size.x + 90, 30)}

        val dripSounds = arrayOf(soundManager.load(R.raw.click_end, 1, true))

        // Initialize these here to save on the objects the garbage collector has to deal with.
        val potImage = getDrawable(R.drawable.pot)
        val alivePlantImage = getDrawable(R.drawable.plant)
        val deadPlantImage = getDrawable(R.drawable.plant_dead)


        var livingPlants = 3
        val onPlantDeathCallback: (Plant) -> Unit =  { plant ->
            livingPlants--
            if (livingPlants <= 2)
            {
                gamePlaying = false
                isRunning = false
            }
        }

        for (i in 0 until livingPlants){
            val plant = Plant(Vector2(490+(i*300),0), potImage, alivePlantImage, deadPlantImage, dripSounds, onPlantDeathCallback)
            gameObjectLayers[1].add(plant)
            initializationList.add{canvas -> plant.position.y = canvas.height - (canvas.height*0.1457f).toInt()-plant.size.y; plant.updateHitbox(); plant.updateHealthGaugePos() }
        }

        myThread.start()
    }


    fun registerListener(listener: GameUpdateListener)
    {
        gameUpdateListener = listener
        wateringCan.gameUpdateListener = listener
    }

    private fun getDrawable(resID: Int): Drawable {
        return ResourcesCompat.getDrawable(resources, resID, null)!!
    }

//
//    private fun getBitmap(drawable: Int): Bitmap {
//        return generateBitmap(drawable, false)
//    }
//
//    private fun generateBitmap(drawable: Int, inScaled: Boolean): Bitmap {
//        val options = BitmapFactory.Options()
//        options.inScaled = inScaled
//        return BitmapFactory.decodeResource(resources, drawable, options)
//    }

    private fun createDroplet(pourable: Pourable) {
        var objXVelocity = 0
        if (pourable is DynamicObject)
            objXVelocity = pourable.velocity.x
        gameObjectLayers[0].add(WaterDroplet(pourable.getSprinklerPosition(), pourable.getDropletSize(), Vector2(objXVelocity+ pourable.getDropletSpreadX(),0),dropletImage))
    }


    private fun drawWorld(tickGame: Boolean)
    {
        if (tickGame) {
            frameCount++
            if (wateringCan.isPouring && wateringCan.drain(1, frameCount))
                createDroplet(wateringCan)

            if (faucet.isPouring && wateringCan.isUnderFaucet && faucet.drain(wateringCan.tankLimit-wateringCan.waterTank, frameCount))
                createDroplet(faucet)
        }
        val canvas: Canvas = myHolder.lockCanvas()

        if (!initialized)
        {
            for (function in initializationList)
                function(canvas)

            val bitmap = Bitmap.createBitmap(canvas.width, canvas.height, Bitmap.Config.RGB_565)
            val bgCanvas = Canvas(bitmap)
            val bgImg = getDrawable(R.drawable.game_background) // Stretched photo to fit many displays
            val imgWidthToHeight = 2.42f
            val width = (imgWidthToHeight * canvas.height.toFloat()).toInt()

            bgImg.setBounds(0,0,width, canvas.height)
            bgImg.draw(bgCanvas)

//            val windowImg = getDrawable(R.drawable.window_shelf)
//            windowImg.setBounds(0,0, 573, 549)
//            windowImg.draw(bgCanvas)
            bgBitmap = bitmap
            initialized = true
            initializationList.clear()
        }
        canvas.drawBitmap(bgBitmap!!, 0f,0f, null)



        for (i in 0..gameObjectLayers.lastIndex)
        {
            val layer = gameObjectLayers[i]
            for (j in 0..layer.lastIndex)
            {
                val gameObject = layer.getOrNull(j)
                if (gameObject != null)
                {
                    if (!gameObject.isUsable && tickGame) {
                        layer.removeAt(j)
                        continue
                    }
                    if (tickGame)
                        gameObject.tick(canvas, frameCount)
                    // Commented out in favor of callbacks on death
//                        if (gameObject is Plant && livingPlants.contains(gameObject) && !gameObject.isAlive ) {
//                            livingPlants.remove(gameObject)
//                            if (livingPlants.size > 2)
//                            {
//                                val leftVolume = (gameObject.position.x+(gameObject.size.x/2)).toFloat() / canvas.width.toFloat()
//                                plantDeathSounds.random()!!.play(leftVolume,1f - leftVolume, 0,0,1f)
//                            } else {
//                                gameEndSound!!.play(1f,1f,0,0,1f)
//                                isRunning = false
//                                endGame()
//                            }
//                        }

                    gameObject.hitbox?.checkForCollisions(gameObjectLayers, frameCount)

                    gameObject.draw(canvas, frameCount)
                }
            }
        }
//            Removed in favor of View UI stopwatch.
//            var timeInMS: Int = 1000/60*frameCount
//            var timeInSeconds: Int = timeInMS / 1000
//            var timeInMinutes: Int = timeInSeconds / 60
//            var string = timeInMinutes.toString().padStart(2,'0') +":"+(timeInSeconds % 60).toString().padStart(2,'0')+":"+(timeInMS%1000).toString().take(2).padStart(2,'0')
//            paint.color = Color.BLACK
//            paint.textSize = 100f
//            canvas.drawText(string,canvas.width/10f,120f,paint)
        myHolder.unlockCanvasAndPost(canvas)
        if (tickGame)
            gameUpdateListener?.onFrameCountUpdate(frameCount)
    }

    override fun run() {

        var fps = 0
        var lastSecond: Long = 0
        var lastFPS = -1


        while (isRunning)
        {
            if (!myHolder.surface.isValid)
                continue

            if (lastFPS == -1)
                gameUpdateListener?.onFPSUpdate(0)

            if (System.currentTimeMillis()  >= lastSecond + 1000)
            {
                if (lastFPS != fps)
                    gameUpdateListener?.onFPSUpdate(fps)
                lastFPS = fps
                lastSecond = System.currentTimeMillis()
                fps = 0
            }

            if (!gamePlaying)
                continue



            drawWorld(true)

            fps++
        }
        gameEndSound!!.play(1.0f, 1.0f, 1, 0, 1f)
        Thread.sleep(1500)
        endGame()
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        initialized = false
//        Thread.sleep(500)
//        initialized = false
//    }

//    var pressedButtons: HashMap<Int,UIButton> = HashMap()


    fun onPause()
    {
        if (gamePlaying)
        {
            gamePlaying = false
//            pausePlayButton.image = playImage
        }
    }

    fun onResume()
    {
        if (!gamePlaying)
        {
            gamePlaying = true
//            pausePlayButton.image = pauseImage
        }
    }

    private fun endGame()
    {
        // Clean up sounds
        soundManager.release()
        // Tell fragment game is over
        gameUpdateListener?.onGameOver(frameCount)
    }

//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        val action = event!!.actionMasked
//        when (action)
//        {
//            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
//
//                for (gameObject in gameObjectLayers[3])
//                    if (gameObject is UIButton)
//                        if (gameObject.checkForIntersection(event.getX(event.actionIndex).toInt(), event.getY(event.actionIndex).toInt())) {
//                            // prioritize calling onClick functions of UIButton
//                            gameObject.onTouch(Vector2(event.getX(event.actionIndex).toInt(), event.getY(event.actionIndex).toInt()))
//                            pressedButtons[event.getPointerId(event.actionIndex)] = gameObject
////                            Log.d("TESTING", "TOUCH")
//                        }
//            }
//            MotionEvent.ACTION_MOVE -> {
//                for ((pointer, button) in pressedButtons)
//                {
//                    var index = event.findPointerIndex(pointer)
//                    if (index == -1)
//                        continue
//                    var touchPosition = Vector2(event.getX(index).toInt(), event.getY(index).toInt())
//                    var buttonTouchPosition = button.touchCoordinates
//                    if (buttonTouchPosition != null) {
//                        if (!buttonTouchPosition.equals(touchPosition)) {
//                            if (!button.checkForIntersection(touchPosition)) {
//                                button.onRelease()
//                            } else button.touchCoordinates = touchPosition
//                        }
//                    }
//                }
//            }
//            MotionEvent.ACTION_POINTER_UP -> {
//                pressedButtons.remove(event.findPointerIndex(event.actionIndex))?.onRelease()
//            }
//            MotionEvent.ACTION_UP -> {
//                for ((_, button) in pressedButtons) {
//                    button.onRelease()
//                }
//                pressedButtons.clear()
//            }
//        }
//
//
//
//        return true
//    }

//    var pointer = 0
//
//    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        val action = event?.actionMasked
//
//        when(action)
//        {
//            MotionEvent.ACTION_POINTER_DOWN -> {
//                Log.d("TESTING", "Pointer down. Total pointers: "+event.pointerCount)
//            }
//            MotionEvent.ACTION_POINTER_UP -> {
//                Log.d("TESTING", "Pointer up. Total pointers: "+(event.pointerCount-1))
//            }
//
//
//
//            MotionEvent.ACTION_DOWN -> {
//                Log.d("TESTING", "Pushed down.")
//                pointer = event.x.toInt()
//            }
//
//            MotionEvent.ACTION_UP -> {
//                Log.d("TESTING", "Let go completely.")
//            }
//        }
//
//        return true
//    }

    //    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        val action = event!!.action
//
//
//
//        for (gameObject in gameObjectLayers[2])
//            if (gameObject is UIButton)
//                if (gameObject.checkForIntersection(event!!.x.toInt(), event!!.y.toInt())) {
//                    when(action)
//                    {
//                        MotionEvent.ACTION_DOWN -> {
//                            gameObject.onClick()
//                            map[event] = Vector2(event!!.x.toInt(), event!!.y.toInt())
//                        }
//                        //MotionEvent.ACTION_MOVE -> gameObject.isPushed = false
//                        MotionEvent.ACTION_UP -> {
//                            gameObject.onRelease()
//                            map.remove(event)
//                        }
//                    }
//                } else if (map.get(event) != null && gameObject.checkForIntersection(map.get(event)!!.x, map.get(event)!!.y)) {
//                    gameObject.onRelease()
//                    map.remove(event)
//                }
//
//        return true
//    }
}