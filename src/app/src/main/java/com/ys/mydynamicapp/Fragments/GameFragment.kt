package com.ys.mydynamicapp.Fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ys.mydynamicapp.*
import com.ys.mydynamicapp.GameObjects.initialTravelSpeed

import com.ys.mydynamicapp.databinding.FragmentGameBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class GameFragment : Fragment(), GameUpdateListener {

    private lateinit var gameView: GameSurfaceView
    private val viewModel: GameViewModel by activityViewModels()
    private var backButtonCallback = object:OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            gamePause()
        }
    }




    private lateinit var binding: FragmentGameBinding
    private var soundPool: SoundPool? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  FragmentGameBinding.inflate(inflater, container, false)

        // Create listener for back button to pause game.
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backButtonCallback)

        gameView = binding.gameView

        soundPool = SoundPool.Builder().setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        ).setMaxStreams(2).build()


        initializeButton(binding.restartButton, null, {findNavController().navigate(R.id.action_gameFragment_self)})

        initializeButton(binding.pausePlayButton, null) {
            if (gameView.gamePlaying)
                gamePause()
            else
                gameResume()
        }



        val wateringCan = gameView.wateringCan



        val faucet = gameView.faucet
        gameView.registerListener(this)

        initializeButton(binding.waterButton, { wateringCan.rotateWateringCan(); faucet.enable() }, { wateringCan.levelWateringCan(); faucet.disable() })


        if (viewModel.usesAccelerometer.value!!)
        {
            viewModel.accelerometerInfoUpDown.observe(viewLifecycleOwner) {
                wateringCan.velocity.x = (initialTravelSpeed.toFloat() * it/10).toInt()
            }
            binding.rightArrowButton.visibility = View.GONE
            binding.leftArrowButton.visibility = View.GONE
        } else {
            val stopWateringCan = {wateringCan.velocity.x = 0}
            initializeButton(binding.rightArrowButton, {wateringCan.velocity.x = initialTravelSpeed }, stopWateringCan)
            initializeButton(binding.leftArrowButton, {wateringCan.velocity.x = -initialTravelSpeed; faucet.isPouring = false}, stopWateringCan)
        }

        if (viewModel.showFPSCounter.value!!)
            binding.fpsCounter.visibility = View.VISIBLE

        binding.waterLevelBar.max = wateringCan.tankLimit



        // Return root
        return binding.root
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initializeButton(button: ImageButton, onTouch: (() -> Unit)?, onRelease: (()->Unit)?)
    {
        if (Build.VERSION.SDK_INT >= 23)
            button.imageTintList = resources.getColorStateList(R.color.black, null).withAlpha(200)
        else {
            @SuppressLint("NewApi")
            button.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.black, null)).withAlpha(200)
        }
        button.imageTintMode = null
        button.setOnTouchListener { _, event ->
            when(event.actionMasked)
            {
                MotionEvent.ACTION_UP -> {
                    onRelease?.invoke()
                    button.imageTintMode = null
                }
                MotionEvent.ACTION_DOWN -> {
                    onTouch?.invoke()
                    button.imageTintMode = PorterDuff.Mode.MULTIPLY
                }
            }
            return@setOnTouchListener true
        }
    }

    override fun onPause() {
        super.onPause()
        backButtonCallback.isEnabled = false
        gamePause()
    }

    fun gamePause()
    {
        gameView.onPause()
        binding.pausePlayButton.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.play_button,
                null
            )
        )
        binding.restartButton.visibility = View.VISIBLE

    }

    private fun gameResume()
    {
        gameView.onResume()
        binding.pausePlayButton.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.pause_button,
                null
            )
        )
        binding.restartButton.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        backButtonCallback.isEnabled = true
    }


    override fun onWaterTankUpdate(capacity: Int) {
        binding.waterLevelBar.progress = capacity
    }

    override fun onChangePourMode(underFaucet: Boolean) {

        MainScope().launch {
            val resID = if (underFaucet)
                R.drawable.faucet_button
            else
                R.drawable.water_button
            binding.waterButton.setImageDrawable(ResourcesCompat.getDrawable(resources, resID, null))
        }

    }

    override fun onFrameCountUpdate(frameCount: Int) {
        MainScope().launch {
            // Calculate time in minutes:seconds:milliseconds
            val timeInMS: Int = 1000/60*frameCount
            val timeInSeconds: Int = timeInMS / 1000
//            val timeInMinutes: Int = timeInSeconds / 60
//            val string = timeInMinutes.toString().padStart(2,'0') +":"+(timeInSeconds % 60).toString().padStart(2,'0')+":"+(timeInMS%1000).toString().take(2).padStart(2,'0')

            binding.gameStopwatch.text =  getString(
                R.string.stopwatch_time,
                (timeInSeconds / 60).toString().padStart(2,'0'),
                (timeInSeconds % 60).toString().padStart(2,'0'),
                (timeInMS%1000).toString().trim().take(2).padStart(2,'0')
            )
        }
    }

    override fun onGameOver(frameCount: Int) {
        MainScope().launch {
            viewModel.latestScoreInFrames.value = frameCount
            findNavController().navigate(R.id.action_gameFragment_to_gameOverFragment)
        }
    }

    override fun onFPSUpdate(fps: Int) {
        MainScope().launch {
            if (viewModel.showFPSCounter.value!!)
                binding.fpsCounter.text = getString(R.string.fps_message, fps)
        }
    }

}