package com.project.dicodingevent.data.preferences

import android.content.Context
import android.content.SharedPreferences

class ReminderPreferences(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "reminder_pref"
        private const val DAILY_REMINDER = "daily_reminder"
    }

    fun setDailyReminder(isChecked: Boolean) {
        val editor = preferences.edit()
        editor.putBoolean(DAILY_REMINDER, isChecked)
        editor.apply()
    }

    fun getDailyReminder(): Boolean {
        return preferences.getBoolean(DAILY_REMINDER, false)
    }
}
