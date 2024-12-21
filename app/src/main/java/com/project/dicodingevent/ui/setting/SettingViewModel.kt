package com.project.dicodingevent.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.project.dicodingevent.data.preferences.DarkModePreferences
import kotlinx.coroutines.launch

class SettingViewModel(
    private val darkModePreferences: DarkModePreferences
) : ViewModel() {
    // Theme Settings
    fun getThemeSettings(): LiveData<Boolean> =
        darkModePreferences.getThemeSetting().asLiveData()

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            darkModePreferences.saveThemeSetting(isDarkModeActive)
        }
    }

    companion object {
        private const val TAG = "SettingViewModel"
    }
}