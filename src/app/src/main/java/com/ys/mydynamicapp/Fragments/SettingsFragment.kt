package com.ys.mydynamicapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ys.mydynamicapp.GameViewModel
import com.ys.mydynamicapp.R
import com.ys.mydynamicapp.databinding.FragmentSettingsBinding as FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private val viewModel: GameViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentSettingsBinding.inflate(inflater, container, false)
        if (viewModel.canUseAccelerometer.value == true)
        {
            binding.useAccelerometerSwitch.isChecked = viewModel.usesAccelerometer.value!!
            binding.useAccelerometerSwitch.setOnClickListener{
                viewModel.updateAccelerometerSettings(binding.useAccelerometerSwitch.isChecked)
            }
        } else {
            binding.useAccelerometerSwitch.isClickable = false
        }

        binding.fpsCounterSwitch.isChecked = viewModel.showFPSCounter.value!!
        binding.fpsCounterSwitch.setOnClickListener{
            viewModel.showFPSCounter.value = binding.fpsCounterSwitch.isChecked
        }


        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_mainMenuFragment)
        }



        return binding.root
    // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

}