package com.mobile.kaelo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mobile.kaelo.data.local.dao.HabitDao
import com.mobile.kaelo.data.local.dao.HabitRecordDao
import com.mobile.kaelo.data.local.entity.HabitEntity
import com.mobile.kaelo.data.local.entity.HabitRecordEntity

@Database(
    entities = [HabitEntity::class, HabitRecordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitRecordDao(): HabitRecordDao
}