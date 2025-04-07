package com.ys.mydynamicapp


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private val accelerometerDataUpDown = MutableLiveData<Float>()
    val accelerometerInfoUpDown: MutableLiveData<Float> get() = accelerometerDataUpDown

    private val accelerometerEnabled = MutableLiveData<Boolean>()
    val usesAccelerometer: MutableLiveData<Boolean> get() = accelerometerEnabled

    private val accelerometerUsable = MutableLiveData<Boolean>()
    val canUseAccelerometer: MutableLiveData<Boolean> get() = accelerometerUsable

    private val fpsCounter = MutableLiveData<Boolean>()
    val showFPSCounter: MutableLiveData<Boolean> get() = fpsCounter

    private val highestScore = MutableLiveData<Int>()
    val highestScoreInFrames: MutableLiveData<Int> get() = highestScore

    private val latestScore = MutableLiveData<Int>()
    val latestScoreInFrames: MutableLiveData<Int> get() = latestScore


    init {
        accelerometerEnabled.value = false
        accelerometerUsable.value = true
        fpsCounter.value = false
        highestScore.value = 0
        latestScore.value = 0
    }

    fun updateAccelerometerData(gyroData: Float)
    {
        this.accelerometerDataUpDown.value = gyroData
    }

    fun updateAccelerometerSettings(usesGyro: Boolean)
    {
        accelerometerEnabled.value = usesGyro
    }


}