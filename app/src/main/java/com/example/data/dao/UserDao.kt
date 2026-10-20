package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfile)

    @Update
    suspend fun updateProfile(profile: UserProfile)

    @Query("UPDATE user_profile SET totalXp = totalXp + :xpBonus WHERE id = 1")
    suspend fun addXp(xpBonus: Int)

    @Query("UPDATE user_profile SET studySessionsCount = studySessionsCount + 1, totalStudyMinutes = totalStudyMinutes + :minutes, totalXp = totalXp + :xpEarned WHERE id = 1")
    suspend fun recordStudySession(minutes: Int, xpEarned: Int)
}
