package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Habit
import com.example.data.model.HabitCompletion
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY createdAt ASC")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :habitId LIMIT 1")
    suspend fun getHabitById(habitId: Long): Habit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabitById(habitId: Long)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId")
    suspend fun deleteHabitCompletionsByHabitId(habitId: Long)

    // Completions
    @Query("SELECT * FROM habit_completions WHERE dateString = :dateString")
    fun getCompletionsForDate(dateString: String): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completions WHERE dateString = :dateString")
    suspend fun getCompletionsForDateOnce(dateString: String): List<HabitCompletion>

    @Query("SELECT * FROM habit_completions WHERE dateString IN (:dateList)")
    fun getCompletionsForDates(dateList: List<String>): Flow<List<HabitCompletion>>

    @Query("SELECT COUNT(*) FROM habit_completions WHERE habitId = :habitId AND dateString = :dateString")
    suspend fun isHabitCompletedOnDate(habitId: Long, dateString: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompletion(completion: HabitCompletion): Long

    @Query("SELECT COUNT(*) FROM habit_completions")
    fun getTotalCompletionsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM habit_completions")
    suspend fun getTotalCompletionsCountOnce(): Int
}
