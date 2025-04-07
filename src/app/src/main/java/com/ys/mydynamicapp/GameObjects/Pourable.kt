package com.ys.mydynamicapp.GameObjects

import com.ys.mydynamicapp.Vector2

interface Pourable {
    var isPouring: Boolean
    var tankLimit: Int // If 0 then no limit
    fun getSprinklerPosition(): Vector2
    fun getDropletSize(): Vector2
    fun drain(amount: Int,frameCount: Int): Boolean
    fun getDropletSpreadX(): Int
}