package com.mobile.kaelo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val createdAt: String = LocalDate.now().toString(),
    val isActive: Boolean = true
)