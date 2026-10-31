package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

data class OnboardingStep(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val steps = remember {
        listOf(
            OnboardingStep(
                title = "Voice Conversation",
                subtitle = "Speak naturally with Zara",
                description = "Tap the microphone to ask questions or give commands. Zara understands your voice clearly and conversationally.",
                icon = Icons.Default.Mic,
                accentColor = ZaraCyan
            ),
            OnboardingStep(
                title = "Multilingual Support",
                subtitle = "বাংলা, English, हिन्दी and more",
                description = "Communicate comfortably in your preferred language. Zara provides natural conversations in Bengali, Hindi, Arabic, Spanish, and English.",
                icon = Icons.Default.Translate,
                accentColor = ZaraPurple
            ),
            OnboardingStep(
                title = "AI Answers",
                subtitle = "Powered by Google Gemini",
                description = "Get intelligent, contextual, and helpful responses to questions on technology, science, everyday tasks, and creative ideas.",
                icon = Icons.Default.AutoAwesome,
                accentColor = ZaraPink
            ),
            OnboardingStep(
                title = "Natural Speech",
                subtitle = "Clear voice synthesis",
                description = "Hear responses read aloud with Android TextToSpeech. You can stop or interrupt playback at any time with a single tap.",
                icon = Icons.Default.RecordVoiceOver,
                accentColor = ZaraCyan
            ),
            OnboardingStep(
                title = "Conversation History",
                subtitle = "Locally saved records",
                description = "Access past discussions, replay voice responses, copy text, or clear conversations whenever you want with full privacy.",
                icon = Icons.Default.History,
                accentColor = ZaraPurple
            ),
            OnboardingStep(
                title = "Personal Settings",
                subtitle = "Customized to your taste",
                description = "Adjust speaking speed, choose your preferred TTS voice, configure your Gemini API key, and manage permissions easily.",
                icon = Icons.Default.Settings,
                accentColor = ZaraPink
            )
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }
    val currentStep = steps[currentIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZaraBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (currentIndex > 0) {
                IconButton(
                    onClick = { currentIndex-- },
                    modifier = Modifier.testTag("onboarding_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ZaraTextSecondary
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }

            TextButton(
                onClick = onFinish,
                modifier = Modifier.testTag("onboarding_skip")
            ) {
                Text(
                    text = "Skip",
                    color = ZaraTextMuted,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Dynamic Card Content
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "step_content"
        ) { step ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = ZaraSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Elevated glowing icon circle
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(step.accentColor.copy(alpha = 0.35f), ZaraSurface)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = step.icon,
                            contentDescription = null,
                            tint = step.accentColor,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = step.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZaraTextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = step.subtitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = step.accentColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = step.description,
                        fontSize = 14.sp,
                        color = ZaraTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Indicator Dots
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            steps.forEachIndexed { index, _ ->
                val isSelected = index == currentIndex
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .height(6.dp)
                        .size(if (isSelected) 24.dp else 6.dp, 6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (isSelected) ZaraPurple else ZaraTextMuted.copy(alpha = 0.4f))
                )
            }
        }

        // Action Buttons
        Button(
            onClick = {
                if (currentIndex < steps.size - 1) {
                    currentIndex++
                } else {
                    onFinish()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("onboarding_next_button"),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ZaraElectricPurple)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (currentIndex < steps.size - 1) "Next" else "Get Started",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.size(8.dp))
                Icon(
                    imageVector = if (currentIndex < steps.size - 1) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
