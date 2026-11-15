package com.mobile.kaelo.ui.screens.achievements

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.kaelo.data.local.entity.HabitEntity
import com.mobile.kaelo.data.local.entity.HabitRecordEntity
import com.mobile.kaelo.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<AchievementUiState> = combine(
        _selectedMonth,
        repository.getAllActiveHabits(),
        repository.getAllRecords()
    ) { month, habits, records ->
        calculateMonthlyStats(month, habits, records)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AchievementUiState()
    )

    fun previousMonth() = _selectedMonth.update { it.minusMonths(1) }
    fun nextMonth() = _selectedMonth.update { it.plusMonths(1) }

    private fun calculateMonthlyStats(
        month: YearMonth,
        habits: List<HabitEntity>,
        records: List<HabitRecordEntity>
    ): AchievementUiState {
        val today = LocalDate.now()
        val currentYearMonth = YearMonth.now()
        val daysInMonth = month.lengthOfMonth()

        val earliestHabitDate = habits.minOfOrNull { LocalDate.parse(it.createdAt) } ?: today
        val earliestYearMonth = YearMonth.from(earliestHabitDate)

        // Travas de Navegação
        val canGoPrev = month.isAfter(earliestYearMonth)
        val canGoNext = month.isBefore(currentYearMonth)

        val monthPrefix = month.format(DateTimeFormatter.ofPattern("yyyy-MM"))
        val monthlyRecords = records.filter { it.date.startsWith(monthPrefix) }

        var maxStreak = 0
        var currentStreak = 0
        var totalTasks = 0
        var completedTasks = 0
        val chartData = mutableListOf<DailyPerformance>()

        var completedSundays = 0
        var daysWithTwoBefore9am = 0

        for (day in 1..daysInMonth) {
            val dateStr = String.format("%s-%02d", monthPrefix, day)
            val currentDate = month.atDay(day)

            val isFuture = currentDate.isAfter(today)
            val isBeforeCreation = currentDate.isBefore(earliestHabitDate)
            val isActiveDay = !isFuture && !isBeforeCreation

            val dailyRecords = monthlyRecords.filter { it.date == dateStr && it.isCompleted }
            val dailyCompletedCount = dailyRecords.size

            val activeHabitsForDay = habits.count {
                val habitCreation = LocalDate.parse(it.createdAt)
                !currentDate.isBefore(habitCreation)
            }

            if (isActiveDay) {
                totalTasks += activeHabitsForDay
                completedTasks += dailyCompletedCount

                val rate = if (activeHabitsForDay > 0) dailyCompletedCount.toFloat() / activeHabitsForDay else 0f
                chartData.add(DailyPerformance(day, rate, true))

                if (dailyCompletedCount >= 2) {
                    currentStreak++
                    if (currentStreak > maxStreak) maxStreak = currentStreak
                } else {
                    currentStreak = 0
                }

                val before9amCount = dailyRecords.count { record ->
                    val hour = Instant.ofEpochMilli(record.updatedAt).atZone(ZoneId.systemDefault()).hour
                    hour < 9
                }
                if (before9amCount >= 2) daysWithTwoBefore9am++

                if (currentDate.dayOfWeek.value == 7 && dailyCompletedCount == activeHabitsForDay && activeHabitsForDay > 0) {
                    completedSundays++
                }

            } else {
                chartData.add(DailyPerformance(day, 0f, false))
            }
        }

        val completionRate = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f

        val badges = listOf(
            BadgeState(1, "O Primeiro Elo", "Complete a primeiríssima tarefa do mês.", completedTasks >= 1),
            BadgeState(2, "Trinca de Ouro", "Atinja 3 dias de ofensiva no mesmo mês.", maxStreak >= 3),
            BadgeState(3, "Semana Perfeita", "Atinja 7 dias de ofensiva no mesmo mês.", maxStreak >= 7),
            BadgeState(4, "Quinzena Implacável", "Atinja 14 dias de ofensiva no mês.", maxStreak >= 14),
            BadgeState(5, "Tríplice Coroa", "Atinja 21 dias de ofensiva no mês.", maxStreak >= 21),
            BadgeState(6, "O Clube das 9h", "Cumpra pelo menos 2 tarefas antes das 09:00 da manhã no mesmo dia.", daysWithTwoBefore9am >= 1),
            BadgeState(7, "Domingo Inquebrável", "Vença a preguiça e tenha 100% de aproveitamento das metas num domingo.", completedSundays >= 1),
            BadgeState(8, "Foco na Metade", "No placar geral, conclua mais da metade de todas as suas tarefas acumuladas do mês.", completionRate > 0.5f),
            BadgeState(9, "Grão-Mestre", "Lendário! Feche o mês com 100% de aproveitamento nos dias em que usou o app.", completionRate == 1.0f && totalTasks > 0)
        )

        return AchievementUiState(
            month, maxStreak, totalTasks, completedTasks, chartData, badges,
            canGoPreviousMonth = canGoPrev, canGoNextMonth = canGoNext
        )
    }
}