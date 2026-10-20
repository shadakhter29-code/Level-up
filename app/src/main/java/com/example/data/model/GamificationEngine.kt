package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object GamificationEngine {

    data class LevelInfo(
        val level: Int,
        val title: String,
        val totalXp: Int,
        val currentLevelBaseXp: Int,
        val nextLevelTotalXp: Int,
        val xpInCurrentLevel: Int,
        val xpRequiredForNextLevel: Int,
        val progressPercentage: Float
    )

    /**
     * Calculates user level and progress from total XP.
     * Level 1: 0 - 99 XP (100 XP to reach Lv 2)
     * Level 2: 100 - 249 XP (150 XP to reach Lv 3)
     * Level 3: 250 - 449 XP (200 XP to reach Lv 4)
     * Level 4: 450 - 699 XP (250 XP to reach Lv 5)
     * Level N requires (100 + (N - 1) * 50) XP to level up.
     */
    fun calculateLevel(totalXp: Int): LevelInfo {
        val safeXp = totalXp.coerceAtLeast(0)
        var level = 1
        var accumulatedBaseXp = 0
        var xpRequiredForThisLevel = 100

        while (safeXp >= accumulatedBaseXp + xpRequiredForThisLevel) {
            accumulatedBaseXp += xpRequiredForThisLevel
            level++
            xpRequiredForThisLevel = 100 + (level - 1) * 50
        }

        val xpInCurrentLevel = safeXp - accumulatedBaseXp
        val progress = (xpInCurrentLevel.toFloat() / xpRequiredForThisLevel.toFloat()).coerceIn(0f, 1f)

        val title = when (level) {
            1 -> "Novice Scholar"
            2 -> "Apprentice Learner"
            3 -> "Focus Fighter"
            4 -> "Habit Adept"
            5 -> "Study Knight"
            6 -> "Knowledge Seeker"
            7 -> "Mindset Master"
            8 -> "Academic Champion"
            9 -> "Grandmaster Scholar"
            else -> "Legendary Prodigy (Lv. $level)"
        }

        return LevelInfo(
            level = level,
            title = title,
            totalXp = safeXp,
            currentLevelBaseXp = accumulatedBaseXp,
            nextLevelTotalXp = accumulatedBaseXp + xpRequiredForThisLevel,
            xpInCurrentLevel = xpInCurrentLevel,
            xpRequiredForNextLevel = xpRequiredForThisLevel,
            progressPercentage = progress
        )
    }

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getYesterdayDateString(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, -1)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    fun getPastDays(daysCount: Int): List<String> {
        val list = mutableListOf<String>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        for (i in (daysCount - 1) downTo 0) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, -i)
            list.add(sdf.format(calendar.time))
        }
        return list
    }

    fun formatDayName(dateString: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(dateString) ?: return dateString
            val out = SimpleDateFormat("EEE", Locale.getDefault())
            out.format(date)
        } catch (e: Exception) {
            dateString
        }
    }
}
