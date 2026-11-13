package com.mobile.kaelo.data.repository

import com.mobile.kaelo.data.local.dao.HabitDao
import com.mobile.kaelo.data.local.dao.HabitRecordDao
import com.mobile.kaelo.data.local.entity.HabitEntity
import com.mobile.kaelo.data.local.entity.HabitRecordEntity
import com.mobile.kaelo.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val recordDao: HabitRecordDao
) : HabitRepository {

    override fun getAllRecords() = recordDao.getAllRecords()

    override fun getAllActiveHabits(): Flow<List<HabitEntity>> {
        return habitDao.getAllActiveHabits()
    }

    override suspend fun getHabitById(id: Long): HabitEntity? {
        return habitDao.getHabitById(id)
    }

    override suspend fun insertHabit(habit: HabitEntity): Long {
        return habitDao.insertHabit(habit)
    }

    override suspend fun updateHabit(habit: HabitEntity) {
        habitDao.updateHabit(habit)
    }

    override suspend fun deleteHabit(habit: HabitEntity) {
        habitDao.deleteHabit(habit)
    }

    override fun getRecordsForHabit(habitId: Long): Flow<List<HabitRecordEntity>> {
        return recordDao.getRecordsForHabit(habitId)
    }

    override fun getRecordsForDate(date: String): Flow<List<HabitRecordEntity>> {
        return recordDao.getRecordsForDate(date)
    }

    override suspend fun upsertHabitRecord(record: HabitRecordEntity) {
        recordDao.upsertHabitRecord(record)
    }

}