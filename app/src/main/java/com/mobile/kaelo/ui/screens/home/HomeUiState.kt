package com.mobile.kaelo.ui.screens.home

import com.mobile.kaelo.data.local.entity.HabitEntity
import java.time.LocalDate

data class HomeUiState(
    val isLoading: Boolean = true,
    val habits: List<HabitEntity> = emptyList(),
    val currentWeekDays: List<LocalDate> = emptyList(),
    val currentStreak: Int = 0,
    val completedHabitIds: Set<Long> = emptySet()
)