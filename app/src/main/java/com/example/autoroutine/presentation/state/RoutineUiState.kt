package com.example.autoroutine.presentation.state

import com.example.autoroutine.data.local.RoutineEntity

sealed interface RoutineUiState {
    object Loading : RoutineUiState
    data class Success(
        val suggestedRoutines: List<RoutineEntity>,
        val activeRoutines: List<RoutineEntity>
    ) : RoutineUiState
    data class Error(val message: String) : RoutineUiState
}
