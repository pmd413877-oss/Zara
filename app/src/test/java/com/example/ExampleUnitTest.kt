package com.example

import com.example.domain.model.VoiceAssistantState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testVoiceAssistantStateCycle() {
        val idle = VoiceAssistantState.Idle
        assertTrue(idle is VoiceAssistantState.Idle)

        val listening = VoiceAssistantState.Listening(partialText = "Zara", amplitude = 0.5f)
        assertEquals("Zara", listening.partialText)
        assertEquals(0.5f, listening.amplitude, 0.01f)

        val processing = VoiceAssistantState.Processing(userPrompt = "Who are you?")
        assertEquals("Who are you?", processing.userPrompt)

        val speaking = VoiceAssistantState.Speaking(
            userPrompt = "Who are you?",
            responseText = "I am Zara, your personal voice assistant.",
            amplitude = 0.7f
        )
        assertEquals("Who are you?", speaking.userPrompt)
        assertTrue(speaking.responseText.contains("Zara"))

        val error = VoiceAssistantState.Error(errorMessage = "Microphone error", canRetry = true)
        assertTrue(error.canRetry)
        assertEquals("Microphone error", error.errorMessage)
    }

    @Test
    fun testTextSanitizationForSpeech() {
        val markdownText = "**Hello!** This is *Zara* with `code block` and # Header."
        val sanitized = markdownText
            .replace(Regex("\\*\\*(.*?)\\*\\*"), "$1")
            .replace(Regex("\\*(.*?)\\*"), "$1")
            .replace(Regex("`{1,3}(.*?)`{1,3}"), "$1")
            .replace(Regex("#+\\s*"), "")
            .trim()

        assertEquals("Hello! This is Zara with code block and Header.", sanitized)
    }
}
