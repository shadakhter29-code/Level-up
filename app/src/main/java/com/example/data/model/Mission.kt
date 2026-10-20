package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "missions")
data class Mission(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String, // "Study", "Reading", "Exercise", "Personal Growth"
    val xpReward: Int,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val isStudySession: Boolean = false,
    val durationMinutes: Int = 0,
    val dueDateString: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
