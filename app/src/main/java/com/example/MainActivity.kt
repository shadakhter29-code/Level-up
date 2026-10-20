package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.MilitaryTech
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.StudyTimerDialog
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HabitTrackerScreen
import com.example.ui.screens.MissionsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.GoldXP
import com.example.ui.theme.GoldXPLight
import com.example.ui.theme.LevelUpTheme
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Navy950
import com.example.ui.theme.PurpleGlow
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.LevelUpViewModel
import com.example.ui.viewmodel.UiEvent

enum class Screen(val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    DASHBOARD("Home", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    MISSIONS("Missions", Icons.Filled.Assignment, Icons.Outlined.Assignment),
    HABITS("Habits", Icons.Filled.TrackChanges, Icons.Outlined.TrackChanges),
    ACHIEVEMENTS("Badges", Icons.Filled.MilitaryTech, Icons.Outlined.MilitaryTech),
    STATISTICS("Stats", Icons.Filled.BarChart, Icons.Outlined.BarChart),
    SETTINGS("Settings", Icons.Filled.Settings, Icons.Filled.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LevelUpTheme {
                LevelUpApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelUpApp(viewModel: LevelUpViewModel = viewModel()) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val levelInfo by viewModel.levelInfo.collectAsStateWithLifecycle()
    val missions by viewModel.allMissions.collectAsStateWithLifecycle()
    val habits by viewModel.allHabits.collectAsStateWithLifecycle()
    val todayCompletions by viewModel.todayCompletions.collectAsStateWithLifecycle()
    val achievements by viewModel.allAchievements.collectAsStateWithLifecycle()
    val weeklyStudyLogs by viewModel.weeklyStudyLogs.collectAsStateWithLifecycle()
    val weeklyCompletions by viewModel.weeklyCompletions.collectAsStateWithLifecycle()
    val totalStudyMinutes by viewModel.totalStudyMinutes.collectAsStateWithLifecycle()
    val totalStudySessions by viewModel.totalStudySessions.collectAsStateWithLifecycle()

    val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()
    val timerSecondsRemaining by viewModel.timerSecondsRemaining.collectAsStateWithLifecycle()
    val activeTimerSubject by viewModel.activeTimerSubject.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }
    var showStudyTimerDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Collect UI events for celebration snackbars
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is UiEvent.XpAwarded -> {
                    snackbarHostState.showSnackbar("⚡ +${event.xp} XP! ${event.source}")
                }
                is UiEvent.AchievementUnlocked -> {
                    snackbarHostState.showSnackbar("🏆 UNLOCKED: ${event.title}! (+${event.bonusXp} XP)")
                }
                is UiEvent.Success -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is UiEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    // Android System BackHandler: if not on Dashboard, return to Dashboard!
    BackHandler(enabled = currentScreen != Screen.DASHBOARD) {
        currentScreen = Screen.DASHBOARD
    }

    // Onboarding Gate: If user profile is not ready or welcome is not completed, show WelcomeScreen
    if (userProfile != null && !userProfile!!.isWelcomeCompleted) {
        WelcomeScreen(
            initialName = userProfile?.displayName ?: "Alex",
            initialSchool = userProfile?.gradeOrSchool ?: "High School",
            initialAvatar = userProfile?.avatarEmoji ?: "🚀",
            onComplete = { name, school, avatar, goals ->
                viewModel.completeWelcome(name, school, avatar, goals)
            }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Navy950,
        contentWindowInsets = WindowInsets.statusBars,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PurplePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = GoldXPLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "LevelUp",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "Level ${levelInfo.level} Scholar",
                                fontSize = 11.sp,
                                color = GoldXPLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    // Total XP Chip
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldXP.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldXP.copy(alpha = 0.4f)),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = GoldXPLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${levelInfo.totalXp} XP",
                                color = GoldXPLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Settings Icon
                    IconButton(
                        onClick = {
                            currentScreen = if (currentScreen == Screen.SETTINGS) Screen.DASHBOARD else Screen.SETTINGS
                        },
                        modifier = Modifier.testTag("top_bar_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (currentScreen == Screen.SETTINGS) PurplePrimary else TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Navy900,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Navy900,
                contentColor = TextPrimary,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                listOf(
                    Screen.DASHBOARD,
                    Screen.MISSIONS,
                    Screen.HABITS,
                    Screen.ACHIEVEMENTS,
                    Screen.STATISTICS
                ).forEach { screen ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TextPrimary,
                            selectedTextColor = TextPrimary,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = PurplePrimary
                        ),
                        modifier = Modifier.testTag("nav_${screen.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.DASHBOARD -> {
                    DashboardScreen(
                        userProfile = userProfile,
                        levelInfo = levelInfo,
                        missions = missions,
                        habits = habits,
                        todayCompletions = todayCompletions,
                        onCompleteMission = { viewModel.completeMission(it) },
                        onCompleteHabit = { viewModel.completeHabitToday(it) },
                        onStartStudyTimer = { showStudyTimerDialog = true },
                        onNavigateToMissions = { currentScreen = Screen.MISSIONS },
                        onNavigateToHabits = { currentScreen = Screen.HABITS }
                    )
                }
                Screen.MISSIONS -> {
                    MissionsScreen(
                        missions = missions,
                        onCompleteMission = { viewModel.completeMission(it) },
                        onCreateMission = { title, desc, cat, xp, isStudy, mins ->
                            viewModel.createMission(title, desc, cat, xp, isStudy, mins)
                        },
                        onDeleteMission = { viewModel.deleteMission(it) }
                    )
                }
                Screen.HABITS -> {
                    HabitTrackerScreen(
                        habits = habits,
                        todayCompletions = todayCompletions,
                        onCompleteHabit = { viewModel.completeHabitToday(it) },
                        onCreateHabit = { title, cat, emoji, xp ->
                            viewModel.createHabit(title, cat, emoji, xp)
                        },
                        onDeleteHabit = { viewModel.deleteHabit(it) }
                    )
                }
                Screen.ACHIEVEMENTS -> {
                    AchievementsScreen(
                        achievements = achievements
                    )
                }
                Screen.STATISTICS -> {
                    StatisticsScreen(
                        userProfile = userProfile,
                        missions = missions,
                        weeklyStudyLogs = weeklyStudyLogs,
                        weeklyCompletions = weeklyCompletions,
                        totalStudyMinutes = totalStudyMinutes,
                        totalStudySessions = totalStudySessions
                    )
                }
                Screen.SETTINGS -> {
                    SettingsScreen(
                        userProfile = userProfile,
                        onUpdateProfile = { name, school, avatar, goals, sound, reminders ->
                            viewModel.updateProfile(name, school, avatar, goals, sound, reminders)
                        },
                        onResetProgress = { viewModel.resetData() }
                    )
                }
            }
        }
    }

    if (showStudyTimerDialog) {
        StudyTimerDialog(
            isRunning = isTimerRunning,
            remainingSeconds = timerSecondsRemaining,
            subject = activeTimerSubject,
            onStart = { viewModel.startTimer() },
            onPause = { viewModel.pauseTimer() },
            onReset = { viewModel.resetTimer(25) },
            onFinishEarly = {
                viewModel.finishStudySession()
                showStudyTimerDialog = false
            },
            onSubjectChange = { viewModel.setTimerSubject(it) },
            onDismiss = { showStudyTimerDialog = false }
        )
    }
}
