package com.mobile.kaelo.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.kaelo.data.local.datastore.ThemePreferences
import com.mobile.kaelo.data.notifications.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: ThemePreferences,
    private val scheduler: ReminderScheduler
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        prefs.isDarkMode,
        prefs.isReminderEnabled,
        prefs.reminderTimes
    ) { isDark, isReminderEnabled, reminderTimes ->
        SettingsUiState(isDark, isReminderEnabled, reminderTimes)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    init {
        viewModelScope.launch {
            uiState.collect { state ->
                scheduler.scheduleAlarms(state.reminderTimes, state.isReminderEnabled)
            }
        }
    }

    fun toggleTheme(isDark: Boolean) = viewModelScope.launch { prefs.toggleTheme(isDark) }

    fun toggleReminder(enabled: Boolean) = viewModelScope.launch { prefs.updateReminderEnabled(enabled) }

    fun addReminderTime(time: String) = viewModelScope.launch { prefs.addReminderTime(time) }

    fun removeReminderTime(time: String) = viewModelScope.launch {
        scheduler.cancelAlarm(time)
        prefs.removeReminderTime(time)
    }
}