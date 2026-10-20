package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GamificationEngine
import com.example.data.model.HabitCompletion
import com.example.data.model.Mission
import com.example.data.model.StudySessionLog
import com.example.data.model.UserProfile
import com.example.ui.theme.CategoryExercise
import com.example.ui.theme.CategoryGrowth
import com.example.ui.theme.CategoryReading
import com.example.ui.theme.CategoryStudy
import com.example.ui.theme.GoldXP
import com.example.ui.theme.GoldXPLight
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Navy950
import com.example.ui.theme.PurpleContainer
import com.example.ui.theme.PurpleGlow
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun StatisticsScreen(
    userProfile: UserProfile?,
    missions: List<Mission>,
    weeklyStudyLogs: List<StudySessionLog>,
    weeklyCompletions: List<HabitCompletion>,
    totalStudyMinutes: Int,
    totalStudySessions: Int,
    modifier: Modifier = Modifier
) {
    val completedMissions = missions.filter { it.isCompleted }
    val totalMissionsCount = completedMissions.size

    val past7Days = GamificationEngine.getPastDays(7)
    val studyLogsByDay = weeklyStudyLogs.groupBy { it.dateString }
    val habitCompletionsByDay = weeklyCompletions.groupBy { it.dateString }

    // Category distribution from completed missions
    val studyCount = completedMissions.count { it.category == "Study" }
    val readingCount = completedMissions.count { it.category == "Reading" }
    val exerciseCount = completedMissions.count { it.category == "Exercise" }
    val growthCount = completedMissions.count { it.category == "Personal Growth" }
    val totalCategoryCount = (studyCount + readingCount + exerciseCount + growthCount).coerceAtLeast(1)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Navy950)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Progress & Statistics",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = "Track your study sessions, habits, and weekly achievements",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        // Summary Metric Cards (2x2 Grid)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Total XP",
                        value = "${userProfile?.totalXp ?: 0}",
                        subtitle = "Earned to date",
                        icon = Icons.Default.Bolt,
                        accentColor = GoldXP,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Study Time",
                        value = "${totalStudyMinutes}m",
                        subtitle = "$totalStudySessions sessions logged",
                        icon = Icons.Default.Timer,
                        accentColor = CategoryStudy,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        title = "Completed Missions",
                        value = "$totalMissionsCount",
                        subtitle = "Tasks checked off",
                        icon = Icons.Default.CheckCircle,
                        accentColor = PurplePrimary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Current Streak",
                        value = "${userProfile?.currentStreak ?: 1} Days",
                        subtitle = "Daily consistency",
                        icon = Icons.Default.HourglassTop,
                        accentColor = GoldXPLight,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Weekly Study Activity Bar Chart
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Navy900),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Navy700),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weekly_chart_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Weekly Focus Minutes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Study session minutes logged over the past 7 days",
                        fontSize = 12.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 7-day Bar Chart
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        past7Days.forEach { dateStr ->
                            val minutesLogged = studyLogsByDay[dateStr]?.sumOf { it.durationMinutes } ?: 0
                            val maxMinutes = 100f
                            val heightFraction = (minutesLogged.toFloat() / maxMinutes).coerceIn(0.08f, 1f)

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (minutesLogged > 0) {
                                    Text(
                                        text = "${minutesLogged}m",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldXPLight
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }

                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .fillMaxHeight(heightFraction)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(
                                            if (minutesLogged > 0) {
                                                Brush.verticalGradient(listOf(PurplePrimary, PurpleContainer))
                                            } else {
                                                Brush.verticalGradient(listOf(Navy800, Navy950))
                                            }
                                        )
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = GamificationEngine.formatDayName(dateStr),
                                    fontSize = 11.sp,
                                    color = if (minutesLogged > 0) TextPrimary else TextMuted,
                                    fontWeight = if (minutesLogged > 0) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // Category Breakdown
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Navy900),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Navy700),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Focus by Category",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Distribution of completed student tasks",
                        fontSize = 12.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CategoryProgressBar(
                        title = "Study & Focus Sessions",
                        count = studyCount,
                        total = totalCategoryCount,
                        color = CategoryStudy
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CategoryProgressBar(
                        title = "Reading & Literature",
                        count = readingCount,
                        total = totalCategoryCount,
                        color = CategoryReading
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CategoryProgressBar(
                        title = "Exercise & Health",
                        count = exerciseCount,
                        total = totalCategoryCount,
                        color = CategoryExercise
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CategoryProgressBar(
                        title = "Personal Growth & Routine",
                        count = growthCount,
                        total = totalCategoryCount,
                        color = CategoryGrowth
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Navy900),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Navy700),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
fun CategoryProgressBar(
    title: String,
    count: Int,
    total: Int,
    color: Color
) {
    val fraction = (count.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    val percentage = (fraction * 100).toInt()

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = "$count tasks ($percentage%)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Navy800
        )
    }
}
