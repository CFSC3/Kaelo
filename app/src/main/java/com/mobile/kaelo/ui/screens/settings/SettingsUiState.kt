package com.mobile.kaelo.ui.screens.settings

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val isReminderEnabled: Boolean = false,
    val reminderTimes: Set<String> = emptySet()
)