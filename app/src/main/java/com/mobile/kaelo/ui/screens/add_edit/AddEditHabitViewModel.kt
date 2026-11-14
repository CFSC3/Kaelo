package com.mobile.kaelo.ui.screens.add_edit

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.kaelo.data.local.entity.HabitEntity
import com.mobile.kaelo.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddEditHabitViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditHabitUiState())
    val uiState = _uiState.asStateFlow()

    private var currentHabitId: Long? = null

    fun loadHabit(habitId: Long?) {
        currentHabitId = habitId
        if (habitId == null) {
            _uiState.update { AddEditHabitUiState() }
            return
        }

        viewModelScope.launch {
            repository.getHabitById(habitId)?.let { habit ->
                _uiState.update {
                    it.copy(
                        name = habit.name,
                        selectedIcon = habit.iconName,
                        selectedColor = habit.colorHex,
                        createdAt = habit.createdAt
                    )
                }
            }
        }
    }

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveHabit(onSaveComplete: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.name.isNotBlank()) {
            viewModelScope.launch {

                val creationDate = if (currentState.createdAt.isEmpty()) LocalDate.now().toString() else currentState.createdAt

                val habit = HabitEntity(
                    id = currentHabitId ?: 0,
                    name = currentState.name,
                    iconName = currentState.selectedIcon,
                    colorHex = currentState.selectedColor,
                    createdAt = creationDate
                )
                if (currentHabitId != null) repository.updateHabit(habit) else repository.insertHabit(habit)
                onSaveComplete()
            }
        }
    }

    fun deleteHabit(onDeleteComplete: () -> Unit) {
        currentHabitId?.let { id ->
            viewModelScope.launch {
                repository.getHabitById(id)?.let { habit ->
                    repository.deleteHabit(habit)
                    onDeleteComplete()
                }
            }
        }
    }
}