package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.Achievement
import com.example.data.model.GamificationEngine
import com.example.data.model.Habit
import com.example.data.model.HabitCompletion
import com.example.data.model.Mission
import com.example.data.model.StudySessionLog
import com.example.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LevelUpRepository(private val database: AppDatabase) {

    private val userDao = database.userDao()
    private val missionDao = database.missionDao()
    private val habitDao = database.habitDao()
    private val achievementDao = database.achievementDao()
    private val studyLogDao = database.studyLogDao()

    val userProfile: Flow<UserProfile?> = userDao.getUserProfile()
    val allMissions: Flow<List<Mission>> = missionDao.getAllMissions()
    val activeMissions: Flow<List<Mission>> = missionDao.getActiveMissions()
    val completedMissions: Flow<List<Mission>> = missionDao.getCompletedMissions()
    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()
    val allAchievements: Flow<List<Achievement>> = achievementDao.getAllAchievements()
    val unlockedAchievementsCount: Flow<Int> = achievementDao.getUnlockedCount()
    val studyLogs: Flow<List<StudySessionLog>> = studyLogDao.getAllLogs()
    val totalStudyMinutes: Flow<Int> = studyLogDao.getTotalMinutesFlow().map { it ?: 0 }
    val totalStudySessions: Flow<Int> = studyLogDao.getSessionCountFlow()

    fun getTodayCompletions(): Flow<List<HabitCompletion>> {
        val today = GamificationEngine.getTodayDateString()
        return habitDao.getCompletionsForDate(today)
    }

    fun getWeeklyStudyLogs(): Flow<List<StudySessionLog>> {
        val past7Days = GamificationEngine.getPastDays(7)
        return studyLogDao.getLogsForDates(past7Days)
    }

    fun getWeeklyCompletions(): Flow<List<HabitCompletion>> {
        val past7Days = GamificationEngine.getPastDays(7)
        return habitDao.getCompletionsForDates(past7Days)
    }

    suspend fun initializeDefaultsIfNeeded() = withContext(Dispatchers.IO) {
        // Initialize UserProfile
        val existingProfile = userDao.getUserProfileOnce()
        val today = GamificationEngine.getTodayDateString()
        val yesterday = GamificationEngine.getYesterdayDateString()

        if (existingProfile == null) {
            val initialProfile = UserProfile(
                id = 1,
                displayName = "Alex",
                gradeOrSchool = "Grade 10",
                avatarEmoji = "🚀",
                selectedGoals = "Ace Exams,Daily Reading,Time Management,Consistent Routine",
                totalXp = 0,
                currentStreak = 1,
                lastActiveDate = today,
                isWelcomeCompleted = false
            )
            userDao.insertOrUpdateProfile(initialProfile)

            // Seed initial missions
            val defaultMissions = listOf(
                Mission(
                    title = "Complete 25-min Math Study Session",
                    description = "Focus on algebra equations without phone distractions.",
                    category = "Study",
                    xpReward = 25,
                    isStudySession = true,
                    durationMinutes = 25
                ),
                Mission(
                    title = "Read 15 Pages of Literature",
                    description = "Chapter 4 of class novel.",
                    category = "Reading",
                    xpReward = 10,
                    isStudySession = false
                ),
                Mission(
                    title = "Afternoon 20-min Quick Walk / Jog",
                    description = "Get fresh air and boost afternoon focus.",
                    category = "Exercise",
                    xpReward = 20,
                    isStudySession = false
                ),
                Mission(
                    title = "Organize Study Desk & Backpack",
                    description = "Clear clutter for maximum mental clarity.",
                    category = "Personal Growth",
                    xpReward = 15,
                    isStudySession = false
                )
            )
            defaultMissions.forEach { missionDao.insertMission(it) }

            // Seed initial habits
            val defaultHabits = listOf(
                Habit(
                    title = "Daily 25m Focus Block",
                    category = "Study",
                    iconEmoji = "⏱️",
                    xpReward = 25,
                    streakCount = 2,
                    bestStreak = 2,
                    lastCompletedDate = yesterday
                ),
                Habit(
                    title = "Read 20 Mins Before Bed",
                    category = "Reading",
                    iconEmoji = "📖",
                    xpReward = 10,
                    streakCount = 1,
                    bestStreak = 3,
                    lastCompletedDate = yesterday
                ),
                Habit(
                    title = "Drink 2L Water & Stretch",
                    category = "Exercise",
                    iconEmoji = "💧",
                    xpReward = 15,
                    streakCount = 0,
                    bestStreak = 1,
                    lastCompletedDate = null
                ),
                Habit(
                    title = "Evening Reflection & Gratitude",
                    category = "Personal Growth",
                    iconEmoji = "✨",
                    xpReward = 15,
                    streakCount = 1,
                    bestStreak = 1,
                    lastCompletedDate = yesterday
                )
            )
            defaultHabits.forEach { habitDao.insertHabit(it) }
        } else {
            // Update streak if needed
            val lastActive = existingProfile.lastActiveDate
            if (lastActive != today && lastActive != yesterday && lastActive.isNotEmpty()) {
                // Streak broken
                userDao.updateProfile(existingProfile.copy(currentStreak = 1, lastActiveDate = today))
            } else if (lastActive == yesterday) {
                // Consecutive active day
                userDao.updateProfile(existingProfile.copy(currentStreak = existingProfile.currentStreak + 1, lastActiveDate = today))
            } else if (lastActive.isEmpty()) {
                userDao.updateProfile(existingProfile.copy(lastActiveDate = today))
            }
        }

        // Initialize Achievements catalog
        val defaultAchievements = listOf(
            Achievement(
                id = "first_mission",
                title = "First Step",
                description = "Complete your first mission",
                category = "Missions",
                iconEmoji = "🎯",
                requiredProgress = 1,
                xpBonus = 30
            ),
            Achievement(
                id = "mission_5",
                title = "Mission Cadet",
                description = "Complete 5 missions",
                category = "Missions",
                iconEmoji = "🥉",
                requiredProgress = 5,
                xpBonus = 50
            ),
            Achievement(
                id = "mission_15",
                title = "Mission Master",
                description = "Complete 15 missions",
                category = "Missions",
                iconEmoji = "🥇",
                requiredProgress = 15,
                xpBonus = 100
            ),
            Achievement(
                id = "study_first",
                title = "Deep Focus",
                description = "Log your first 25-minute study session",
                category = "Study",
                iconEmoji = "🧠",
                requiredProgress = 1,
                xpBonus = 40
            ),
            Achievement(
                id = "study_5_sessions",
                title = "Study Knight",
                description = "Complete 5 focused study sessions",
                category = "Study",
                iconEmoji = "⚔️",
                requiredProgress = 5,
                xpBonus = 75
            ),
            Achievement(
                id = "study_100_mins",
                title = "Century Scholar",
                description = "Log over 100 minutes of focused study",
                category = "Study",
                iconEmoji = "⏳",
                requiredProgress = 100,
                xpBonus = 100
            ),
            Achievement(
                id = "habit_first",
                title = "Habit Builder",
                description = "Complete your first daily habit",
                category = "Habits",
                iconEmoji = "🌱",
                requiredProgress = 1,
                xpBonus = 25
            ),
            Achievement(
                id = "habit_streak_3",
                title = "Triple Threat",
                description = "Maintain a 3-day habit streak",
                category = "Streak",
                iconEmoji = "🔥",
                requiredProgress = 3,
                xpBonus = 60
            ),
            Achievement(
                id = "habit_streak_7",
                title = "Unstoppable Momentum",
                description = "Achieve a 7-day habit streak",
                category = "Streak",
                iconEmoji = "⚡",
                requiredProgress = 7,
                xpBonus = 150
            ),
            Achievement(
                id = "level_2",
                title = "Level Up!",
                description = "Reach Student Level 2",
                category = "Levels",
                iconEmoji = "⭐",
                requiredProgress = 100,
                xpBonus = 50
            ),
            Achievement(
                id = "level_5",
                title = "Academic Elite",
                description = "Reach Student Level 5 (700+ XP)",
                category = "Levels",
                iconEmoji = "👑",
                requiredProgress = 700,
                xpBonus = 200
            )
        )
        achievementDao.insertInitialAchievements(defaultAchievements)
    }

    suspend fun saveUserProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        userDao.insertOrUpdateProfile(profile)
    }

    suspend fun completeMission(missionId: Long): Boolean = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val updatedRows = missionDao.completeMissionAtomically(missionId, now)
        if (updatedRows > 0) {
            // Atomic update succeeded - award XP strictly once
            val mission = missionDao.getMissionById(missionId) ?: return@withContext false
            userDao.addXp(mission.xpReward)

            // If it was a study session, record the study minutes too
            if (mission.isStudySession && mission.durationMinutes > 0) {
                studyLogDao.insertLog(
                    StudySessionLog(
                        subject = mission.title,
                        durationMinutes = mission.durationMinutes,
                        xpEarned = mission.xpReward,
                        dateString = GamificationEngine.getTodayDateString(),
                        timestamp = now
                    )
                )
            }

            evaluateAchievements()
            return@withContext true
        }
        return@withContext false
    }

    suspend fun addMission(
        title: String,
        description: String,
        category: String,
        xpReward: Int,
        isStudySession: Boolean = false,
        durationMinutes: Int = 0
    ): Long = withContext(Dispatchers.IO) {
        val mission = Mission(
            title = title.trim(),
            description = description.trim(),
            category = category,
            xpReward = xpReward,
            isStudySession = isStudySession,
            durationMinutes = durationMinutes
        )
        missionDao.insertMission(mission)
    }

    suspend fun updateMission(mission: Mission) = withContext(Dispatchers.IO) {
        missionDao.updateMission(mission)
    }

    suspend fun deleteMission(missionId: Long) = withContext(Dispatchers.IO) {
        missionDao.deleteMissionById(missionId)
    }

    suspend fun completeHabitForToday(habitId: Long): Boolean = withContext(Dispatchers.IO) {
        val today = GamificationEngine.getTodayDateString()
        val yesterday = GamificationEngine.getYesterdayDateString()

        // Check if already completed today - PREVENT DUPLICATE XP
        val isAlreadyDone = habitDao.isHabitCompletedOnDate(habitId, today)
        if (isAlreadyDone > 0) {
            return@withContext false
        }

        val habit = habitDao.getHabitById(habitId) ?: return@withContext false

        // Insert completion record
        val completion = HabitCompletion(
            habitId = habitId,
            dateString = today,
            xpEarned = habit.xpReward,
            completedAt = System.currentTimeMillis()
        )
        val insertResult = habitDao.insertCompletion(completion)
        if (insertResult <= 0) {
            return@withContext false
        }

        // Calculate new streak
        val newStreak = if (habit.lastCompletedDate == yesterday) {
            habit.streakCount + 1
        } else {
            1
        }
        val best = maxOf(habit.bestStreak, newStreak)

        habitDao.updateHabit(
            habit.copy(
                streakCount = newStreak,
                bestStreak = best,
                lastCompletedDate = today
            )
        )

        // Award XP to user
        userDao.addXp(habit.xpReward)

        evaluateAchievements()
        return@withContext true
    }

    suspend fun addHabit(
        title: String,
        category: String,
        iconEmoji: String,
        xpReward: Int
    ): Long = withContext(Dispatchers.IO) {
        val habit = Habit(
            title = title.trim(),
            category = category,
            iconEmoji = iconEmoji,
            xpReward = xpReward
        )
        habitDao.insertHabit(habit)
    }

    suspend fun deleteHabit(habitId: Long) = withContext(Dispatchers.IO) {
        habitDao.deleteHabitCompletionsByHabitId(habitId)
        habitDao.deleteHabitById(habitId)
    }

    suspend fun logCompletedStudySession(subject: String, durationMinutes: Int = 25): Int = withContext(Dispatchers.IO) {
        // Award 25 XP for 25-minute session
        val xpEarned = if (durationMinutes >= 25) 25 else (durationMinutes * 25 / 25).coerceAtLeast(10)
        val today = GamificationEngine.getTodayDateString()

        studyLogDao.insertLog(
            StudySessionLog(
                subject = subject.ifBlank { "Deep Focus Session" },
                durationMinutes = durationMinutes,
                xpEarned = xpEarned,
                dateString = today
            )
        )

        userDao.recordStudySession(durationMinutes, xpEarned)
        evaluateAchievements()
        return@withContext xpEarned
    }

    private suspend fun evaluateAchievements() {
        val profile = userDao.getUserProfileOnce() ?: return
        val completedMissionsCount = missionDao.getCompletedMissionsCountOnce()
        val habitCompletionsCount = habitDao.getTotalCompletionsCountOnce()
        val now = System.currentTimeMillis()

        // 1. First mission
        checkAndUnlock("first_mission", completedMissionsCount, now)
        checkAndUnlock("mission_5", completedMissionsCount, now)
        checkAndUnlock("mission_15", completedMissionsCount, now)

        // 2. Study sessions
        checkAndUnlock("study_first", profile.studySessionsCount, now)
        checkAndUnlock("study_5_sessions", profile.studySessionsCount, now)
        checkAndUnlock("study_100_mins", profile.totalStudyMinutes, now)

        // 3. Habits
        checkAndUnlock("habit_first", habitCompletionsCount, now)
        checkAndUnlock("habit_streak_3", profile.currentStreak, now)
        checkAndUnlock("habit_streak_7", profile.currentStreak, now)

        // 4. Levels (using total XP)
        checkAndUnlock("level_2", profile.totalXp, now)
        checkAndUnlock("level_5", profile.totalXp, now)
    }

    private suspend fun checkAndUnlock(achievementId: String, currentVal: Int, timestamp: Long) {
        val achievement = achievementDao.getAchievementById(achievementId) ?: return
        if (achievement.isUnlocked) return

        achievementDao.updateProgress(achievementId, currentVal)
        if (currentVal >= achievement.requiredProgress) {
            val unlockedRows = achievementDao.unlockAchievement(achievementId, timestamp)
            if (unlockedRows > 0) {
                // Award bonus XP for unlocking achievement!
                userDao.addXp(achievement.xpBonus)
            }
        }
    }

    suspend fun resetAllProgress() = withContext(Dispatchers.IO) {
        database.clearAllTables()
        initializeDefaultsIfNeeded()
    }
}
