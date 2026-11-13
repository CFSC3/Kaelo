package com.mobile.kaelo.ui.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.kaelo.data.local.entity.HabitRecordEntity
import com.mobile.kaelo.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        val today = LocalDate.now()
        val weekDays = (0..6).map { today.minusDays(3L).plusDays(it.toLong()) }
        val todayStr = today.toString()

        _uiState.update { it.copy(currentWeekDays = weekDays) }

        viewModelScope.launch {
            // Combine junta a lista de hábitos com a lista de marcações de hoje
            combine(
                repository.getAllActiveHabits(),
                repository.getRecordsForDate(todayStr)
            ) { habitsList, recordsList ->
                // Filtra apenas os IDs dos hábitos que estão marcados como concluídos hoje
                val completedIds = recordsList
                    .filter { it.isCompleted }
                    .map { it.habitId }
                    .toSet()

                _uiState.update {
                    it.copy(
                        habits = habitsList,
                        completedHabitIds = completedIds,
                        isLoading = false
                    )
                }
            }.catch {
                // Tratar erros futuros aqui
            }.collect { }
        }
    }

    fun onToggleHabit(habitId: Long, date: LocalDate, isCompleted: Boolean) {
        viewModelScope.launch {
            val record = HabitRecordEntity(
                habitId = habitId,
                date = date.toString(),
                isCompleted = isCompleted
            )
            repository.upsertHabitRecord(record)
        }
    }
}