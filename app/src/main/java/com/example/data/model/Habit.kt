package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // "Study", "Reading", "Exercise", "Personal Growth"
    val iconEmoji: String = "🎯",
    val xpReward: Int = 15,
    val streakCount: Int = 0,
    val bestStreak: Int = 0,
    val lastCompletedDate: String? = null, // "yyyy-MM-dd"
    val createdAt: Long = System.currentTimeMillis()
)
