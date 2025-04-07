package com.ys.mydynamicapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ys.mydynamicapp.databinding.FragmentGameOverBinding


class GameOverFragment : Fragment() {
    private val viewModel: GameViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentGameOverBinding.inflate(inflater, container, false)
        val score = viewModel.latestScoreInFrames.value!!
        val timeInMS: Int = 1000/60 * score
        val timeInSeconds: Int = timeInMS / 1000

        binding.timeSpent.text =  getString(
            R.string.stopwatch_time,
            (timeInSeconds / 60).toString().padStart(2,'0'),
            (timeInSeconds % 60).toString().padStart(2,'0'),
            (timeInMS%1000).toString().trim().take(2).padStart(2,'0')
        )

        var hasHighScore = false
        if (viewModel.highestScoreInFrames.value == 0)
            viewModel.highestScoreInFrames.value = score
        else if (viewModel.highestScoreInFrames.value!! < score)
        {
            viewModel.highestScoreInFrames.value = score
            binding.highscoreStar.visibility = View.VISIBLE
            hasHighScore = true
        }

        binding.gameOverMessage.text = getString(getGameOverMessageResID(timeInSeconds, hasHighScore))

        // Setup navigation to main menu.
        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_gameOverFragment_to_mainMenuFragment)
        }

        // Setup navigation to settings.
        binding.settingsButton.setOnClickListener {
            findNavController().navigate(R.id.action_gameOverFragment_to_settingsFragment)
        }

        // Setup navigation to game fragment. (Restarting the game)
        binding.retryButton.setOnClickListener {
            findNavController().navigate(R.id.action_gameOverFragment_to_gameFragment)
        }


        // Return view
        return binding.root
    }

    // One time thing so no need to worry about garbage collection. Saves class memory (even though its just int arrays)
    private fun getGameOverMessageResID(seconds: Int, highScore: Boolean): Int {
        if (seconds > 60)
        {
            return if (highScore)
                R.string.great_high_score
            else
                arrayOf(R.string.great1, R.string.great2, R.string.great3).random()
        } else if (seconds > 40)
        {
            return if (highScore)
                R.string.moderate_high_score
            else
                arrayOf(R.string.moderate1, R.string.moderate2, R.string.moderate3).random()
        } else {
            return if (highScore)
                R.string.awful_high_score
            else
                arrayOf(R.string.awful1, R.string.awful2, R.string.awful3).random()
        }
    }
}