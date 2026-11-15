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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
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

    // Função que responde ao menu de seleção de data na tela
    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
    }

    private fun loadProgressData() {
        viewModelScope.launch {
            // Fica escutando as três fontes simultaneamente
            combine(
                repository.getAllActiveHabits(),
                repository.getAllRecords(),
                _uiState.map { it.selectedDate }.distinctUntilChanged()
            ) { habitsList, allRecords, selectedDate ->

                val today = LocalDate.now()
                val selectedDateStr = selectedDate.toString()

                // Constrói o menu de datas (Do primeiro hábito criado até hoje)
                val earliestDateStr = habitsList.minOfOrNull { it.createdAt } ?: today.toString()
                val earliestDate = LocalDate.parse(earliestDateStr)

                val availableDates = mutableListOf<LocalDate>()
                var current = today
                while (!current.isBefore(earliestDate)) {
                    availableDates.add(current)
                    current = current.minusDays(1)
                }
                if (availableDates.isEmpty()) availableDates.add(today)

                // Filtra hábitos (Remove os que ainda não tinham nascido no dia selecionado)
                val visibleHabits = habitsList.filter { it.createdAt <= selectedDateStr }
                val total = visibleHabits.size

                // O MOTOR ANTI-FRAUDE: Filtra registros do dia excluindo marcações retroativas
                val validRecordsForDate = allRecords.filter {
                    it.date == selectedDateStr && it.isCompleted && !it.isRetroactive
                }

                val completedCount = validRecordsForDate.size
                val rate = if (total > 0) completedCount.toFloat() / total.toFloat() else 0f

                // Mapeia apenas os hábitos validados sem atraso
                val completedIds = validRecordsForDate.map { it.habitId }.toSet()
                val completedHabits = visibleHabits.filter { it.id in completedIds }

                ProgressUiState(
                    availableDates = availableDates,
                    selectedDate = selectedDate,
                    totalHabits = total,
                    completedHabitsCount = completedCount,
                    completionRate = rate,
                    completedHabitsList = completedHabits
                )
            }.catch {
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
}