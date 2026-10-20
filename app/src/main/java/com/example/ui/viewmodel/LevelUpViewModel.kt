package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.Achievement
import com.example.data.model.GamificationEngine
import com.example.data.model.Habit
import com.example.data.model.HabitCompletion
import com.example.data.model.Mission
import com.example.data.model.StudySessionLog
import com.example.data.model.UserProfile
import com.example.data.repository.LevelUpRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class UiEvent {
    data class XpAwarded(val xp: Int, val source: String) : UiEvent()
    data class AchievementUnlocked(val title: String, val bonusXp: Int) : UiEvent()
    data class Error(val message: String) : UiEvent()
    data class Success(val message: String) : UiEvent()
}

class LevelUpViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LevelUpRepository(AppDatabase.getInstance(application))

    val userProfile: StateFlow<UserProfile?> = repository.userProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val levelInfo: StateFlow<GamificationEngine.LevelInfo> = userProfile.map { profile ->
        GamificationEngine.calculateLevel(profile?.totalXp ?: 0)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GamificationEngine.calculateLevel(0)
    )

    val allMissions: StateFlow<List<Mission>> = repository.allMissions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allHabits: StateFlow<List<Habit>> = repository.allHabits.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val todayCompletions: StateFlow<List<HabitCompletion>> = repository.getTodayCompletions().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allAchievements: StateFlow<List<Achievement>> = repository.allAchievements.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val weeklyStudyLogs: StateFlow<List<StudySessionLog>> = repository.getWeeklyStudyLogs().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val weeklyCompletions: StateFlow<List<HabitCompletion>> = repository.getWeeklyCompletions().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val totalStudyMinutes: StateFlow<Int> = repository.totalStudyMinutes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val totalStudySessions: StateFlow<Int> = repository.totalStudySessions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    // Study Timer State (25 minutes Pomodoro)
    private val _timerSecondsRemaining = MutableStateFlow(25 * 60)
    val timerSecondsRemaining: StateFlow<Int> = _timerSecondsRemaining.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _activeTimerSubject = MutableStateFlow("Math & Science Focus")
    val activeTimerSubject: StateFlow<String> = _activeTimerSubject.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.initializeDefaultsIfNeeded()
        }
    }

    fun completeWelcome(name: String, school: String, avatar: String, goals: List<String>) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfile()
            val updated = current.copy(
                displayName = name.ifBlank { "Student" },
                gradeOrSchool = school.ifBlank { "High School" },
                avatarEmoji = avatar,
                selectedGoals = goals.joinToString(","),
                isWelcomeCompleted = true
            )
            repository.saveUserProfile(updated)
            triggerHapticFeedback()
            _uiEvents.emit(UiEvent.Success("Welcome to LevelUp, $name! 🚀"))
        }
    }

    fun completeMission(mission: Mission) {
        if (mission.isCompleted) return

        viewModelScope.launch {
            val awarded = repository.completeMission(mission.id)
            if (awarded) {
                triggerHapticFeedback()
                _uiEvents.emit(UiEvent.XpAwarded(mission.xpReward, "Mission Completed: ${mission.title}"))
            } else {
                _uiEvents.emit(UiEvent.Error("Mission already completed! Duplicate XP prevented."))
            }
        }
    }

    fun createMission(
        title: String,
        description: String,
        category: String,
        xpReward: Int,
        isStudySession: Boolean = false,
        durationMinutes: Int = 0
    ) {
        viewModelScope.launch {
            if (title.isBlank()) {
                _uiEvents.emit(UiEvent.Error("Mission title cannot be blank."))
                return@launch
            }
            repository.addMission(title, description, category, xpReward, isStudySession, durationMinutes)
            _uiEvents.emit(UiEvent.Success("Mission added! Earn +$xpReward XP upon completion."))
        }
    }

    fun deleteMission(missionId: Long) {
        viewModelScope.launch {
            repository.deleteMission(missionId)
            _uiEvents.emit(UiEvent.Success("Mission removed."))
        }
    }

    fun completeHabitToday(habit: Habit) {
        viewModelScope.launch {
            val awarded = repository.completeHabitForToday(habit.id)
            if (awarded) {
                triggerHapticFeedback()
                _uiEvents.emit(UiEvent.XpAwarded(habit.xpReward, "Habit Completed: ${habit.title}"))
            } else {
                _uiEvents.emit(UiEvent.Error("Already checked in today!"))
            }
        }
    }

    fun createHabit(title: String, category: String, iconEmoji: String, xpReward: Int) {
        viewModelScope.launch {
            if (title.isBlank()) {
                _uiEvents.emit(UiEvent.Error("Habit title cannot be blank."))
                return@launch
            }
            repository.addHabit(title, category, iconEmoji, xpReward)
            _uiEvents.emit(UiEvent.Success("Habit created! Track daily for streaks."))
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
            _uiEvents.emit(UiEvent.Success("Habit deleted."))
        }
    }

    fun updateProfile(
        name: String,
        school: String,
        avatar: String,
        goals: List<String>,
        soundEnabled: Boolean,
        remindersEnabled: Boolean
    ) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfile()
            repository.saveUserProfile(
                current.copy(
                    displayName = name,
                    gradeOrSchool = school,
                    avatarEmoji = avatar,
                    selectedGoals = goals.joinToString(","),
                    soundEffectsEnabled = soundEnabled,
                    dailyRemindersEnabled = remindersEnabled
                )
            )
            _uiEvents.emit(UiEvent.Success("Settings updated."))
        }
    }

    fun resetData() {
        viewModelScope.launch {
            repository.resetAllProgress()
            _uiEvents.emit(UiEvent.Success("All data reset to initial student defaults."))
        }
    }

    // Study Timer controls
    fun setTimerSubject(subject: String) {
        _activeTimerSubject.value = subject
    }

    fun startTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value && _timerSecondsRemaining.value > 0) {
                delay(1000)
                _timerSecondsRemaining.value -= 1
            }
            if (_timerSecondsRemaining.value == 0) {
                _isTimerRunning.value = false
                finishStudySession()
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer(durationMinutes: Int = 25) {
        pauseTimer()
        _timerSecondsRemaining.value = durationMinutes * 60
    }

    fun finishStudySession() {
        pauseTimer()
        viewModelScope.launch {
            val minutesSpent = 25
            val xpEarned = repository.logCompletedStudySession(_activeTimerSubject.value, minutesSpent)
            triggerHapticFeedback()
            _uiEvents.emit(UiEvent.XpAwarded(xpEarned, "Study Session Finished ($minutesSpent min)!"))
            _timerSecondsRemaining.value = 25 * 60
        }
    }

    private fun triggerHapticFeedback() {
        try {
            val context = getApplication<Application>()
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(100)
                }
            }
        } catch (_: Exception) {
            // Ignore if vibrator not accessible in test or restricted environment
        }
    }
}
