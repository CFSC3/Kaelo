package com.mobile.kaelo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_records",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE // Se o hábito for apagado, o histórico vai junto
        )
    ],
    indices = [
        Index(value = ["habitId", "date"], unique = true) // Garante apenas 1 registro por dia para cada hábito
    ]
)
data class HabitRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val date: String, // Formato "yyyy-MM-dd"
    val isCompleted: Boolean,
    val updatedAt: Long = System.currentTimeMillis()
)