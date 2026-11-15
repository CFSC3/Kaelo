package com.mobile.kaelo.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class ThemePreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        val REMINDER_TIMES = stringSetPreferencesKey("reminder_times")
    }

    // Fluxo do Tema
    val isDarkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_DARK_MODE] ?: false
    }

    // Fluxo do Status do Lembrete (Ligado/Desligado)
    val isReminderEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DAILY_REMINDER_ENABLED] ?: false
    }

    // Fluxo da Lista de Horários
    val reminderTimes: Flow<Set<String>> = dataStore.data.map { preferences ->
        preferences[REMINDER_TIMES] ?: emptySet()
    }

    suspend fun toggleTheme(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDark
        }
    }

    suspend fun updateReminderEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DAILY_REMINDER_ENABLED] = enabled
        }
    }

    suspend fun addReminderTime(time: String) {
        dataStore.edit { preferences ->
            val currentTimes = preferences[REMINDER_TIMES] ?: emptySet()
            preferences[REMINDER_TIMES] = currentTimes + time
        }
    }

    suspend fun removeReminderTime(time: String) {
        dataStore.edit { preferences ->
            val currentTimes = preferences[REMINDER_TIMES] ?: emptySet()
            preferences[REMINDER_TIMES] = currentTimes - time
        }
    }
}