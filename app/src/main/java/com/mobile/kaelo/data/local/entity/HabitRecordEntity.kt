package com.mobile.kaelo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "habit_records",
    primaryKeys = ["habitId", "date"],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HabitRecordEntity(
    val habitId: Long,
    val date: String,
    val isCompleted: Boolean,
    val isRetroactive: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)