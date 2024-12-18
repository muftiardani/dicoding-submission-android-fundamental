package com.project.dicodingevent.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    companion object {
        private const val PREFERENCES_NAME = "settings"

        private val THEME_KEY = booleanPreferencesKey("theme_setting")
        private val REMINDER_KEY = booleanPreferencesKey("daily_reminder_setting")

        @Volatile
        private var INSTANCE: SettingPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingPreferences(dataStore).also { INSTANCE = it }
            }
    }

    // Theme Settings
    fun getThemeSetting(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: false
        }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkModeActive
        }
    }

    // Reminder Settings
    fun getReminderSetting(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[REMINDER_KEY] ?: false
        }

    suspend fun saveReminderSetting(isReminderActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[REMINDER_KEY] = isReminderActive
        }
    }
}