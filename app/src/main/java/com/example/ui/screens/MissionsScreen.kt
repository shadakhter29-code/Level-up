package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Mission
import com.example.ui.components.CategoryBadge
import com.example.ui.components.EmptyPlaceholder
import com.example.ui.components.XpRewardTag
import com.example.ui.theme.CategoryExercise
import com.example.ui.theme.CategoryGrowth
import com.example.ui.theme.CategoryReading
import com.example.ui.theme.CategoryStudy
import com.example.ui.theme.GoldXP
import com.example.ui.theme.GoldXPLight
import com.example.ui.theme.LevelError
import com.example.ui.theme.LevelSuccess
import com.example.ui.theme.Navy700
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Navy950
import com.example.ui.theme.PurpleGlow
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MissionsScreen(
    missions: List<Mission>,
    onCompleteMission: (Mission) -> Unit,
    onCreateMission: (title: String, desc: String, category: String, xp: Int, isStudy: Boolean, mins: Int) -> Unit,
    onDeleteMission: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var selectedStatusTab by remember { mutableIntStateOf(0) } // 0 = Active, 1 = Completed, 2 = All
    var showCreateDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Study", "Reading", "Exercise", "Personal Growth")

    val filteredMissions = missions.filter { mission ->
        val matchesCategory = if (selectedCategoryFilter == "All") true else mission.category == selectedCategoryFilter
        val matchesStatus = when (selectedStatusTab) {
            0 -> !mission.isCompleted
            1 -> mission.isCompleted
            else -> true
        }
        matchesCategory && matchesStatus
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Navy950,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = PurplePrimary,
                contentColor = TextPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_create_mission")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Mission")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Screen Header
            Text(
                text = "Missions",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = "Complete objectives and earn XP to level up your scholar ranking",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Status Tabs (Active, Completed, All)
            TabRow(
                selectedTabIndex = selectedStatusTab,
                containerColor = Navy900,
                contentColor = PurplePrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedStatusTab]),
                        color = PurplePrimary
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                listOf("Active", "Completed", "All").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedStatusTab == index,
                        onClick = { selectedStatusTab = index },
                        text = {
                            val count = when (index) {
                                0 -> missions.count { !it.isCompleted }
                                1 -> missions.count { it.isCompleted }
                                else -> missions.size
                            }
                            Text(
                                text = "$title ($count)",
                                fontWeight = if (selectedStatusTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedStatusTab == index) TextPrimary else TextMuted
                            )
                        },
                        modifier = Modifier.testTag("tab_$title")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Horizontal Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategoryFilter == cat
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) PurplePrimary else Navy900,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) PurpleGlow else Navy700
                        ),
                        modifier = Modifier
                            .clickable { selectedCategoryFilter = cat }
                            .testTag("filter_cat_$cat")
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) TextPrimary else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mission List
            if (filteredMissions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyPlaceholder(
                        title = "No missions found",
                        subtitle = if (selectedStatusTab == 0) "Tap the '+' button to assign a new mission!" else "No completed missions in this category yet.",
                        emoji = if (selectedStatusTab == 1) "🏆" else "📝"
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredMissions, key = { it.id }) { mission ->
                        MissionItem(
                            mission = mission,
                            onComplete = { onCompleteMission(mission) },
                            onDelete = { onDeleteMission(mission.id) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateMissionDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title, desc, cat, xp, isStudy, mins ->
                onCreateMission(title, desc, cat, xp, isStudy, mins)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun MissionItem(
    mission: Mission,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Navy900),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (mission.isCompleted) LevelSuccess.copy(alpha = 0.5f) else Navy700
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("mission_item_${mission.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CategoryBadge(category = mission.category)
                    Spacer(modifier = Modifier.width(8.dp))
                    XpRewardTag(xp = mission.xpReward)
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp).testTag("delete_mission_${mission.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Mission",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = mission.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (mission.isCompleted) TextMuted else TextPrimary
            )

            if (mission.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mission.description,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action / Status Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (mission.isCompleted) {
                    val dateFormatted = mission.completedAt?.let {
                        val sdf = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                        sdf.format(Date(it))
                    } ?: "Completed"

                    Text(
                        text = "Completed: $dateFormatted",
                        fontSize = 12.sp,
                        color = LevelSuccess,
                        fontWeight = FontWeight.Medium
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LevelSuccess.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LevelSuccess.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = LevelSuccess,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Earned +${mission.xpReward} XP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = LevelSuccess
                            )
                        }
                    }
                } else {
                    Text(
                        text = if (mission.isStudySession) "25-min Study Block" else "Pending Completion",
                        fontSize = 12.sp,
                        color = TextMuted
                    )

                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("action_complete_mission_${mission.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Claim +${mission.xpReward} XP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMissionDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, desc: String, category: String, xp: Int, isStudy: Boolean, mins: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Study") }
    var xpReward by remember { mutableIntStateOf(25) }
    var isStudySession by remember { mutableStateOf(true) }
    var durationMinutes by remember { mutableIntStateOf(25) }

    val categories = listOf("Study", "Reading", "Exercise", "Personal Growth")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp)),
            color = Navy900
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
            ) {
                Text(
                    text = "New Mission",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Assign tasks to earn XP and level up",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Presets Quick Buttons
                Text(
                    text = "Quick Presets:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CategoryStudy.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CategoryStudy),
                            modifier = Modifier.clickable {
                                title = "25-min Focused Study Session"
                                selectedCategory = "Study"
                                xpReward = 25
                                isStudySession = true
                                durationMinutes = 25
                            }
                        ) {
                            Text(
                                text = "⏱️ Study (+25 XP)",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CategoryReading.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CategoryReading),
                            modifier = Modifier.clickable {
                                title = "Read 15 Pages for Class"
                                selectedCategory = "Reading"
                                xpReward = 10
                                isStudySession = false
                                durationMinutes = 0
                            }
                        ) {
                            Text(
                                text = "📖 Reading (+10 XP)",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = CategoryExercise.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CategoryExercise),
                            modifier = Modifier.clickable {
                                title = "Afternoon Jog / 20m Workout"
                                selectedCategory = "Exercise"
                                xpReward = 20
                                isStudySession = false
                                durationMinutes = 0
                            }
                        ) {
                            Text(
                                text = "🏃 Exercise (+20 XP)",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Mission Title") },
                    placeholder = { Text("e.g. Finish Biology Homework") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = Navy700,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = PurplePrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mission_title_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    placeholder = { Text("e.g. Problems 1 to 10 on page 42") },
                    maxLines = 2,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = Navy700,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = PurplePrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Category:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) PurplePrimary else Navy800,
                            modifier = Modifier.clickable {
                                selectedCategory = cat
                                if (cat == "Study") {
                                    xpReward = 25
                                    isStudySession = true
                                    durationMinutes = 25
                                } else if (cat == "Reading") {
                                    xpReward = 10
                                    isStudySession = false
                                }
                            }
                        ) {
                            Text(
                                text = cat,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // XP Reward Selector (Allow custom XP values for other tasks as requested)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "XP Reward:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "$xpReward XP",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldXP
                    )
                }

                Slider(
                    value = xpReward.toFloat(),
                    onValueChange = { xpReward = it.toInt() },
                    valueRange = 5f..50f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = GoldXP,
                        activeTrackColor = GoldXP,
                        inactiveTrackColor = Navy700
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Navy800),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Cancel", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onCreate(
                                    title,
                                    description,
                                    selectedCategory,
                                    xpReward,
                                    isStudySession,
                                    durationMinutes
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("submit_mission_button")
                    ) {
                        Text(text = "Create Mission", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
