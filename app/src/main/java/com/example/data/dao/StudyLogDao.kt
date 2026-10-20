package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.StudySessionLog
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyLogDao {
    @Query("SELECT * FROM study_session_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<StudySessionLog>>

    @Query("SELECT * FROM study_session_logs WHERE dateString IN (:dateList) ORDER BY timestamp DESC")
    fun getLogsForDates(dateList: List<String>): Flow<List<StudySessionLog>>

    @Query("SELECT SUM(durationMinutes) FROM study_session_logs")
    fun getTotalMinutesFlow(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM study_session_logs")
    fun getSessionCountFlow(): Flow<Int>

    @Query("SELECT SUM(durationMinutes) FROM study_session_logs WHERE dateString IN (:dateList)")
    fun getWeeklyMinutesFlow(dateList: List<String>): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: StudySessionLog): Long
}
