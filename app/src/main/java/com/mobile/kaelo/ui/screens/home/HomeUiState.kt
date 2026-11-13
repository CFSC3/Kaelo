package com.mobile.kaelo.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import com.mobile.kaelo.data.local.entity.HabitEntity
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
data class HomeUiState(
    val isLoading: Boolean = true,
    val habits: List<HabitEntity> = emptyList(),
    val currentWeekDays: List<LocalDate> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val currentStreak: Int = 0,
    val completedHabitIds: Set<Long> = emptySet()
)