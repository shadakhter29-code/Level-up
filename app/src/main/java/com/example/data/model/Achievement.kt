package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String, // "Missions", "Study", "Streak", "Habits", "Levels"
    val iconEmoji: String,
    val requiredProgress: Int,
    val currentProgress: Int = 0,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val xpBonus: Int = 50
)
