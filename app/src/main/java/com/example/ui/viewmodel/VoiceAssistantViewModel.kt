package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.local.AppDatabase
import com.example.data.local.ConversationEntity
import com.example.data.preferences.PreferencesManager
import com.example.domain.model.VoiceAssistantState
import com.example.network.gemini.GeminiApiClient
import com.example.speech.DeviceVoice
import com.example.speech.SpeechRecognizerHelper
import com.example.speech.TextToSpeechHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.example.domain.action.DeviceActionHelper
import com.example.domain.action.DeviceActionResult
import kotlinx.coroutines.delay

sealed interface ConnectionTestStatus {
    data object Idle : ConnectionTestStatus
    data object Testing : ConnectionTestStatus
    data class Success(val message: String) : ConnectionTestStatus
    data class Error(val message: String) : ConnectionTestStatus
}

data class AssistantUiState(
    val assistantState: VoiceAssistantState = VoiceAssistantState.Idle,
    val currentPrompt: String = "",
    val currentResponse: String = "",
    val apiKey: String = "",
    val isApiKeyConfigured: Boolean = false,
    val preferredLanguage: String = "en",
    val speechRate: Float = 1.0f,
    val voicePitch: Float = 1.05f,
    val continuousConversation: Boolean = true,
    val selectedVoiceName: String = "",
    val autoSpeak: Boolean = true,
    val onboardingCompleted: Boolean = false,
    val isMicPermissionGranted: Boolean = false,
    val isSpeechAvailable: Boolean = true,
    val connectionTestStatus: ConnectionTestStatus = ConnectionTestStatus.Idle,
    val availableVoices: List<DeviceVoice> = emptyList(),
    val lastDeviceAction: DeviceActionResult? = null
)

class VoiceAssistantViewModel(
    context: Context,
    private val preferencesManager: PreferencesManager = PreferencesManager(context),
    private val database: AppDatabase = AppDatabase.getDatabase(context)
) : ViewModel() {

    private val geminiClient = GeminiApiClient()
    private val speechHelper = SpeechRecognizerHelper(context)
    private val deviceActionHelper = DeviceActionHelper(context)
    private var ttsHelper: TextToSpeechHelper? = null

    private val _uiState = MutableStateFlow(
        AssistantUiState(
            isSpeechAvailable = speechHelper.isAvailable()
        )
    )
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()

    val conversationsFlow: StateFlow<List<ConversationEntity>> = database.conversationDao()
        .getAllConversations()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private var activeGeminiJob: Job? = null

    init {
        ttsHelper = TextToSpeechHelper(context) { success ->
            if (success) {
                refreshAvailableVoices()
            }
        }

        // Collect preferences
        viewModelScope.launch {
            preferencesManager.apiKeyFlow.collect { key ->
                val effectiveKey = if (key.isNotBlank()) key else {
                    // Try BuildConfig key if present
                    try {
                        val buildKey = BuildConfig.GEMINI_API_KEY
                        if (buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY") buildKey else ""
                    } catch (e: Throwable) {
                        ""
                    }
                }
                _uiState.update {
                    it.copy(
                        apiKey = key,
                        isApiKeyConfigured = effectiveKey.isNotBlank()
                    )
                }
            }
        }

        viewModelScope.launch {
            preferencesManager.onboardingCompletedFlow.collect { completed ->
                _uiState.update { it.copy(onboardingCompleted = completed) }
            }
        }

        viewModelScope.launch {
            preferencesManager.preferredLanguageFlow.collect { lang ->
                _uiState.update { it.copy(preferredLanguage = lang) }
            }
        }

        viewModelScope.launch {
            preferencesManager.speechRateFlow.collect { rate ->
                _uiState.update { it.copy(speechRate = rate) }
            }
        }

        viewModelScope.launch {
            preferencesManager.selectedVoiceFlow.collect { voice ->
                _uiState.update { it.copy(selectedVoiceName = voice) }
            }
        }

        viewModelScope.launch {
            preferencesManager.autoSpeakFlow.collect { auto ->
                _uiState.update { it.copy(autoSpeak = auto) }
            }
        }

        viewModelScope.launch {
            preferencesManager.continuousConversationFlow.collect { continuous ->
                _uiState.update { it.copy(continuousConversation = continuous) }
            }
        }

        viewModelScope.launch {
            preferencesManager.voicePitchFlow.collect { pitch ->
                _uiState.update { it.copy(voicePitch = pitch) }
            }
        }
    }

    fun getEffectiveApiKey(): String {
        val userKey = _uiState.value.apiKey.trim()
        if (userKey.isNotBlank()) return userKey
        return try {
            val buildKey = BuildConfig.GEMINI_API_KEY.trim()
            if (buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY") buildKey else ""
        } catch (e: Throwable) {
            ""
        }
    }

    fun refreshAvailableVoices() {
        val voices = ttsHelper?.getAvailableVoices() ?: emptyList()
        _uiState.update { it.copy(availableVoices = voices) }
    }

    fun updateMicPermission(granted: Boolean) {
        _uiState.update { it.copy(isMicPermissionGranted = granted) }
    }

    fun startListening() {
        // Stop any active TTS first
        ttsHelper?.stop()

        _uiState.update {
            it.copy(
                assistantState = VoiceAssistantState.Listening(partialText = "", amplitude = 0.1f)
            )
        }

        speechHelper.startListening(
            languageCode = _uiState.value.preferredLanguage,
            onRmsChanged = { amp ->
                if (_uiState.value.assistantState is VoiceAssistantState.Listening) {
                    _uiState.update {
                        val currentListening = it.assistantState as? VoiceAssistantState.Listening
                        it.copy(
                            assistantState = VoiceAssistantState.Listening(
                                partialText = currentListening?.partialText ?: "",
                                amplitude = amp
                            )
                        )
                    }
                }
            },
            onPartialResult = { partial ->
                _uiState.update {
                    val currentListening = it.assistantState as? VoiceAssistantState.Listening
                    it.copy(
                        assistantState = VoiceAssistantState.Listening(
                            partialText = partial,
                            amplitude = currentListening?.amplitude ?: 0.3f
                        )
                    )
                }
            },
            onFinalResult = { prompt ->
                submitPrompt(prompt)
            },
            onError = { error, isTimeout ->
                if (isTimeout && _uiState.value.continuousConversation) {
                    // In continuous mode, silence or pause shouldn't show an intrusive red error
                    _uiState.update {
                        it.copy(assistantState = VoiceAssistantState.Idle)
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            assistantState = VoiceAssistantState.Error(
                                errorMessage = error,
                                canRetry = true
                            )
                        )
                    }
                }
            }
        )
    }

    fun stopListening() {
        speechHelper.stop()
    }

    fun cancelListening() {
        speechHelper.cancel()
        _uiState.update { it.copy(assistantState = VoiceAssistantState.Idle) }
    }

    private fun scheduleResumeListening() {
        if (!_uiState.value.continuousConversation) return
        viewModelScope.launch {
            delay(500)
            if (_uiState.value.assistantState is VoiceAssistantState.Idle) {
                startListening()
            }
        }
    }

    fun submitPrompt(prompt: String) {
        val trimmed = prompt.trim()
        if (trimmed.isBlank()) return

        // Cancel any active speech recognition and TTS
        speechHelper.stop()
        ttsHelper?.stop()

        val isBengali = _uiState.value.preferredLanguage == "bn"

        // 1. Check for Device Actions (Open Apps, YouTube search, Web Search, Play Store)
        val actionResult = deviceActionHelper.processVoiceCommand(trimmed, isBengali)
        if (actionResult != null && actionResult.isHandled) {
            val feedback = actionResult.feedbackMessage
            _uiState.update {
                it.copy(
                    currentPrompt = trimmed,
                    currentResponse = feedback,
                    lastDeviceAction = actionResult,
                    assistantState = VoiceAssistantState.Speaking(
                        userPrompt = trimmed,
                        responseText = feedback,
                        amplitude = 0.6f
                    )
                )
            }

            // Record in Room Database
            viewModelScope.launch {
                database.conversationDao().insert(
                    ConversationEntity(
                        prompt = trimmed,
                        response = feedback,
                        language = _uiState.value.preferredLanguage,
                        isSpoken = true
                    )
                )
            }

            // Speak feedback and resume listening if continuous
            if (_uiState.value.autoSpeak) {
                ttsHelper?.speak(
                    text = feedback,
                    languageCode = _uiState.value.preferredLanguage,
                    speechRate = _uiState.value.speechRate,
                    pitch = _uiState.value.voicePitch,
                    voiceName = _uiState.value.selectedVoiceName,
                    onDone = {
                        _uiState.update { it.copy(assistantState = VoiceAssistantState.Idle) }
                        scheduleResumeListening()
                    },
                    onError = {
                        _uiState.update { it.copy(assistantState = VoiceAssistantState.Idle) }
                    }
                )
            } else {
                _uiState.update { it.copy(assistantState = VoiceAssistantState.Idle) }
            }
            return
        }

        // 2. Otherwise process with Gemini AI
        val effectiveKey = getEffectiveApiKey()
        if (effectiveKey.isBlank()) {
            _uiState.update {
                it.copy(
                    assistantState = VoiceAssistantState.Error(
                        errorMessage = "Gemini API key is not configured. Please set your API key in Settings.",
                        canRetry = false
                    ),
                    currentPrompt = trimmed
                )
            }
            return
        }

        activeGeminiJob?.cancel()
        _uiState.update {
            it.copy(
                assistantState = VoiceAssistantState.Processing(userPrompt = trimmed),
                currentPrompt = trimmed
            )
        }

        activeGeminiJob = viewModelScope.launch {
            val history = conversationsFlow.value.take(4).reversed().map {
                it.prompt to it.response
            }

            val result = geminiClient.generateResponse(
                apiKey = effectiveKey,
                prompt = trimmed,
                languageCode = _uiState.value.preferredLanguage,
                conversationHistory = history
            )

            result.onSuccess { responseText ->
                _uiState.update {
                    it.copy(
                        currentResponse = responseText,
                        assistantState = VoiceAssistantState.Speaking(
                            userPrompt = trimmed,
                            responseText = responseText,
                            amplitude = 0.6f
                        )
                    )
                }

                // Save to Room database
                viewModelScope.launch {
                    database.conversationDao().insert(
                        ConversationEntity(
                            prompt = trimmed,
                            response = responseText,
                            language = _uiState.value.preferredLanguage,
                            isSpoken = true
                        )
                    )
                }

                // If auto speak is enabled, invoke TTS
                if (_uiState.value.autoSpeak) {
                    ttsHelper?.speak(
                        text = responseText,
                        languageCode = _uiState.value.preferredLanguage,
                        speechRate = _uiState.value.speechRate,
                        pitch = _uiState.value.voicePitch,
                        voiceName = _uiState.value.selectedVoiceName,
                        onStart = {
                            _uiState.update {
                                if (it.assistantState is VoiceAssistantState.Speaking) it else {
                                    it.copy(
                                        assistantState = VoiceAssistantState.Speaking(
                                            userPrompt = trimmed,
                                            responseText = responseText,
                                            amplitude = 0.7f
                                        )
                                    )
                                }
                            }
                        },
                        onDone = {
                            _uiState.update {
                                it.copy(assistantState = VoiceAssistantState.Idle)
                            }
                            scheduleResumeListening()
                        },
                        onError = {
                            _uiState.update {
                                it.copy(assistantState = VoiceAssistantState.Idle)
                            }
                        }
                    )
                } else {
                    _uiState.update { it.copy(assistantState = VoiceAssistantState.Idle) }
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        assistantState = VoiceAssistantState.Error(
                            errorMessage = err.localizedMessage ?: "Failed to get response from Gemini.",
                            canRetry = true
                        )
                    )
                }
            }
        }
    }

    fun stopSpeaking() {
        ttsHelper?.stop()
        _uiState.update { it.copy(assistantState = VoiceAssistantState.Idle) }
    }

    fun speakCurrentResponse() {
        val text = _uiState.value.currentResponse
        if (text.isBlank()) return

        ttsHelper?.speak(
            text = text,
            languageCode = _uiState.value.preferredLanguage,
            speechRate = _uiState.value.speechRate,
            voiceName = _uiState.value.selectedVoiceName,
            onStart = {
                _uiState.update {
                    it.copy(
                        assistantState = VoiceAssistantState.Speaking(
                            userPrompt = it.currentPrompt,
                            responseText = text,
                            amplitude = 0.7f
                        )
                    )
                }
            },
            onDone = {
                _uiState.update { it.copy(assistantState = VoiceAssistantState.Idle) }
            },
            onError = {
                _uiState.update { it.copy(assistantState = VoiceAssistantState.Idle) }
            }
        )
    }

    fun cancelProcessing() {
        activeGeminiJob?.cancel()
        _uiState.update { it.copy(assistantState = VoiceAssistantState.Idle) }
    }

    fun resetState() {
        speechHelper.cancel()
        ttsHelper?.stop()
        activeGeminiJob?.cancel()
        _uiState.update { it.copy(assistantState = VoiceAssistantState.Idle) }
    }

    fun testApiKey(key: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(connectionTestStatus = ConnectionTestStatus.Testing) }
            val result = geminiClient.testConnection(key.trim())
            result.onSuccess { message ->
                _uiState.update { it.copy(connectionTestStatus = ConnectionTestStatus.Success(message)) }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        connectionTestStatus = ConnectionTestStatus.Error(
                            err.localizedMessage ?: "Connection test failed."
                        )
                    )
                }
            }
        }
    }

    fun clearConnectionTestStatus() {
        _uiState.update { it.copy(connectionTestStatus = ConnectionTestStatus.Idle) }
    }

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            preferencesManager.setApiKey(key.trim())
        }
    }

    fun deleteApiKey() {
        viewModelScope.launch {
            preferencesManager.clearApiKey()
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            preferencesManager.setOnboardingCompleted(true)
        }
    }

    fun setPreferredLanguage(langCode: String) {
        viewModelScope.launch {
            preferencesManager.setPreferredLanguage(langCode)
        }
    }

    fun setSpeechRate(rate: Float) {
        viewModelScope.launch {
            preferencesManager.setSpeechRate(rate)
        }
    }

    fun setSelectedVoice(voiceName: String) {
        viewModelScope.launch {
            preferencesManager.setSelectedVoice(voiceName)
        }
    }

    fun setAutoSpeak(auto: Boolean) {
        viewModelScope.launch {
            preferencesManager.setAutoSpeak(auto)
        }
    }

    fun setContinuousConversation(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setContinuousConversation(enabled)
        }
    }

    fun setVoicePitch(pitch: Float) {
        viewModelScope.launch {
            preferencesManager.setVoicePitch(pitch)
        }
    }

    fun testVoiceSample(sample: String) {
        val defaultSample = if (_uiState.value.preferredLanguage == "bn") {
            "হ্যালো! আমি জারা। আপনার স্পষ্ট ও মিষ্টি কণ্ঠের পার্সোনাল ভয়েস অ্যাসিস্ট্যান্ট।"
        } else {
            "Hello! I am Zara, your crystal clear personal voice assistant."
        }
        val text = sample.ifBlank { defaultSample }
        ttsHelper?.speak(
            text = text,
            languageCode = _uiState.value.preferredLanguage,
            speechRate = _uiState.value.speechRate,
            pitch = _uiState.value.voicePitch,
            voiceName = _uiState.value.selectedVoiceName
        )
    }

    fun deleteConversation(id: Long) {
        viewModelScope.launch {
            database.conversationDao().deleteById(id)
        }
    }

    fun clearAllConversations() {
        viewModelScope.launch {
            database.conversationDao().deleteAll()
        }
    }

    fun resetAppSettings() {
        viewModelScope.launch {
            preferencesManager.resetAll()
            database.conversationDao().deleteAll()
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechHelper.destroy()
        ttsHelper?.destroy()
        activeGeminiJob?.cancel()
    }
}
