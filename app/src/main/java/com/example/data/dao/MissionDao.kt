package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Mission
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionDao {
    @Query("SELECT * FROM missions ORDER BY isCompleted ASC, createdAt DESC")
    fun getAllMissions(): Flow<List<Mission>>

    @Query("SELECT * FROM missions WHERE isCompleted = 0 ORDER BY createdAt DESC")
    fun getActiveMissions(): Flow<List<Mission>>

    @Query("SELECT * FROM missions WHERE isCompleted = 1 ORDER BY completedAt DESC")
    fun getCompletedMissions(): Flow<List<Mission>>

    @Query("SELECT * FROM missions WHERE category = :category ORDER BY isCompleted ASC, createdAt DESC")
    fun getMissionsByCategory(category: String): Flow<List<Mission>>

    @Query("SELECT * FROM missions WHERE id = :missionId LIMIT 1")
    suspend fun getMissionById(missionId: Long): Mission?

    @Query("SELECT COUNT(*) FROM missions WHERE isCompleted = 1")
    fun getCompletedMissionsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM missions WHERE isCompleted = 1")
    suspend fun getCompletedMissionsCountOnce(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: Mission): Long

    @Update
    suspend fun updateMission(mission: Mission)

    @Delete
    suspend fun deleteMission(mission: Mission)

    @Query("DELETE FROM missions WHERE id = :missionId")
    suspend fun deleteMissionById(missionId: Long)

    @Query("UPDATE missions SET isCompleted = 1, completedAt = :completedAt WHERE id = :missionId AND isCompleted = 0")
    suspend fun completeMissionAtomically(missionId: Long, completedAt: Long): Int
}
