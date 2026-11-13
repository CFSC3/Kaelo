package com.mobile.kaelo.ui.screens.add_edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.kaelo.data.local.entity.HabitEntity
import com.mobile.kaelo.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditHabitViewModel @Inject constructor(
    private val repository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditHabitUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun saveHabit(onSaveComplete: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.name.isNotBlank()) {
            viewModelScope.launch {
                repository.insertHabit(
                    HabitEntity(
                        name = currentState.name,
                        iconName = currentState.selectedIcon,
                        colorHex = currentState.selectedColor
                    )
                )
                // Após salvar no banco, avisa a tela para fechar e voltar
                onSaveComplete()
            }
        }
    }
}