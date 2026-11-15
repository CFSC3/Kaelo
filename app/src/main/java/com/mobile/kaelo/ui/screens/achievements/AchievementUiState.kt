package com.mobile.kaelo.ui.screens.achievements

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
data class AchievementUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val maxStreak: Int = 0,
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val chartData: List<DailyPerformance> = emptyList(),
    val badges: List<BadgeState> = emptyList(),
    val canGoPreviousMonth: Boolean = false,
    val canGoNextMonth: Boolean = false
)

data class DailyPerformance(
    val dayOfMonth: Int,
    val completionRate: Float,
    val isActive: Boolean
)

data class BadgeState(
    val id: Int,
    val title: String,
    val description: String,
    val isUnlocked: Boolean
)