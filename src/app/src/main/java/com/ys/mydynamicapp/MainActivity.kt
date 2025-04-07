package com.ys.mydynamicapp

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate


class MainActivity : AppCompatActivity() {

    private val viewModel: GameViewModel by viewModels()
    private var sensorManager: SensorManager? = null
    private var accelerometerEventListener: SensorEventListener? = null
    private var accelerometerSensor: Sensor? = null

    val usingAccelerometer: Boolean
        get() = accelerometerSensor != null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_main)
        viewModel.usesAccelerometer.observe(this) { usesGyro ->
            if (usesGyro) {
                if (!usingAccelerometer) {
                    initializeSensor()
                    sensorManager!!.registerListener(
                        accelerometerEventListener,
                        accelerometerSensor,
                        SensorManager.SENSOR_DELAY_GAME
                    )
                }
            }else
                disableSensor()
        }

        // Check SDK version to separate deprecated functions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars() or WindowInsets.Type.systemBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Using old API so should be fine despite being deprecated in API 30
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN)
        }


    }

    private fun disableSensor()
    {
        if (usingAccelerometer)
        {
            sensorManager!!.unregisterListener(accelerometerEventListener)
            sensorManager = null
            accelerometerSensor = null
            accelerometerEventListener = null
        }
    }

    private fun initializeSensor()
    {
        if (accelerometerSensor == null && viewModel.canUseAccelerometer.value == true)
        {
            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            accelerometerSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            if (accelerometerSensor == null) {
                Toast.makeText(this, "This device does not have a gyroscope.", Toast.LENGTH_SHORT).show()
                viewModel.canUseAccelerometer.value = false
                viewModel.usesAccelerometer.value = false
            }
            else {
                accelerometerEventListener = object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent?) {
                        viewModel.updateAccelerometerData(event!!.values[1])
                    }

                    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.usesAccelerometer.value == true)
            initializeSensor()
        if (usingAccelerometer)
            sensorManager!!.registerListener(accelerometerEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME)
    }


    override fun onPause() {
        if (usingAccelerometer)
            sensorManager!!.unregisterListener(accelerometerEventListener)
        super.onPause()
    }

}