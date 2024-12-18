package com.project.dicodingevent.ui.model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.project.dicodingevent.data.local.datastore.SettingPreferences
import com.project.dicodingevent.util.ReminderWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SettingViewModel(private val pref: SettingPreferences) : ViewModel() {

    private companion object {
        const val REMINDER_WORK_NAME = "DailyReminder"
        const val REMINDER_INTERVAL = 1L
    }

    val themeSettings: LiveData<Boolean> = pref.getThemeSetting().asLiveData()
    val reminderSettings: LiveData<Boolean> = pref.getReminderSetting().asLiveData()

    fun updateThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun updateReminderSetting(isReminderActive: Boolean) {
        viewModelScope.launch {
            pref.saveReminderSetting(isReminderActive)
        }
    }

    fun toggleDailyReminder(context: Context, isEnabled: Boolean) {
        if (isEnabled) {
            scheduleDailyReminder(context)
        } else {
            cancelDailyReminder(context)
        }
    }

    private fun scheduleDailyReminder(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val reminderRequest = createReminderWorkRequest()

        workManager.enqueueUniquePeriodicWork(
            REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            reminderRequest
        )
    }

    private fun createReminderWorkRequest(): PeriodicWorkRequest {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        return PeriodicWorkRequest.Builder(
            ReminderWorker::class.java,
            REMINDER_INTERVAL,
            TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .build()
    }

    private fun cancelDailyReminder(context: Context) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(REMINDER_WORK_NAME)
    }
}