package com.ys.mydynamicapp

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.ys.mydynamicapp.GameObjects.GameObject


class Hitbox(val gameObject: GameObject, var hitboxRect: Rect) {


    fun checkForCollisions(gameObjectLayers: Array< ArrayList<GameObject> >, frameCount: Int)
    {
        for (i in 0..gameObjectLayers.lastIndex)
            for (gameObj in gameObjectLayers[i])
            {
                if ((gameObj.hitbox != null) and (gameObj != gameObject))
                {
//                    Log.d("TESTING", "has hitbox")

//                    Log.d("TESTING", "Hitbox boundary: "+boundingBox.toString())
//                    Log.d("TESTING", "Enemy boundary: "+gameObj.hitbox!!.boundingBox.toString())

                    if (Rect.intersects(gameObj.hitbox!!.hitboxRect, hitboxRect))
                    {
//                        Log.d("TESTING", "Touching!")
                        if (gameObject.onCollision(gameObj.hitbox!!, frameCount))
                            return
                    }


                }
            }
    }

    fun updateRect(hitboxRect: Rect)
    {
        this.hitboxRect = hitboxRect
    }

    // Meant for debugging
    fun visualizeHitbox(canvas: Canvas)
    {
        var paint = Paint()
        paint.color = Color.BLUE
        canvas.drawRect(hitboxRect, paint)
    }
}