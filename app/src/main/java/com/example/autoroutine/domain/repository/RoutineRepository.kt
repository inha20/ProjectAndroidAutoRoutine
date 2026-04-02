package com.example.autoroutine.domain.repository

import com.example.autoroutine.data.local.RoutineEntity
import kotlinx.coroutines.flow.Flow

// Domain 레이어에 위치하여 OCP 및 DIP(의존성 역전 원칙)를 준수합니다.
interface RoutineRepository {
    fun getActiveRoutines(): Flow<List<RoutineEntity>>
    fun getSuggestedRoutines(): Flow<List<RoutineEntity>>
    suspend fun insertRoutine(routine: RoutineEntity)
    suspend fun updateRoutine(routine: RoutineEntity)
}
