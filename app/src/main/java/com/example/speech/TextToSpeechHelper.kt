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

            // Set speech rate
            tts?.setSpeechRate(speechRate.coerceIn(0.5f, 2.0f))

            // Set specific voice if available
            if (voiceName.isNotBlank()) {
                tts?.voices?.find { it.name == voiceName }?.let { voice ->
                    tts?.voice = voice
                }
            }

            // Clean text from markdown formatting or asterisks before speaking
            val cleanedText = sanitizeTextForSpeech(text)

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

    private fun sanitizeTextForSpeech(text: String): String {
        return text
            .replace(Regex("\\*\\*(.*?)\\*\\*"), "$1") // remove bold markdown
            .replace(Regex("\\*(.*?)\\*"), "$1")     // remove italic markdown
            .replace(Regex("`{1,3}(.*?)`{1,3}"), "$1") // remove code
            .replace(Regex("#+\\s*"), "")             // remove headers
            .replace(Regex("\\[(.*?)\\]\\(.*?\\)"), "$1") // links
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
