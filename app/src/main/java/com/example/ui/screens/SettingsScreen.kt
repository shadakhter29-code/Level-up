package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.GoldXP
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

@Composable
fun SettingsScreen(
    userProfile: UserProfile?,
    onUpdateProfile: (name: String, school: String, avatar: String, goals: List<String>, sound: Boolean, reminders: Boolean) -> Unit,
    onResetProgress: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember(userProfile) { mutableStateOf(userProfile?.displayName ?: "Student") }
    var school by remember(userProfile) { mutableStateOf(userProfile?.gradeOrSchool ?: "High School") }
    var avatar by remember(userProfile) { mutableStateOf(userProfile?.avatarEmoji ?: "🚀") }
    var soundEnabled by remember(userProfile) { mutableStateOf(userProfile?.soundEffectsEnabled ?: true) }
    var remindersEnabled by remember(userProfile) { mutableStateOf(userProfile?.dailyRemindersEnabled ?: true) }

    var showResetDialog by remember { mutableStateOf(false) }
    var isEditingProfile by remember { mutableStateOf(false) }

    val avatars = listOf("🚀", "⚡", "🧠", "📚", "🏆", "🌟", "🛡️", "🔥")

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
                text = "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = "Account, preferences, and privacy information",
                fontSize = 13.sp,
                color = TextSecondary
            )
        }

        // Account / Student Profile Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Navy900),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Navy700),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_profile_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = PurplePrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Student Profile",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        TextButton(
                            onClick = {
                                if (isEditingProfile) {
                                    val goalsList = userProfile?.selectedGoals?.split(",") ?: listOf("Ace Exams")
                                    onUpdateProfile(name, school, avatar, goalsList, soundEnabled, remindersEnabled)
                                }
                                isEditingProfile = !isEditingProfile
                            },
                            modifier = Modifier.testTag("toggle_edit_profile")
                        ) {
                            Text(
                                text = if (isEditingProfile) "Save" else "Edit",
                                color = PurpleGlow,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isEditingProfile) {
                        Text(
                            text = "Choose Avatar:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            avatars.forEach { a ->
                                val isSelected = a == avatar
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) PurplePrimary else Navy800)
                                        .border(1.5.dp, if (isSelected) GoldXP else Navy700, CircleShape)
                                        .clickable { avatar = a },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = a, fontSize = 18.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Display Name") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurplePrimary,
                                unfocusedBorderColor = Navy700,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("edit_name_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = school,
                            onValueChange = { school = it },
                            label = { Text("Grade / School") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurplePrimary,
                                unfocusedBorderColor = Navy700,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("edit_school_input")
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(Navy800)
                                    .border(1.5.dp, PurplePrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = userProfile?.avatarEmoji ?: "🚀", fontSize = 26.sp)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = userProfile?.displayName ?: "Student",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = userProfile?.gradeOrSchool ?: "High School",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Goals: ${userProfile?.selectedGoals ?: "Study, Habits"}",
                                    fontSize = 11.sp,
                                    color = PurpleGlow
                                )
                            }
                        }
                    }
                }
            }
        }

        // Notification & Preferences Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Navy900),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Navy700),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = PurplePrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Preferences & Reminders",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Daily Habit Reminders", fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                            Text(text = "Prompt daily check-ins to protect your streaks", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(
                            checked = remindersEnabled,
                            onCheckedChange = {
                                remindersEnabled = it
                                val goalsList = userProfile?.selectedGoals?.split(",") ?: emptyList()
                                onUpdateProfile(name, school, avatar, goalsList, soundEnabled, it)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = PurplePrimary, checkedTrackColor = PurpleGlow),
                            modifier = Modifier.testTag("toggle_reminders")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Haptic & Sound Effects", fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                            Text(text = "Vibrate on mission completion and level-ups", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = {
                                soundEnabled = it
                                val goalsList = userProfile?.selectedGoals?.split(",") ?: emptyList()
                                onUpdateProfile(name, school, avatar, goalsList, it, remindersEnabled)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = PurplePrimary, checkedTrackColor = PurpleGlow),
                            modifier = Modifier.testTag("toggle_sound")
                        )
                    }
                }
            }
        }

        // Privacy & External Service Information Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Navy900),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Navy700),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = LevelSuccess,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Privacy & Safe for Students",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "• 100% On-Device Storage: All missions, habits, XP, and study logs are stored locally on your device in a secure SQLite Room database.\n" +
                                "• Zero Tracking: No analytics, tracking pixels, or ads.\n" +
                                "• Safe Gamification: Rewards are entirely virtual badges and XP with zero gambling or cash prizes.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Transparent Architecture Notice
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = GoldXP,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "External Services & Paid Plans: LevelUp operates fully offline with no paid subscriptions required. Cloud multi-device sync would require a cloud backend (e.g. Firebase), but this local edition protects your privacy and works anywhere without internet.",
                            fontSize = 11.sp,
                            color = TextMuted,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Reset Data Button
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Navy900),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LevelError.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reset Progress",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = LevelError
                        )
                        Text(
                            text = "Clear database and restart with fresh sample missions",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Button(
                        onClick = { showResetDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = LevelError.copy(alpha = 0.2f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, LevelError),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("reset_data_button")
                    ) {
                        Text(text = "Reset", color = LevelError, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(text = "Reset All Progress?", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Text(
                    text = "This will reset all your missions, habits, study logs, and earned XP back to the student onboarding defaults. This action cannot be undone.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        onResetProgress()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LevelError)
                ) {
                    Text("Yes, Reset Everything", color = TextPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = Navy900,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
