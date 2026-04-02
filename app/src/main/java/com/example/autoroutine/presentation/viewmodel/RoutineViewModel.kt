package com.example.autoroutine.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autoroutine.data.local.RoutineEntity
import com.example.autoroutine.domain.repository.RoutineRepository
import com.example.autoroutine.presentation.state.RoutineUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val repository: RoutineRepository
) : ViewModel() {

    // UDF: 단일 상태 객체 (StateFlow)로 UI 상태 발행
    val uiState: StateFlow<RoutineUiState> = combine(
        repository.getSuggestedRoutines(),
        repository.getActiveRoutines()
    ) { suggested, active ->
        RoutineUiState.Success(
            suggestedRoutines = suggested,
            activeRoutines = active
        )
    }.catch { e ->
        emit(RoutineUiState.Error(e.message ?: "데이터 로딩 중 에러가 발생했습니다."))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // 앱 백그라운드 전환 시 Flow 정지 (가비지 컬렉팅 및 리소스 최적화)
        initialValue = RoutineUiState.Loading
    )

    // 사용자가 제안된 루틴을 수락했을 때의 인텐트
    fun acceptRoutine(routine: RoutineEntity) {
        viewModelScope.launch {
            val updated = routine.copy(isUserAccepted = true, isActive = true)
            repository.updateRoutine(updated)
        }
    }

    // 사용자가 제안된 루틴을 거절했을 때의 인텐트
    fun rejectRoutine(routine: RoutineEntity) {
        viewModelScope.launch {
            val removed = routine.copy(isUserAccepted = true, isActive = false)
            repository.updateRoutine(removed)
        }
    }

    // 사용자가 활성 루틴의 스위치를 끄고 켤 때의 인텐트
    fun toggleRoutine(routine: RoutineEntity, isChecked: Boolean) {
        viewModelScope.launch {
            val toggled = routine.copy(isActive = isChecked)
            repository.updateRoutine(toggled)
        }
    }
}
