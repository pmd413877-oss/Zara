package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ZaraBackground
import com.example.ui.theme.ZaraCardBorder
import com.example.ui.theme.ZaraCyan
import com.example.ui.theme.ZaraPink
import com.example.ui.theme.ZaraPurple
import com.example.ui.theme.ZaraSurface
import com.example.ui.theme.ZaraTextMuted
import com.example.ui.theme.ZaraTextPrimary
import com.example.ui.theme.ZaraTextSecondary

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZaraBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("about_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ZaraTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "About Zara AI",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ZaraTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hero Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = ZaraSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "Archer AI Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F1E16))
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Archer AI",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ZaraTextPrimary
                )

                Text(
                    text = "Voice-Powered AI Assistant",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ZaraCyan
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Version 1.0 (Build 1) • Production Native Android",
                    fontSize = 12.sp,
                    color = ZaraTextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Architecture Tech Specs
        TechSpecCard(
            title = "Artificial Intelligence",
            subtitle = "Google Gemini 2.5 Flash / 1.5 Flash",
            description = "High-speed conversational intelligence with multilingual instruction tuning, reasoning, and context awareness.",
            icon = Icons.Default.AutoAwesome,
            accentColor = ZaraPurple
        )

        Spacer(modifier = Modifier.height(10.dp))

        TechSpecCard(
            title = "Speech Recognition (STT)",
            subtitle = "Android SpeechRecognizer Service",
            description = "Native on-device and cloud-assisted acoustic parsing supporting বাংলা (Bengali), English, हिन्दी (Hindi), and more.",
            icon = Icons.Default.Mic,
            accentColor = ZaraCyan
        )

        Spacer(modifier = Modifier.height(10.dp))

        TechSpecCard(
            title = "Speech Synthesis (TTS)",
            subtitle = "Android TextToSpeech Engine",
            description = "Synthesizes responses into natural human speech with adjustable rate, custom pitch, and dynamic interruption handling.",
            icon = Icons.Default.RecordVoiceOver,
            accentColor = ZaraPink
        )

        Spacer(modifier = Modifier.height(10.dp))

        TechSpecCard(
            title = "Mobile Engineering Stack",
            subtitle = "Modern Android & Jetpack Compose",
            description = "Kotlin Coroutines & Flow, Material 3, Room local persistence, DataStore preferences, and edge-to-edge system insets.",
            icon = Icons.Default.PhoneAndroid,
            accentColor = ZaraPurple
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Privacy & Terms
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = ZaraSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = ZaraCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Privacy & Local Data Policy",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ZaraTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Zara AI prioritizes user privacy. Raw voice recordings are never stored or transmitted. Voice questions are transcribed by your device's SpeechRecognizer and sent securely over encrypted HTTPS directly to Google Gemini. All conversation history and API keys are stored locally on your device in private sandbox storage.",
                    fontSize = 12.sp,
                    color = ZaraTextSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://policies.google.com/privacy")
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Google Privacy Policy", color = ZaraCyan, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        tint = ZaraCyan,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TechSpecCard(
    title: String,
    subtitle: String,
    description: String,
    icon: ImageVector,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ZaraSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
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

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZaraTextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = accentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = ZaraTextSecondary,
                    lineHeight = 17.sp
                )
            }
        }
    }
}
