package com.example.autoroutine.di

import android.content.Context
import androidx.room.Room
import com.example.autoroutine.data.local.AppDatabase
import com.example.autoroutine.data.local.RoutineDao
import com.example.autoroutine.data.repository.RoutineRepositoryImpl
import com.example.autoroutine.domain.repository.RoutineRepository
import com.example.autoroutine.domain.usecase.SuggestRoutineUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "autoroutine_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideRoutineDao(db: AppDatabase): RoutineDao = db.routineDao()

    @Provides
    @Singleton
    fun provideRoutineRepository(impl: RoutineRepositoryImpl): RoutineRepository = impl

    @Provides
    @Singleton
    fun provideSuggestRoutineUseCase(): SuggestRoutineUseCase {
        return SuggestRoutineUseCase()
    }
}
