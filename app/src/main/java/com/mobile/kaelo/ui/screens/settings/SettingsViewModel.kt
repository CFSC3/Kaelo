package com.mobile.kaelo.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.kaelo.data.local.datastore.ThemePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themePreferences: ThemePreferences
) : ViewModel() {

    val isDarkMode = themePreferences.isDarkMode

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            themePreferences.toggleTheme(isDark)
        }
    }
}