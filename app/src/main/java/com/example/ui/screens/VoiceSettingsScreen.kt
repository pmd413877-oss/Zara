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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.ui.theme.ZaraBackground
import com.example.ui.theme.ZaraCardBorder
import com.example.ui.theme.ZaraCyan
import com.example.ui.theme.ZaraElectricPurple
import com.example.ui.theme.ZaraPink
import com.example.ui.theme.ZaraPurple
import com.example.ui.theme.ZaraSurface
import com.example.ui.theme.ZaraTextMuted
import com.example.ui.theme.ZaraTextPrimary
import com.example.ui.theme.ZaraTextSecondary
import com.example.ui.viewmodel.VoiceAssistantViewModel

@Composable
fun VoiceSettingsScreen(
    viewModel: VoiceAssistantViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentSpeechRate by remember(uiState.speechRate) {
        mutableFloatStateOf(uiState.speechRate)
    }
    var selectedVoice by remember(uiState.selectedVoiceName) {
        mutableStateOf(uiState.selectedVoiceName)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZaraBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("voice_settings_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ZaraTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "Voice & Speech Settings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZaraTextPrimary
                )
                Text(
                    text = "Configure voice speed and synthesis",
                    fontSize = 11.sp,
                    color = ZaraTextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Speech Rate Slider Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ZaraSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = ZaraCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Speech Speed",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ZaraTextPrimary
                        )
                    }

                    Text(
                        text = "${String.format("%.2f", currentSpeechRate)}x",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZaraCyan
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Slider(
                    value = currentSpeechRate,
                    onValueChange = { currentSpeechRate = it },
                    valueRange = 0.6f..1.8f,
                    steps = 11,
                    colors = SliderDefaults.colors(
                        thumbColor = ZaraCyan,
                        activeTrackColor = ZaraElectricPurple,
                        inactiveTrackColor = ZaraCardBorder
                    ),
                    modifier = Modifier.testTag("speech_rate_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Slow (0.6x)", fontSize = 11.sp, color = ZaraTextMuted)
                    Text("Normal (1.0x)", fontSize = 11.sp, color = ZaraTextMuted)
                    Text("Fast (1.8x)", fontSize = 11.sp, color = ZaraTextMuted)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Test Voice Button
                OutlinedButton(
                    onClick = {
                        val sampleText = when (uiState.preferredLanguage) {
                            "bn" -> "নমস্কার, আমি জারা। এই গতিতে কথা বলছি।"
                            "hi" -> "नमस्ते, मैं ज़ारा हूँ। इस गति से बोल रही हूँ।"
                            "ar" -> "مرحباً، أنا زارا، أتحدث بهذه السرعة."
                            "es" -> "Hola, soy Zara. Hablando a esta velocidad."
                            else -> "Hello, I am Zara. Testing my speaking speed at ${String.format("%.1f", currentSpeechRate)}x."
                        }
                        viewModel.setSpeechRate(currentSpeechRate)
                        viewModel.testVoiceSample(sampleText)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("test_voice_sample_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ZaraPurple),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ZaraPurple.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Test Current Speech Rate", fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Device Installed Voices List
        Text(
            text = "Installed Device Voices",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ZaraTextPrimary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Default option
            item {
                VoiceItemRow(
                    name = "",
                    displayName = "System Default Engine Voice",
                    locale = "Automatic",
                    isSelected = selectedVoice.isBlank(),
                    onSelect = { selectedVoice = "" }
                )
            }

            // Installed device voices
            items(uiState.availableVoices, key = { it.name }) { voice ->
                VoiceItemRow(
                    name = voice.name,
                    displayName = voice.displayName,
                    locale = voice.locale,
                    isSelected = selectedVoice == voice.name,
                    onSelect = { selectedVoice = voice.name }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Save Button
        Button(
            onClick = {
                viewModel.setSpeechRate(currentSpeechRate)
                viewModel.setSelectedVoice(selectedVoice)
                onNavigateBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("save_voice_settings_button"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ZaraElectricPurple)
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Voice Settings", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun VoiceItemRow(
    name: String,
    displayName: String,
    locale: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1E1742) else ZaraSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) ZaraPurple else ZaraCardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color.White else ZaraTextPrimary
                )
                Text(
                    text = "Locale: $locale",
                    fontSize = 11.sp,
                    color = if (isSelected) ZaraCyan else ZaraTextMuted
                )
            }

            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = ZaraPurple,
                    unselectedColor = ZaraTextMuted
                )
            )
        }
    }
}
