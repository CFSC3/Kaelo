package com.mobile.kaelo.ui.screens.progress

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.kaelo.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProgressData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadProgressData() {
        val todayStr = LocalDate.now().toString()

        viewModelScope.launch {
            combine(
                repository.getAllActiveHabits(),
                repository.getRecordsForDate(todayStr)
            ) { habitsList, recordsList ->

                val total = habitsList.size

                // Filtra os registros que estão concluídos hoje
                val completedRecords = recordsList.filter { it.isCompleted }
                val completedCount = completedRecords.size
                val rate = if (total > 0) completedCount.toFloat() / total.toFloat() else 0f

                // Descobre QUAIS hábitos foram concluídos cruzando os IDs
                val completedIds = completedRecords.map { it.habitId }.toSet()
                val completedHabits = habitsList.filter { it.id in completedIds }

                ProgressUiState(
                    totalHabits = total,
                    habitsCompletedToday = completedCount,
                    completionRateToday = rate,
                    completedHabitsList = completedHabits
                )
            }.catch {
            }.collect { newState ->
                _uiState.update { newState }
            }
        }
    }
}