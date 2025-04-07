package com.ys.mydynamicapp

interface GameUpdateListener {
    fun onWaterTankUpdate(capacity: Int)
    fun onChangePourMode(underFaucet: Boolean)
    fun onFrameCountUpdate(frameCount: Int)
    fun onGameOver(frameCount: Int)
    fun onFPSUpdate(fps: Int)
}