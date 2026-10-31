package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.navigation.Screen
import com.example.ui.components.ZaraBottomBar
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.ApiKeySetupScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.ConversationHistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IntroVideoScreen
import com.example.ui.screens.LanguageSelectionScreen
import com.example.ui.screens.MicrophonePermissionScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VoiceListeningScreen
import com.example.ui.screens.VoiceSettingsScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.ZaraAITheme
import com.example.ui.theme.ZaraBackground
import com.example.ui.viewmodel.VoiceAssistantViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ZaraAITheme {
                val appViewModel: VoiceAssistantViewModel = viewModel {
                    VoiceAssistantViewModel(applicationContext)
                }
                ZaraAppNavigation(viewModel = appViewModel)
            }
        }
    }
}

@Composable
fun ZaraAppNavigation(viewModel: VoiceAssistantViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val uiState by viewModel.uiState.collectAsState()

    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Chat.route,
        Screen.ConversationHistory.route,
        Screen.Settings.route
    )

    // Intro video plays first when opening the app
    val startDestination = Screen.IntroVideo.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ZaraBackground,
        bottomBar = {
            if (showBottomBar) {
                ZaraBottomBar(
                    currentRoute = currentRoute,
                    assistantState = uiState.assistantState,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onOrbClick = {
                        navController.navigate(Screen.VoiceListening.route)
                        viewModel.startListening()
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else androidx.compose.ui.unit.Dp.Unspecified),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            // 0. Intro Video (Plays first when opening app)
            composable(Screen.IntroVideo.route) {
                IntroVideoScreen(
                    onVideoFinished = {
                        val destination = if (uiState.onboardingCompleted) Screen.Home.route else Screen.Welcome.route
                        navController.navigate(destination) {
                            popUpTo(Screen.IntroVideo.route) { inclusive = true }
                        }
                    }
                )
            }

            // 1. Welcome
            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onGetStartedClick = {
                        navController.navigate(Screen.Onboarding.route)
                    }
                )
            }

            // 2. Onboarding
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinish = {
                        viewModel.completeOnboarding()
                        if (viewModel.getEffectiveApiKey().isBlank()) {
                            navController.navigate(Screen.ApiKeySetup.route) {
                                popUpTo(Screen.Welcome.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.MicrophonePermission.route) {
                                popUpTo(Screen.Welcome.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            // 3. API Key Setup (Initial)
            composable(Screen.ApiKeySetup.route) {
                ApiKeySetupScreen(
                    viewModel = viewModel,
                    isInitialSetup = true,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onProceedToNext = {
                        navController.navigate(Screen.MicrophonePermission.route) {
                            popUpTo(Screen.ApiKeySetup.route) { inclusive = true }
                        }
                    }
                )
            }

            // 4. Microphone Permission
            composable(Screen.MicrophonePermission.route) {
                MicrophonePermissionScreen(
                    onPermissionHandled = {
                        viewModel.updateMicPermission(true)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.MicrophonePermission.route) { inclusive = true }
                        }
                    }
                )
            }

            // 5. Main Home Screen
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }

            // 6. Voice Listening Screen
            composable(Screen.VoiceListening.route) {
                VoiceListeningScreen(
                    viewModel = viewModel,
                    onMinimize = {
                        navController.popBackStack()
                    }
                )
            }

            // 7. Chat (Text Input Mode)
            composable(Screen.Chat.route) {
                ChatScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onVoiceClick = {
                        navController.navigate(Screen.VoiceListening.route)
                        viewModel.startListening()
                    }
                )
            }

            // 8. Conversation History
            composable(Screen.ConversationHistory.route) {
                ConversationHistoryScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onSelectConversation = { prompt ->
                        viewModel.submitPrompt(prompt)
                        navController.navigate(Screen.Chat.route)
                    }
                )
            }

            // 9. Settings
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateTo = { route ->
                        navController.navigate(route)
                    }
                )
            }

            // 10. Language Selection
            composable(Screen.LanguageSelection.route) {
                LanguageSelectionScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // 11. Voice Settings
            composable(Screen.VoiceSettings.route) {
                VoiceSettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // 12. API Configuration (from Settings)
            composable(Screen.ApiConfiguration.route) {
                ApiKeySetupScreen(
                    viewModel = viewModel,
                    isInitialSetup = false,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // 13. About
            composable(Screen.About.route) {
                AboutScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // 14. Trading Mentor
            composable(Screen.TradingMentor.route) {
                com.example.ui.screens.TradingMentorScreen(
                    viewModel = viewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onAskZara = { prompt ->
                        viewModel.submitPrompt(prompt)
                        navController.navigate(Screen.VoiceListening.route)
                    }
                )
            }
        }
    }
}
