package com.ys.mydynamicapp.Fragments

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.ys.mydynamicapp.R
import com.ys.mydynamicapp.SoundManager
import com.ys.mydynamicapp.databinding.FragmentMainMenuBinding


class MainMenuFragment : Fragment() {
    lateinit var birdsChirping: SoundManager.UnstackableLoadedSound

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding =  FragmentMainMenuBinding.inflate(inflater, container, false)


        birdsChirping = SoundManager(requireContext()).load(R.raw.chirping, 1, false) as SoundManager.UnstackableLoadedSound
        birdsChirping.play(1f, 1f, 1, 1, 1f)
        var lastBackPressed = 0L
        binding.backButton.setOnClickListener{
            val currentTime = System.currentTimeMillis()
            if (lastBackPressed + 2000 > currentTime) {
                stopChirping()
                requireActivity().finish()
            }
            else {
                Toast.makeText(requireContext(), "Press back again to exit.", Toast.LENGTH_SHORT).show()
                lastBackPressed = currentTime
            }
        }

        binding.startButton.setOnClickListener {
            stopChirping()
            findNavController().navigate(R.id.action_mainMenuFragment_to_gameFragment)
        }
        binding.settingsButton.setOnClickListener {
            stopChirping()
            findNavController().navigate(R.id.action_mainMenuFragment_to_settingsFragment)
        }

        return binding.root
    }


    fun stopChirping() {
        birdsChirping.stop()
        birdsChirping.unload()
    }

}