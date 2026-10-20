package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.AchievementDao
import com.example.data.dao.HabitDao
import com.example.data.dao.MissionDao
import com.example.data.dao.StudyLogDao
import com.example.data.dao.UserDao
import com.example.data.model.Achievement
import com.example.data.model.Habit
import com.example.data.model.HabitCompletion
import com.example.data.model.Mission
import com.example.data.model.StudySessionLog
import com.example.data.model.UserProfile

@Database(
    entities = [
        UserProfile::class,
        Mission::class,
        Habit::class,
        HabitCompletion::class,
        Achievement::class,
        StudySessionLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun missionDao(): MissionDao
    abstract fun habitDao(): HabitDao
    abstract fun achievementDao(): AchievementDao
    abstract fun studyLogDao(): StudyLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "levelup_database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
