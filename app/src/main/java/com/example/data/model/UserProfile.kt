package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val displayName: String = "Student",
    val gradeOrSchool: String = "High School",
    val avatarEmoji: String = "⚡",
    val selectedGoals: String = "Ace Exams,Daily Reading,Time Management",
    val totalXp: Int = 0,
    val currentStreak: Int = 1,
    val lastActiveDate: String = "",
    val isWelcomeCompleted: Boolean = false,
    val studySessionsCount: Int = 0,
    val totalStudyMinutes: Int = 0,
    val soundEffectsEnabled: Boolean = true,
    val dailyRemindersEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
