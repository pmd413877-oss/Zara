package com.example.navigation

sealed class Screen(val route: String) {
    data object IntroVideo : Screen("intro_video")
    data object Welcome : Screen("welcome")
    data object Onboarding : Screen("onboarding")
    data object ApiKeySetup : Screen("api_key_setup")
    data object MicrophonePermission : Screen("mic_permission")
    data object Home : Screen("home")
    data object VoiceListening : Screen("voice_listening")
    data object Chat : Screen("chat")
    data object ConversationHistory : Screen("history")
    data object Settings : Screen("settings")
    data object LanguageSelection : Screen("language_selection")
    data object VoiceSettings : Screen("voice_settings")
    data object ApiConfiguration : Screen("api_configuration")
    data object About : Screen("about")
}
