package com.example.autoroutine.data.repository

import com.example.autoroutine.data.local.RoutineDao
import com.example.autoroutine.data.local.RoutineEntity
import com.example.autoroutine.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Repository 구현체는 Data Layer에 존재하며 Room DB(Dao)의 세부 명세를 숨김.
class RoutineRepositoryImpl @Inject constructor(
    private val routineDao: RoutineDao
) : RoutineRepository {
    
    override fun getActiveRoutines(): Flow<List<RoutineEntity>> {
        return routineDao.getActiveRoutines()
    }

    override fun getSuggestedRoutines(): Flow<List<RoutineEntity>> {
        return routineDao.getSuggestedRoutines()
    }

    override suspend fun insertRoutine(routine: RoutineEntity) {
        routineDao.insertRoutine(routine)
    }

    override suspend fun updateRoutine(routine: RoutineEntity) {
        routineDao.updateRoutine(routine)
    }
}
