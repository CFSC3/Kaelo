package com.mobile.kaelo.ui.screens.progress

import com.mobile.kaelo.data.local.entity.HabitEntity
import java.time.LocalDate

data class ProgressUiState(
    val availableDates: List<LocalDate> = listOf(LocalDate.now()),
    val selectedDate: LocalDate = LocalDate.now(),
    val totalHabits: Int = 0,
    val completedHabitsCount: Int = 0,
    val completionRate: Float = 0f,
    val completedHabitsList: List<HabitEntity> = emptyList()
)