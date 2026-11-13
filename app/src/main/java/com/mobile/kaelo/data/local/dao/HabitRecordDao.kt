package com.mobile.kaelo.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mobile.kaelo.data.local.entity.HabitRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitRecordDao {
    // Busca o histórico de um hábito específico
    @Query("SELECT * FROM habit_records WHERE habitId = :habitId ORDER BY date ASC")
    fun getRecordsForHabit(habitId: Long): Flow<List<HabitRecordEntity>>

    // Busca os registros de todos os hábitos em uma data específica
    @Query("SELECT * FROM habit_records WHERE date = :date")
    fun getRecordsForDate(date: String): Flow<List<HabitRecordEntity>>

    // @Upsert insere o registro se não existir, ou atualiza se já existir (graças ao Index unique)
    @Upsert
    suspend fun upsertHabitRecord(record: HabitRecordEntity)
}