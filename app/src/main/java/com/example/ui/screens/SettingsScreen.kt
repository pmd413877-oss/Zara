package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.navigation.Screen
import com.example.ui.theme.ZaraBackground
import com.example.ui.theme.ZaraCardBg
import com.example.ui.theme.ZaraCardBorder
import com.example.ui.theme.ZaraCyan
import com.example.ui.theme.ZaraElectricPurple
import com.example.ui.theme.ZaraGreen
import com.example.ui.theme.ZaraPink
import com.example.ui.theme.ZaraPurple
import com.example.ui.theme.ZaraRed
import com.example.ui.theme.ZaraSurface
import com.example.ui.theme.ZaraSurfaceVariant
import com.example.ui.theme.ZaraTextMuted
import com.example.ui.theme.ZaraTextPrimary
import com.example.ui.theme.ZaraTextSecondary
import com.example.ui.viewmodel.VoiceAssistantViewModel

@Composable
fun SettingsScreen(
    viewModel: VoiceAssistantViewModel,
    onNavigateBack: () -> Unit,
    onNavigateTo: (String) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    val isMicPermissionGranted = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ZaraBackground)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ZaraTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Settings",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZaraTextPrimary
                )
            }
        }

        // Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ZaraSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "Archer AI Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F1E16))
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "Archer AI",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ZaraTextPrimary
                        )
                        Text(
                            text = "Voice-Powered Assistant",
                            fontSize = 12.sp,
                            color = ZaraCyan
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (uiState.isApiKeyConfigured) ZaraGreen else ZaraRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.isApiKeyConfigured) "Gemini Connected" else "API Key Required",
                                fontSize = 11.sp,
                                color = if (uiState.isApiKeyConfigured) ZaraGreen else ZaraRed
                            )
                        }
                    }
                }
            }
        }

        // Section: Voice & Intelligence
        item {
            SectionHeader(title = "Voice & Intelligence")
        }

        item {
            SettingsNavigationRow(
                title = "Preferred Language",
                subtitle = when (uiState.preferredLanguage) {
                    "bn" -> "বাংলা (Bengali)"
                    "hi" -> "हिन्दी (Hindi)"
                    "ar" -> "العربية (Arabic)"
                    "es" -> "Español"
                    else -> "English"
                },
                icon = Icons.Default.Translate,
                accentColor = ZaraCyan,
                testTag = "settings_language_row",
                onClick = { onNavigateTo(Screen.LanguageSelection.route) }
            )
        }

        item {
            SettingsNavigationRow(
                title = "Speech Speed & Voice",
                subtitle = "${String.format("%.1f", uiState.speechRate)}x • ${uiState.selectedVoiceName.ifBlank { "System Default Voice" }}",
                icon = Icons.Default.RecordVoiceOver,
                accentColor = ZaraPurple,
                testTag = "settings_voice_row",
                onClick = { onNavigateTo(Screen.VoiceSettings.route) }
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ZaraSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ZaraPink.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = ZaraPink,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Auto-Speak Responses",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ZaraTextPrimary
                            )
                            Text(
                                text = "Speak answers aloud automatically",
                                fontSize = 11.sp,
                                color = ZaraTextMuted
                            )
                        }
                    }

                    Switch(
                        checked = uiState.autoSpeak,
                        onCheckedChange = { viewModel.setAutoSpeak(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = ZaraElectricPurple,
                            uncheckedThumbColor = ZaraTextMuted,
                            uncheckedTrackColor = ZaraSurfaceVariant
                        )
                    )
                }
            }
        }

        // Section: System & Data
        item {
            SectionHeader(title = "System & Data")
        }

        item {
            SettingsNavigationRow(
                title = "API Configuration",
                subtitle = if (uiState.isApiKeyConfigured) "Active • Configured" else "Configure Google AI Studio key",
                icon = Icons.Default.Key,
                accentColor = if (uiState.isApiKeyConfigured) ZaraGreen else ZaraCyan,
                testTag = "settings_api_config_row",
                onClick = { onNavigateTo(Screen.ApiConfiguration.route) }
            )
        }

        item {
            SettingsNavigationRow(
                title = "Conversation History",
                subtitle = "Manage saved chats and logs",
                icon = Icons.Default.History,
                accentColor = ZaraPurple,
                testTag = "settings_history_row",
                onClick = { onNavigateTo(Screen.ConversationHistory.route) }
            )
        }

        item {
            SettingsNavigationRow(
                title = "Microphone Permission",
                subtitle = if (isMicPermissionGranted) "Granted • Active" else "Not Granted • Tap to request",
                icon = Icons.Default.Mic,
                accentColor = if (isMicPermissionGranted) ZaraGreen else ZaraRed,
                testTag = "settings_mic_perm_row",
                onClick = { onNavigateTo(Screen.MicrophonePermission.route) }
            )
        }

        // Section: About & Danger
        item {
            SectionHeader(title = "About & General")
        }

        item {
            SettingsNavigationRow(
                title = "Play Intro Animation",
                subtitle = "Replay Archer AI opening video",
                icon = Icons.Default.PlayArrow,
                accentColor = Color(0xFF10B981),
                testTag = "settings_intro_video_row",
                onClick = { onNavigateTo(Screen.IntroVideo.route) }
            )
        }

        item {
            SettingsNavigationRow(
                title = "About Archer AI",
                subtitle = "v1.0 • Gemini 2.5 • Android M3",
                icon = Icons.Default.Info,
                accentColor = ZaraCyan,
                testTag = "settings_about_row",
                onClick = { onNavigateTo(Screen.About.route) }
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { showResetDialog = true }
                    .testTag("settings_reset_row"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ZaraSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ZaraRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = null,
                            tint = ZaraRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Reset All Settings",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ZaraRed
                        )
                        Text(
                            text = "Clear database and restore factory defaults",
                            fontSize = 11.sp,
                            color = ZaraTextMuted
                        )
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text("Reset All Settings?", color = ZaraTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "This will erase your saved Gemini API key, conversation history, and voice preferences.",
                    color = ZaraTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAppSettings()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZaraRed)
                ) {
                    Text("Reset Everything", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = ZaraTextSecondary)
                }
            },
            containerColor = ZaraSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = ZaraPurple,
        modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 6.dp)
    )
}

@Composable
private fun SettingsNavigationRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ZaraSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ZaraTextPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = ZaraTextMuted
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = ZaraTextMuted,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
