package com.example.domain.model

sealed interface VoiceAssistantState {
    data object Idle : VoiceAssistantState
    data class Listening(
        val partialText: String = "",
        val amplitude: Float = 0.0f
    ) : VoiceAssistantState
    data class Processing(
        val userPrompt: String = ""
    ) : VoiceAssistantState
    data class Speaking(
        val userPrompt: String,
        val responseText: String,
        val amplitude: Float = 0.5f
    ) : VoiceAssistantState
    data class Error(
        val errorMessage: String,
        val canRetry: Boolean = true
    ) : VoiceAssistantState
}
