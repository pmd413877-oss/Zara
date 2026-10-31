package com.example.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import java.util.Locale

data class DeviceVoice(
    val name: String,
    val displayName: String,
    val locale: String,
    val isNetworkRequired: Boolean
)

class TextToSpeechHelper(
    private val context: Context,
    private val onInitComplete: (Boolean) -> Unit = {}
) : TextToSpeech.OnInitListener {

    companion object {
        private const val TAG = "TextToSpeechHelper"
        private const val UTTERANCE_ID = "ZARA_AI_RESPONSE"
    }

    private var tts: TextToSpeech? = null
    var isInitialized: Boolean = false
        private set
    var isSpeaking: Boolean = false
        private set

    private var onSpeechStart: (() -> Unit)? = null
    private var onSpeechDone: (() -> Unit)? = null
    private var onSpeechError: ((String) -> Unit)? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    isSpeaking = true
                    onSpeechStart?.invoke()
                }

                override fun onDone(utteranceId: String?) {
                    isSpeaking = false
                    onSpeechDone?.invoke()
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    isSpeaking = false
                    onSpeechError?.invoke("TextToSpeech playback error.")
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    isSpeaking = false
                    onSpeechError?.invoke("TextToSpeech error code: $errorCode")
                }
            })
            Log.d(TAG, "TextToSpeech initialized successfully.")
            onInitComplete(true)
        } else {
            isInitialized = false
            Log.e(TAG, "Failed to initialize TextToSpeech: status $status")
            onInitComplete(false)
        }
    }

    fun speak(
        text: String,
        languageCode: String,
        speechRate: Float = 1.0f,
        pitch: Float = 1.05f,
        voiceName: String = "",
        onStart: () -> Unit = {},
        onDone: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (!isInitialized || tts == null) {
            onError("TextToSpeech engine is not initialized yet.")
            return
        }

        this.onSpeechStart = onStart
        this.onSpeechDone = onDone
        this.onSpeechError = onError

        try {
            // Apply language locale
            val targetLocale = when (languageCode) {
                "bn" -> Locale("bn", "BD")
                "hi" -> Locale("hi", "IN")
                "ar" -> Locale("ar", "SA")
                "es" -> Locale("es", "ES")
                else -> Locale.US
            }

            var result = tts?.setLanguage(targetLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                result = tts?.setLanguage(Locale(targetLocale.language))
            }
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w(TAG, "Locale $targetLocale not supported, using default locale")
                tts?.setLanguage(Locale.getDefault())
            }

            // Set speech rate & pitch for crisp natural presence
            tts?.setSpeechRate(speechRate.coerceIn(0.6f, 1.8f))
            tts?.setPitch(pitch.coerceIn(0.7f, 1.5f))

            // Set specific voice if available, otherwise find highest quality voice for this language
            if (voiceName.isNotBlank()) {
                tts?.voices?.find { it.name == voiceName }?.let { voice ->
                    tts?.voice = voice
                }
            } else {
                // Auto-select the clearest, highest quality voice
                try {
                    val matchingVoices = tts?.voices?.filter {
                        it.locale.language == targetLocale.language
                    }
                    val highestQualityVoice = matchingVoices?.maxByOrNull { v ->
                        var score = 10
                        if (v.quality == Voice.QUALITY_VERY_HIGH) score += 30
                        else if (v.quality == Voice.QUALITY_HIGH) score += 20
                        if (v.latency == Voice.LATENCY_VERY_LOW || v.latency == Voice.LATENCY_LOW) score += 10
                        score
                    }
                    if (highestQualityVoice != null) {
                        tts?.voice = highestQualityVoice
                    }
                } catch (e: Throwable) {
                    Log.d(TAG, "Voice quality auto-selection skipped: ${e.message}")
                }
            }

            // Clean text thoroughly from markdown formatting, asterisks, emojis for natural speech
            val cleanedText = sanitizeTextForSpeech(text)

            if (cleanedText.isBlank()) {
                onDone()
                return
            }

            isSpeaking = true
            tts?.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)
        } catch (e: Exception) {
            isSpeaking = false
            Log.e(TAG, "Error in TTS speak", e)
            onError(e.localizedMessage ?: "TTS error")
        }
    }

    fun stop() {
        try {
            isSpeaking = false
            tts?.stop()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping TTS", e)
        }
    }

    fun getAvailableVoices(): List<DeviceVoice> {
        if (!isInitialized || tts == null) return emptyList()
        return try {
            tts?.voices?.map { voice ->
                DeviceVoice(
                    name = voice.name,
                    displayName = "${voice.locale.displayLanguage} (${voice.locale.country}) - ${voice.name.takeLast(6)}",
                    locale = voice.locale.language,
                    isNetworkRequired = voice.isNetworkConnectionRequired
                )
            }?.sortedBy { it.displayName } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error querying voices", e)
            emptyList()
        }
    }

    fun sanitizeTextForSpeech(text: String): String {
        return text
            .replace(Regex("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+"), "") // Strip unicode emojis
            .replace(Regex("\\*\\*(.*?)\\*\\*"), "$1") // bold markdown
            .replace(Regex("\\*(.*?)\\*"), "$1")     // italic markdown
            .replace(Regex("`{1,3}[\\s\\S]*?`{1,3}"), "") // code blocks
            .replace(Regex("#+\\s*"), "")             // markdown headers
            .replace(Regex("\\[(.*?)\\]\\(.*?\\)"), "$1") // link text
            .replace(Regex("^[-*•]\\s+", RegexOption.MULTILINE), "") // bullet points
            .replace(Regex("\\s+"), " ")             // duplicate spaces
            .trim()
    }

    fun destroy() {
        try {
            stop()
            tts?.shutdown()
            tts = null
        } catch (e: Exception) {
            Log.e(TAG, "Error destroying TTS", e)
        }
    }
}
