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
import com.example.ui.theme.ZaraSurfaceVariant
import com.example.ui.theme.ZaraTextMuted
import com.example.ui.theme.ZaraTextPrimary
import com.example.ui.theme.ZaraTextSecondary
import com.example.ui.viewmodel.VoiceAssistantViewModel

import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults

@Composable
fun VoiceSettingsScreen(
    viewModel: VoiceAssistantViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentSpeechRate by remember(uiState.speechRate) {
        mutableFloatStateOf(uiState.speechRate)
    }
    var currentPitch by remember(uiState.voicePitch) {
        mutableFloatStateOf(uiState.voicePitch)
    }
    var selectedVoice by remember(uiState.selectedVoiceName) {
        mutableStateOf(uiState.selectedVoiceName)
    }
    var continuousTalk by remember(uiState.continuousConversation) {
        mutableStateOf(uiState.continuousConversation)
    }

    val isBn = uiState.preferredLanguage == "bn"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZaraBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
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
                    text = if (isBn) "ভয়েস ও উচ্চারণ সেটিংস" else "Voice & Speech Settings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZaraTextPrimary
                )
                Text(
                    text = if (isBn) "ক্লিয়ার ভয়েস পিচ, স্পিড ও অনবরত কথোপকথন" else "Clear audio tuning, tone presets & continuous talk",
                    fontSize = 11.sp,
                    color = ZaraTextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Continuous Conversation Switch Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
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
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(ZaraCyan.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = ZaraCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isBn) "অনবরত ব্যাক-টু-ব্যাক কথা বলা" else "Continuous Auto-Conversation",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ZaraTextPrimary
                                )
                                Text(
                                    text = if (isBn) "জারা উত্তর দেওয়ার পর স্বয়ংক্রিয়ভাবে পরবর্তী কথা শুনবে।" else "Zara keeps listening after replying without button taps.",
                                    fontSize = 11.sp,
                                    color = ZaraTextMuted
                                )
                            }
                        }

                        Switch(
                            checked = continuousTalk,
                            onCheckedChange = { continuousTalk = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = ZaraCyan,
                                uncheckedThumbColor = ZaraTextMuted,
                                uncheckedTrackColor = ZaraCardBorder
                            )
                        )
                    }
                }
            }

            // 2. Clear Voice Pitch & Tone Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ZaraSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = null,
                                    tint = ZaraCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBn) "ভয়েস ক্লিয়ারিটি ও পিচ (Pitch)" else "Voice Clarity & Pitch",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ZaraTextPrimary
                                )
                            }
                            Text(
                                text = "${String.format("%.2f", currentPitch)}x",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZaraCyan
                            )
                        }

                        Slider(
                            value = currentPitch,
                            onValueChange = { currentPitch = it },
                            valueRange = 0.8f..1.4f,
                            steps = 11,
                            colors = SliderDefaults.colors(
                                thumbColor = ZaraCyan,
                                activeTrackColor = ZaraCyan,
                                inactiveTrackColor = ZaraCardBorder
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Deep (0.8x)", fontSize = 11.sp, color = ZaraTextMuted)
                            Text("Clear Studio (1.05x)", fontSize = 11.sp, color = ZaraCyan)
                            Text("Bright (1.4x)", fontSize = 11.sp, color = ZaraTextMuted)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Preset Chips
                        Text(
                            text = if (isBn) "দ্রুত টোন প্রিসেট:" else "Voice Tone Presets:",
                            fontSize = 12.sp,
                            color = ZaraTextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SuggestionChip(
                                onClick = { currentPitch = 1.08f; currentSpeechRate = 1.0f },
                                label = { Text("Studio Crisp", fontSize = 11.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = ZaraSurfaceVariant)
                            )
                            SuggestionChip(
                                onClick = { currentPitch = 1.0f; currentSpeechRate = 0.95f },
                                label = { Text("Natural Warm", fontSize = 11.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = ZaraSurfaceVariant)
                            )
                            SuggestionChip(
                                onClick = { currentPitch = 1.15f; currentSpeechRate = 1.1f },
                                label = { Text("Fast & Crisp", fontSize = 11.sp) },
                                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = ZaraSurfaceVariant)
                            )
                        }
                    }
                }
            }

            // 3. Speech Rate Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ZaraSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = ZaraElectricPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBn) "কথা বলার গতি (Speed)" else "Speaking Rate",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ZaraTextPrimary
                                )
                            }

                            Text(
                                text = "${String.format("%.2f", currentSpeechRate)}x",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZaraElectricPurple
                            )
                        }

                        Slider(
                            value = currentSpeechRate,
                            onValueChange = { currentSpeechRate = it },
                            valueRange = 0.6f..1.8f,
                            steps = 11,
                            colors = SliderDefaults.colors(
                                thumbColor = ZaraElectricPurple,
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
                                val sampleText = if (isBn) {
                                    "নমস্কার! আমি জারা। আপনার স্পষ্ট ও মধুর কন্ঠের অ্যাসিস্ট্যান্ট।"
                                } else {
                                    "Hello! I am Zara, testing this clear voice pitch and speed."
                                }
                                viewModel.setVoicePitch(currentPitch)
                                viewModel.setSpeechRate(currentSpeechRate)
                                viewModel.testVoiceSample(sampleText)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("test_voice_sample_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ZaraCyan),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCyan.copy(alpha = 0.6f))
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isBn) "ক্লিয়ার ভয়েস টেস্ট করুন" else "Play Clear Voice Sample", fontSize = 13.sp)
                        }
                    }
                }
            }

            // 4. Device Voice List
            item {
                Text(
                    text = if (isBn) "ডিভাইস ভয়েস ইঞ্জিন সমূহ" else "Installed Device Voices",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZaraTextPrimary,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            item {
                VoiceItemRow(
                    name = "",
                    displayName = if (isBn) "স্বয়ংক্রিয় হাই-কোয়ালিটি ক্লিয়ার ভয়েস (প্রস্তাবিত)" else "Auto High-Quality Crisp Voice (Recommended)",
                    locale = "Auto",
                    isSelected = selectedVoice.isBlank(),
                    onSelect = { selectedVoice = "" }
                )
            }

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
                viewModel.setVoicePitch(currentPitch)
                viewModel.setSelectedVoice(selectedVoice)
                viewModel.setContinuousConversation(continuousTalk)
                onNavigateBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("voice_settings_save_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ZaraElectricPurple)
        ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isBn) "সেটিংস সংরক্ষণ করুন" else "Save Settings", fontSize = 15.sp, fontWeight = FontWeight.Bold)
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
