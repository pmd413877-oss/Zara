package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.VoiceAssistantState
import com.example.ui.components.WaveformVisualizer
import com.example.ui.components.ZaraOrb
import com.example.ui.theme.ZaraCyan
import com.example.ui.theme.ZaraPink
import com.example.ui.theme.ZaraPurple
import com.example.ui.theme.ZaraRed
import com.example.ui.theme.ZaraSurfaceVariant
import com.example.ui.theme.ZaraTextMuted
import com.example.ui.theme.ZaraTextPrimary
import com.example.ui.theme.ZaraTextSecondary
import com.example.ui.viewmodel.VoiceAssistantViewModel

@Composable
fun VoiceListeningScreen(
    viewModel: VoiceAssistantViewModel,
    onMinimize: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val currentStatusText = when (val state = uiState.assistantState) {
        is VoiceAssistantState.Listening -> "Zara is listening..."
        is VoiceAssistantState.Processing -> "Zara is thinking..."
        is VoiceAssistantState.Speaking -> "Zara is speaking..."
        is VoiceAssistantState.Error -> "Speech error"
        else -> "Zara is ready"
    }

    val recognizedPreview = when (val state = uiState.assistantState) {
        is VoiceAssistantState.Listening -> state.partialText.ifBlank { "Speak now..." }
        is VoiceAssistantState.Processing -> uiState.currentPrompt
        is VoiceAssistantState.Speaking -> uiState.currentResponse
        is VoiceAssistantState.Error -> state.errorMessage
        else -> uiState.currentResponse.ifBlank { "Tap microphone below to talk" }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F0B1E),
                        Color(0xFF070510),
                        Color(0xFF05040B)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Zara",
                    fontSize = 28.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light,
                    color = ZaraTextPrimary
                )

                IconButton(
                    onClick = onMinimize,
                    modifier = Modifier.testTag("listening_minimize_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ZaraSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Minimize",
                            tint = ZaraTextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Massive Glowing Orb
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(260.dp)
            ) {
                ZaraOrb(
                    state = uiState.assistantState,
                    size = 250.dp,
                    onClick = {
                        if (uiState.assistantState is VoiceAssistantState.Speaking) {
                            viewModel.stopSpeaking()
                        } else if (uiState.assistantState is VoiceAssistantState.Listening) {
                            viewModel.stopListening()
                        } else {
                            viewModel.startListening()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.weight(0.4f))

            // Status and Subtitles
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = currentStatusText,
                    fontSize = 24.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Normal,
                    color = when (uiState.assistantState) {
                        is VoiceAssistantState.Listening -> ZaraCyan
                        is VoiceAssistantState.Speaking -> ZaraPink
                        is VoiceAssistantState.Error -> ZaraRed
                        else -> ZaraTextPrimary
                    },
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Real-time live transcript preview
                Text(
                    text = recognizedPreview,
                    fontSize = 15.sp,
                    color = ZaraTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    maxLines = 4,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Active waveform
                WaveformVisualizer(
                    isSpeaking = uiState.assistantState is VoiceAssistantState.Speaking,
                    amplitude = when (val s = uiState.assistantState) {
                        is VoiceAssistantState.Listening -> s.amplitude
                        is VoiceAssistantState.Speaking -> s.amplitude
                        else -> 0.1f
                    },
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }

            Spacer(modifier = Modifier.weight(0.6f))

            // Bottom 3 Action Controls (Mute / Mic, End / Stop, Minimize)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Mute / Toggle Mic
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(ZaraSurfaceVariant)
                            .testTag("listening_mic_toggle")
                            .clickable {
                                if (uiState.assistantState is VoiceAssistantState.Listening) {
                                    viewModel.stopListening()
                                } else {
                                    viewModel.startListening()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (uiState.assistantState is VoiceAssistantState.Listening) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Toggle Mic",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (uiState.assistantState is VoiceAssistantState.Listening) "Mute" else "Speak",
                        color = ZaraTextMuted,
                        fontSize = 12.sp
                    )
                }

                // 2. End / Interrupt Button (Large Red Call-End style button)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(ZaraRed)
                            .testTag("listening_end_button")
                            .clickable {
                                viewModel.resetState()
                                onMinimize()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "End",
                        color = ZaraTextMuted,
                        fontSize = 12.sp
                    )
                }

                // 3. Minimize
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(ZaraSurfaceVariant)
                            .testTag("listening_minimize_bottom")
                            .clickable { onMinimize() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Minimize",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Minimize",
                        color = ZaraTextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
