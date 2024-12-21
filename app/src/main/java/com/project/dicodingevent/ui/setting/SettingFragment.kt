package com.project.dicodingevent.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager
import com.project.dicodingevent.background.ReminderScheduler
import com.project.dicodingevent.data.preferences.DarkModePreferences
import com.project.dicodingevent.data.preferences.ReminderPreferences
import com.project.dicodingevent.data.preferences.dataStore
import com.project.dicodingevent.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var reminderPreferences: ReminderPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pref = DarkModePreferences.getInstance(requireContext().dataStore)
        val settingViewModel = ViewModelProvider(this, SettingViewModelFactory(pref)).get(
            SettingViewModel::class.java
        )
        settingViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive: Boolean ->
            binding.switchDarkMode.isChecked = isDarkModeActive
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingViewModel.saveThemeSetting(isChecked)
        }

        reminderPreferences = ReminderPreferences(requireContext())
        val isDailyReminderEnabled = reminderPreferences.getDailyReminder()
        binding.switchDailyReminder.isChecked = isDailyReminderEnabled

        binding.switchDailyReminder.setOnCheckedChangeListener { _, isChecked ->
            reminderPreferences.setDailyReminder(isChecked)

            if (isChecked) {
                ReminderScheduler.scheduleDailyReminder(requireContext())
            } else {
                WorkManager.getInstance(requireContext()).cancelUniqueWork("DailyReminderWork")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}