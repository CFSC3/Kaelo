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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
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

    // Função para viajar no tempo
    fun selectDate(date: LocalDate) {
        val today = LocalDate.now()
        // Bloqueia o futuro! Só permite hoje ou passado.
        if (!date.isAfter(today)) {
            _uiState.update { it.copy(selectedDate = date) }
        }
    }

    private fun loadHomeData() {
        val today = LocalDate.now()
        val weekDays = (0..6).map { today.minusDays(3L).plusDays(it.toLong()) }
        _uiState.update { it.copy(currentWeekDays = weekDays) }

        viewModelScope.launch {
            // Escuta os hábitos, TODOS os registros e a mudança de data simultaneamente
            combine(
                repository.getAllActiveHabits(),
                repository.getAllRecords(),
                _uiState.map { it.selectedDate }.distinctUntilChanged()
            ) { habitsList, allRecords, selectedDate ->

                val selectedDateStr = selectedDate.toString()

                // 1. Marcações da tela atual
                val completedIds = allRecords
                    .filter { it.date == selectedDateStr && it.isCompleted }
                    .map { it.habitId }
                    .toSet()

                // 2. Calcula o Foguinho com as regras de negócio da Luz
                val streak = calculateStreak(allRecords)

                HomeUiState(
                    isLoading = false,
                    habits = habitsList,
                    currentWeekDays = weekDays,
                    selectedDate = selectedDate,
                    currentStreak = streak,
                    completedHabitIds = completedIds
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    private fun calculateStreak(allRecords: List<HabitRecordEntity>): Int {
        // Regra 1: Ignora fraudes (retroativos) e tarefas não concluídas
        val validRecords = allRecords.filter { it.isCompleted && !it.isRetroactive }
        val recordsByDate = validRecords.groupBy { it.date }

        var currentStreak = 0
        var checkDate = LocalDate.now()

        // Regra 2: Verifica se bateu a meta de 2 hábitos HOJE
        val didToday = (recordsByDate[checkDate.toString()]?.size ?: 0) >= 2
        if (didToday) currentStreak++

        // Regra 3: Volta no tempo para somar os dias seguidos anteriores
        checkDate = checkDate.minusDays(1)
        while (true) {
            val count = recordsByDate[checkDate.toString()]?.size ?: 0
            if (count >= 2) {
                currentStreak++
                checkDate = checkDate.minusDays(1)
            } else {
                // Se um dia no passado não bateu a meta, quebra a sequência imediatamente
                break
            }
        }
        return currentStreak
    }

    fun onToggleHabit(habitId: Long, isCompleted: Boolean) {
        val selectedDate = _uiState.value.selectedDate
        // Regra 4: Se o dia selecionado for antes de hoje, é marcado como fraude/retroativo
        val isRetroactive = selectedDate.isBefore(LocalDate.now())

        viewModelScope.launch {
            val record = HabitRecordEntity(
                habitId = habitId,
                date = selectedDate.toString(),
                isCompleted = isCompleted,
                isRetroactive = isRetroactive
            )
            repository.upsertHabitRecord(record)
        }
    }
}