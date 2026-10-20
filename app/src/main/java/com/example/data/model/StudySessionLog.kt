package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_session_logs")
data class StudySessionLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val durationMinutes: Int,
    val xpEarned: Int,
    val dateString: String, // "yyyy-MM-dd"
    val timestamp: Long = System.currentTimeMillis()
)
