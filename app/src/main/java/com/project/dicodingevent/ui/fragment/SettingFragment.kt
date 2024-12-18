package com.project.dicodingevent.ui.fragment

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.project.dicodingevent.data.local.datastore.SettingPreferences
import com.project.dicodingevent.data.local.datastore.dataStore
import com.project.dicodingevent.databinding.FragmentSettingBinding
import com.project.dicodingevent.ui.factory.SettingModelFactory
import com.project.dicodingevent.ui.model.SettingViewModel

class SettingFragment : Fragment() {

    private companion object {
        const val NOTIFICATION_PERMISSION_SDK = 33
    }

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingViewModel by viewModels {
        SettingModelFactory(SettingPreferences.getInstance(requireContext().dataStore))
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val message = if (isGranted) {
            "Notifications permission granted"
        } else {
            "Notifications permission rejected"
        }
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNotificationPermission()
        setupObservers()
        setupSwitchListeners()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= NOTIFICATION_PERMISSION_SDK) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun setupObservers() {
        viewModel.themeSettings.observe(viewLifecycleOwner) { isDarkModeActive ->
            updateThemeMode(isDarkModeActive)
        }

        viewModel.reminderSettings.observe(viewLifecycleOwner) { isReminderActive ->
            binding.switchReminder.isChecked = isReminderActive
        }
    }

    private fun updateThemeMode(isDarkModeActive: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        binding.switchTheme.isChecked = isDarkModeActive
    }

    private fun setupSwitchListeners() {
        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateReminderSetting(isChecked)
            viewModel.toggleDailyReminder(requireContext(), isChecked)
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateThemeSetting(isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}