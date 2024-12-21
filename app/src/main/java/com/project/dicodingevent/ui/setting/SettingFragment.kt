package com.project.dicodingevent.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager
import com.project.dicodingevent.background.ReminderScheduler
import com.project.dicodingevent.data.preferences.DarkModePreferences
import com.project.dicodingevent.data.preferences.ReminderPreferences
import com.project.dicodingevent.data.preferences.dataStore
import com.project.dicodingevent.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {
    // View Binding
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    // ViewModels and Preferences
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var reminderPreferences: ReminderPreferences

    // Lifecycle Methods
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
        setupViewModel()
        setupDarkModeSwitch()
        setupDailyReminderSwitch()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Setup Methods
    private fun setupViewModel() {
        val darkModePreferences = DarkModePreferences.getInstance(requireContext().dataStore)
        settingViewModel = ViewModelProvider(
            this,
            SettingViewModelFactory(darkModePreferences)
        )[SettingViewModel::class.java]
    }

    private fun setupDarkModeSwitch() {
        settingViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            binding.switchDarkMode.isChecked = isDarkModeActive
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            settingViewModel.saveThemeSetting(isChecked)
        }
    }

    private fun setupDailyReminderSwitch() {
        reminderPreferences = ReminderPreferences(requireContext())

        with(binding.switchDailyReminder) {
            isChecked = reminderPreferences.getDailyReminder()

            setOnCheckedChangeListener { _, isChecked ->
                reminderPreferences.setDailyReminder(isChecked)
                handleReminderWorkManager(isChecked)
            }
        }
    }

    // Helper Methods
    private fun handleReminderWorkManager(isEnabled: Boolean) {
        if (isEnabled) {
            ReminderScheduler.scheduleDailyReminder(requireContext())
        } else {
            WorkManager.getInstance(requireContext())
                .cancelUniqueWork(DAILY_REMINDER_WORK_NAME)
        }
    }

    companion object {
        private const val DAILY_REMINDER_WORK_NAME = "DailyReminderWork"
    }
}