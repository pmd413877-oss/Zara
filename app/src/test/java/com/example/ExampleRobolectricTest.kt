package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.domain.model.VoiceAssistantState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Zara AI", appName)
    }

    @Test
    fun `voice assistant state transitions`() {
        val idleState: VoiceAssistantState = VoiceAssistantState.Idle
        assertTrue(idleState is VoiceAssistantState.Idle)

        val listeningState: VoiceAssistantState = VoiceAssistantState.Listening(partialText = "Hello", amplitude = 0.8f)
        assertTrue(listeningState is VoiceAssistantState.Listening)
        assertEquals("Hello", (listeningState as VoiceAssistantState.Listening).partialText)

        val processingState: VoiceAssistantState = VoiceAssistantState.Processing(userPrompt = "What is AI?")
        assertTrue(processingState is VoiceAssistantState.Processing)
        assertEquals("What is AI?", (processingState as VoiceAssistantState.Processing).userPrompt)

        val speakingState: VoiceAssistantState = VoiceAssistantState.Speaking(userPrompt = "Hi", responseText = "Hello!")
        assertTrue(speakingState is VoiceAssistantState.Speaking)
    }
}
