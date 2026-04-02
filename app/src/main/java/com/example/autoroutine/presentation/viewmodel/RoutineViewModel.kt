package com.example.autoroutine.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autoroutine.data.local.RoutineDao
import com.example.autoroutine.data.local.RoutineEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val routineDao: RoutineDao
) : ViewModel() {

    // ViewModel scopes the StateFlow of routines
    val suggestedRoutines: StateFlow<List<RoutineEntity>> = routineDao.getSuggestedRoutines()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val activeRoutines: StateFlow<List<RoutineEntity>> = routineDao.getActiveRoutines()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun acceptRoutine(routine: RoutineEntity) {
        viewModelScope.launch {
            val updated = routine.copy(isUserAccepted = true, isActive = true)
            routineDao.updateRoutine(updated)
        }
    }

    fun rejectRoutine(routine: RoutineEntity) {
        viewModelScope.launch {
            // Either delete or mark as rejected
            // For now, let's just mark it as not active and already reviewed by changing its state or deleting
            val removed = routine.copy(isUserAccepted = true, isActive = false)
            routineDao.updateRoutine(removed)
        }
    }
}
