package com.example.ui.screens

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ZaraBackground
import com.example.ui.theme.ZaraCardBg
import com.example.ui.theme.ZaraCardBorder
import com.example.ui.theme.ZaraCyan
import com.example.ui.theme.ZaraElectricPurple
import com.example.ui.theme.ZaraGreen
import com.example.ui.theme.ZaraPurple
import com.example.ui.theme.ZaraRed
import com.example.ui.theme.ZaraSurface
import com.example.ui.theme.ZaraTextMuted
import com.example.ui.theme.ZaraTextPrimary
import com.example.ui.theme.ZaraTextSecondary
import com.example.ui.viewmodel.ConnectionTestStatus
import com.example.ui.viewmodel.VoiceAssistantViewModel

@Composable
fun ApiKeySetupScreen(
    viewModel: VoiceAssistantViewModel,
    isInitialSetup: Boolean = false,
    onNavigateBack: () -> Unit,
    onProceedToNext: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var inputKey by remember(uiState.apiKey) { mutableStateOf(uiState.apiKey) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var localValidationMessage by remember { mutableStateOf<String?>(null) }

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
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("api_key_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ZaraTextPrimary
                )
            }

            Text(
                text = "Gemini API Setup",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ZaraTextPrimary
            )

            if (isInitialSetup) {
                TextButton(
                    onClick = onProceedToNext,
                    modifier = Modifier.testTag("api_key_skip_button")
                ) {
                    Text("Skip", color = ZaraTextMuted)
                }
            } else {
                Spacer(modifier = Modifier.size(48.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Key Card Icon & Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(ZaraPurple.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = ZaraCyan,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            Column {
                Text(
                    text = "Configure Gemini API",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZaraTextPrimary
                )
                Text(
                    text = "Connect Zara AI with Google Gemini",
                    fontSize = 13.sp,
                    color = ZaraTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // API Key Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ZaraSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Google AI Studio API Key",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ZaraTextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = inputKey,
                    onValueChange = {
                        inputKey = it
                        localValidationMessage = null
                        viewModel.clearConnectionTestStatus()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("api_key_input_field"),
                    placeholder = {
                        Text("AIzaSy...", color = ZaraTextMuted)
                    },
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (inputKey.isNotBlank()) {
                            viewModel.testApiKey(inputKey)
                        }
                    }),
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Paste button
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                    val clip = clipboard?.primaryClip
                                    if (clip != null && clip.itemCount > 0) {
                                        val pastedText = clip.getItemAt(0).text?.toString() ?: ""
                                        inputKey = pastedText.trim()
                                        localValidationMessage = null
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = "Paste",
                                    tint = ZaraCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            // Show/Hide
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle visibility",
                                    tint = ZaraTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ZaraPurple,
                        unfocusedBorderColor = ZaraCardBorder,
                        focusedTextColor = ZaraTextPrimary,
                        unfocusedTextColor = ZaraTextPrimary,
                        cursorColor = ZaraCyan
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                localValidationMessage?.let {
                    Text(
                        text = it,
                        color = ZaraRed,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Test Connection & Save Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Test Button
                    OutlinedButton(
                        onClick = {
                            if (inputKey.isBlank()) {
                                localValidationMessage = "Please enter an API key to test."
                            } else {
                                viewModel.testApiKey(inputKey)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("test_connection_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ZaraCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCyan.copy(alpha = 0.6f))
                    ) {
                        if (uiState.connectionTestStatus is ConnectionTestStatus.Testing) {
                            CircularProgressIndicator(
                                color = ZaraCyan,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text("Test", fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Save Button
                    Button(
                        onClick = {
                            if (inputKey.isBlank()) {
                                localValidationMessage = "API key cannot be empty."
                            } else {
                                viewModel.saveApiKey(inputKey)
                                if (isInitialSetup) {
                                    onProceedToNext()
                                } else {
                                    localValidationMessage = null
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("save_api_key_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ZaraElectricPurple)
                    ) {
                        Text("Save Key", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // Delete key button if already set
                if (uiState.apiKey.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    TextButton(
                        onClick = { showDeleteConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("delete_api_key_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = ZaraRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Delete Saved Key", color = ZaraRed, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Connection Test Result Banner
        AnimatedVisibility(
            visible = uiState.connectionTestStatus !is ConnectionTestStatus.Idle,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            when (val status = uiState.connectionTestStatus) {
                is ConnectionTestStatus.Testing -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = ZaraCardBg),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ZaraPurple.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = ZaraPurple,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.size(12.dp))
                            Text(
                                text = "Connecting to Google Gemini API...",
                                color = ZaraTextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                is ConnectionTestStatus.Success -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF062817)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ZaraGreen.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = ZaraGreen,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.size(12.dp))
                            Text(
                                text = status.message,
                                color = Color(0xFFD1FAE5),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                is ConnectionTestStatus.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1214)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ZaraRed.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = ZaraRed,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.size(12.dp))
                            Text(
                                text = status.message,
                                color = Color(0xFFFEE2E2),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                else -> Unit
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Instructions & Obtaining Key Guide
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = ZaraSurface.copy(alpha = 0.6f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, ZaraCardBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "How to get a free API Key:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZaraTextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "1. Visit Google AI Studio at aistudio.google.com\n" +
                            "2. Sign in with your Google account.\n" +
                            "3. Click 'Get API key' and generate a new key.\n" +
                            "4. Copy and paste it into the field above.",
                    fontSize = 13.sp,
                    color = ZaraTextSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ZaraPurple),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ZaraPurple.copy(alpha = 0.5f))
                ) {
                    Text("Open Google AI Studio", fontSize = 13.sp)
                    Spacer(modifier = Modifier.size(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Security Notice: Your key is stored securely in your device's local private DataStore and sent directly to Google Gemini APIs over HTTPS. Never share your key.",
                    fontSize = 11.sp,
                    color = ZaraTextMuted,
                    lineHeight = 16.sp
                )
            }
        }
    }

    // Confirmation Dialog for Deletion
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text("Delete API Key?", color = ZaraTextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Zara AI will not be able to process AI voice or text queries until a key is configured again.",
                    color = ZaraTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteApiKey()
                        inputKey = ""
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZaraRed)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = ZaraTextSecondary)
                }
            },
            containerColor = ZaraSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }
}
