package com.project.dicodingevent.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore Extension
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DarkModePreferences private constructor(
    private val dataStore: DataStore<Preferences>
) {
    // Preference Keys
    companion object {
        private val THEME_KEY = booleanPreferencesKey("theme_setting")
        private const val DEFAULT_DARK_MODE = false

        @Volatile
        private var INSTANCE: DarkModePreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): DarkModePreferences =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: DarkModePreferences(dataStore).also { INSTANCE = it }
            }
    }

    // Preference Operations
    fun getThemeSetting(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: DEFAULT_DARK_MODE
        }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkModeActive
        }
    }
}