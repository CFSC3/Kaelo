package com.mobile.kaelo.domain.repository

import com.mobile.kaelo.data.local.entity.HabitEntity
import com.mobile.kaelo.data.local.entity.HabitRecordEntity
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getAllActiveHabits(): Flow<List<HabitEntity>>
    suspend fun getHabitById(id: Long): HabitEntity?
    suspend fun insertHabit(habit: HabitEntity): Long
    suspend fun updateHabit(habit: HabitEntity)
    suspend fun deleteHabit(habit: HabitEntity)

    fun getRecordsForHabit(habitId: Long): Flow<List<HabitRecordEntity>>
    fun getRecordsForDate(date: String): Flow<List<HabitRecordEntity>>
    suspend fun upsertHabitRecord(record: HabitRecordEntity)
}