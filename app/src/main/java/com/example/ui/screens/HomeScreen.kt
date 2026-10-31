package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.domain.model.VoiceAssistantState
import com.example.navigation.Screen
import com.example.ui.components.WaveformVisualizer
import com.example.ui.components.ZaraOrb
import com.example.ui.theme.ZaraBackground
import com.example.ui.theme.ZaraCardBg
import com.example.ui.theme.ZaraCardBorder
import com.example.ui.theme.ZaraCyan
import com.example.ui.theme.ZaraElectricPurple
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
fun HomeScreen(
    viewModel: VoiceAssistantViewModel,
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var typedQuery by remember { mutableStateOf("") }

    val quickPrompts = remember {
        listOf(
            "Tell me a story",
            "বাংলায় একটি কবিতা বলো",
            "Explain quantum physics",
            "Give me a Kotlin example",
            "How can I improve my speech?"
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ZaraBackground)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // 1. Top Header Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onNavigate(Screen.ConversationHistory.route) },
                    modifier = Modifier.testTag("home_menu_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ZaraSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = ZaraTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "Archer AI Logo",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "ARCHER AI",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp,
                            color = ZaraTextPrimary
                        )
                        Text(
                            text = "VOICE-POWERED ASSISTANT",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            color = ZaraCyan
                        )
                    }
                }

                IconButton(
                    onClick = { onNavigate(Screen.Settings.route) },
                    modifier = Modifier.testTag("home_settings_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ZaraSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = ZaraTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 2. Persona Banner / Avatar Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .height(210.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0D1E16), Color(0xFF08120E), Color(0xFF090814))
                        )
                    )
                    .border(1.dp, Color(0xFF10B981).copy(alpha = 0.3f), RoundedCornerShape(26.dp))
            ) {
                // Background artistic portrait with user's custom logo
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "Archer AI Avatar",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                )

                // Atmospheric dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x990A071A),
                                    Color(0xF0080612)
                                )
                            )
                        )
                )

                // Callout texts
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CORE // ONLINE\nSYSTEM READY",
                                color = Color(0xFF34D399),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                lineHeight = 14.sp
                            )
                        }

                        Text(
                            text = "VOICE ENGINE\nACTIVE",
                            color = ZaraCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                            letterSpacing = 1.sp,
                            lineHeight = 14.sp
                        )
                    }

                    // Current language badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ZaraBackground.copy(alpha = 0.75f))
                            .clickable { onNavigate(Screen.LanguageSelection.route) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = null,
                            tint = ZaraCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = when (uiState.preferredLanguage) {
                                "bn" -> "বাংলা (Bengali)"
                                "hi" -> "हिन्दी (Hindi)"
                                "ar" -> "العربية (Arabic)"
                                "es" -> "Español"
                                else -> "English"
                            },
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 3. Central Voice Orb & Tap-to-Talk action
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Interactive Orb
                ZaraOrb(
                    state = uiState.assistantState,
                    size = 170.dp,
                    onClick = {
                        when (uiState.assistantState) {
                            is VoiceAssistantState.Speaking -> viewModel.stopSpeaking()
                            is VoiceAssistantState.Listening -> viewModel.stopListening()
                            is VoiceAssistantState.Processing -> viewModel.cancelProcessing()
                            else -> {
                                onNavigate(Screen.VoiceListening.route)
                                viewModel.startListening()
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Action label
                Text(
                    text = when (uiState.assistantState) {
                        is VoiceAssistantState.Listening -> "Listening... Tap to stop"
                        is VoiceAssistantState.Processing -> "Thinking..."
                        is VoiceAssistantState.Speaking -> "Speaking... Tap to interrupt"
                        is VoiceAssistantState.Error -> "Tap to retry"
                        else -> "Tap to Talk"
                    },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (uiState.assistantState) {
                        is VoiceAssistantState.Listening -> ZaraCyan
                        is VoiceAssistantState.Speaking -> ZaraPink
                        is VoiceAssistantState.Error -> ZaraRed
                        else -> ZaraTextPrimary
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Waveform or subtle dotted tracker
                if (uiState.assistantState is VoiceAssistantState.Listening || uiState.assistantState is VoiceAssistantState.Speaking) {
                    WaveformVisualizer(
                        isSpeaking = uiState.assistantState is VoiceAssistantState.Speaking,
                        amplitude = when (val s = uiState.assistantState) {
                            is VoiceAssistantState.Listening -> s.amplitude
                            is VoiceAssistantState.Speaking -> s.amplitude
                            else -> 0.5f
                        },
                        modifier = Modifier.padding(horizontal = 48.dp)
                    )
                } else {
                    Text(
                        text = "• • • • • | • • • • • | • • • • •",
                        color = ZaraTextMuted,
                        fontSize = 12.sp,
                        letterSpacing = 2.sp
                    )
                }
            }
        }

        // 4. Floating Input Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = typedQuery,
                    onValueChange = { typedQuery = it },
                    placeholder = {
                        Text(
                            text = "Ask me anything...",
                            color = ZaraTextMuted,
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = ZaraPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (typedQuery.isNotBlank()) {
                                    val q = typedQuery
                                    typedQuery = ""
                                    viewModel.submitPrompt(q)
                                }
                            },
                            modifier = Modifier.testTag("home_send_button")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (typedQuery.isNotBlank()) ZaraElectricPurple else ZaraSurfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = if (typedQuery.isNotBlank()) Color.White else ZaraTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (typedQuery.isNotBlank()) {
                            val q = typedQuery
                            typedQuery = ""
                            viewModel.submitPrompt(q)
                        }
                    }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_text_input"),
                    shape = RoundedCornerShape(28.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ZaraSurface,
                        unfocusedContainerColor = ZaraSurface,
                        focusedBorderColor = ZaraPurple,
                        unfocusedBorderColor = ZaraCardBorder,
                        focusedTextColor = ZaraTextPrimary,
                        unfocusedTextColor = ZaraTextPrimary,
                        cursorColor = ZaraCyan
                    )
                )
            }
        }

        // 5. Response Card (If active or present)
        item {
            AnimatedVisibility(
                visible = uiState.currentResponse.isNotBlank() || uiState.assistantState is VoiceAssistantState.Error || uiState.assistantState is VoiceAssistantState.Processing,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = ZaraCardBg),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // User Prompt
                        if (uiState.currentPrompt.isNotBlank()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(ZaraCyan.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = ZaraCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.size(10.dp))
                                Text(
                                    text = uiState.currentPrompt,
                                    color = ZaraTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // State status / Response
                        when (val st = uiState.assistantState) {
                            is VoiceAssistantState.Processing -> {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        color = ZaraPurple,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.size(10.dp))
                                    Text(
                                        text = "Zara is thinking...",
                                        color = ZaraTextSecondary,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            is VoiceAssistantState.Error -> {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = null,
                                        tint = ZaraRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.size(10.dp))
                                    Text(
                                        text = st.errorMessage,
                                        color = Color(0xFFFCA5A5),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            else -> {
                                Text(
                                    text = uiState.currentResponse,
                                    color = ZaraTextSecondary,
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Controls: Speak, Stop, Copy
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (uiState.assistantState is VoiceAssistantState.Speaking) {
                                        IconButton(
                                            onClick = { viewModel.stopSpeaking() },
                                            modifier = Modifier.testTag("response_stop_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Stop,
                                                contentDescription = "Stop speaking",
                                                tint = ZaraRed,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    } else {
                                        IconButton(
                                            onClick = { viewModel.speakCurrentResponse() },
                                            modifier = Modifier.testTag("response_speak_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeUp,
                                                contentDescription = "Read aloud",
                                                tint = ZaraCyan,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                            val clip = ClipData.newPlainText("Zara AI Response", uiState.currentResponse)
                                            clipboard?.setPrimaryClip(clip)
                                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.testTag("response_copy_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy text",
                                            tint = ZaraTextMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Action Shortcut Tiles (Chat, Languages, History)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionCard(
                    title = "Chat",
                    subtitle = "Get instant answers",
                    icon = Icons.Default.Chat,
                    accentColor = ZaraPurple,
                    modifier = Modifier.weight(1f),
                    testTag = "action_chat",
                    onClick = { onNavigate(Screen.Chat.route) }
                )

                ActionCard(
                    title = "Languages",
                    subtitle = "বাংলা • हिन्दी • EN",
                    icon = Icons.Default.Translate,
                    accentColor = ZaraCyan,
                    modifier = Modifier.weight(1f),
                    testTag = "action_languages",
                    onClick = { onNavigate(Screen.LanguageSelection.route) }
                )

                ActionCard(
                    title = "History",
                    subtitle = "Saved chats",
                    icon = Icons.Default.History,
                    accentColor = ZaraPink,
                    modifier = Modifier.weight(1f),
                    testTag = "action_history",
                    onClick = { onNavigate(Screen.ConversationHistory.route) }
                )
            }
        }

        // 7. Quick Prompt Chips
        item {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Text(
                    text = "Quick Questions",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZaraTextPrimary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quickPrompts.forEach { prompt ->
                        SuggestionChip(
                            onClick = {
                                viewModel.submitPrompt(prompt)
                            },
                            label = {
                                Text(
                                    text = prompt,
                                    fontSize = 12.sp,
                                    color = ZaraTextSecondary
                                )
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = ZaraSurface
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = ZaraCardBorder
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ZaraSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
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

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ZaraTextPrimary
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = ZaraTextMuted,
                maxLines = 1
            )
        }
    }
}
