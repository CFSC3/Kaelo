package com.mobile.kaelo.ui.screens.progress

import com.mobile.kaelo.data.local.entity.HabitEntity

data class ProgressUiState(
    val totalHabits: Int = 0,
    val habitsCompletedToday: Int = 0,
    val completionRateToday: Float = 0f,
    val completedHabitsList: List<HabitEntity> = emptyList()
)