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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ZaraBackground
import com.example.ui.theme.ZaraCardBorder
import com.example.ui.theme.ZaraCyan
import com.example.ui.theme.ZaraElectricPurple
import com.example.ui.theme.ZaraPurple
import com.example.ui.theme.ZaraSurface
import com.example.ui.theme.ZaraTextMuted
import com.example.ui.theme.ZaraTextPrimary
import com.example.ui.theme.ZaraTextSecondary
import com.example.ui.viewmodel.VoiceAssistantViewModel

data class LanguageOption(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val region: String,
    val sampleGreeting: String
)

@Composable
fun LanguageSelectionScreen(
    viewModel: VoiceAssistantViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedLanguageCode by remember(uiState.preferredLanguage) {
        mutableStateOf(uiState.preferredLanguage)
    }

    val languages = remember {
        listOf(
            LanguageOption(
                code = "bn",
                nativeName = "বাংলা",
                englishName = "Bengali",
                region = "Bangladesh / India",
                sampleGreeting = "নমস্কার! আমি জারা, আপনার ভয়েস সহকারী।"
            ),
            LanguageOption(
                code = "en",
                nativeName = "English",
                englishName = "English (US / Global)",
                region = "Global",
                sampleGreeting = "Hello! I am Zara, your voice assistant."
            ),
            LanguageOption(
                code = "hi",
                nativeName = "हिन्दी",
                englishName = "Hindi",
                region = "India",
                sampleGreeting = "नमस्ते! मैं ज़ारा हूँ, आपकी आवाज़ सहायक।"
            ),
            LanguageOption(
                code = "ar",
                nativeName = "العربية",
                englishName = "Arabic",
                region = "Middle East / North Africa",
                sampleGreeting = "مرحباً! أنا زارا، مساعدتك الصوتية الشخصية."
            ),
            LanguageOption(
                code = "es",
                nativeName = "Español",
                englishName = "Spanish",
                region = "Spain / Latin America",
                sampleGreeting = "¡Hola! Soy Zara, tu asistente de voz."
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZaraBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("language_back_button")
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
                    text = "Preferred Language",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZaraTextPrimary
                )
                Text(
                    text = "Controls speech recognition & TTS locale",
                    fontSize = 11.sp,
                    color = ZaraTextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Language Cards List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(languages, key = { it.code }) { lang ->
                val isSelected = lang.code == selectedLanguageCode
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { selectedLanguageCode = lang.code }
                        .testTag("lang_option_${lang.code}"),
                    shape = RoundedCornerShape(18.dp),
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
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = lang.nativeName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else ZaraTextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "(${lang.englishName})",
                                    fontSize = 13.sp,
                                    color = ZaraTextMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = lang.sampleGreeting,
                                fontSize = 12.sp,
                                color = if (isSelected) ZaraCyan else ZaraTextSecondary
                            )
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedLanguageCode = lang.code },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = ZaraPurple,
                                unselectedColor = ZaraTextMuted
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                viewModel.setPreferredLanguage(selectedLanguageCode)
                onNavigateBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("save_language_button"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ZaraElectricPurple)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Apply Language",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
