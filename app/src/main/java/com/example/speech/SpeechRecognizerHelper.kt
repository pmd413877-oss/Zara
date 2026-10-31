package com.example.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class SpeechRecognizerHelper(private val context: Context) {

    companion object {
        private const val TAG = "SpeechRecognizerHelper"
    }

    private var speechRecognizer: SpeechRecognizer? = null
    var isListening: Boolean = false
        private set

    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun startListening(
        languageCode: String,
        onRmsChanged: (Float) -> Unit,
        onPartialResult: (String) -> Unit,
        onFinalResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isAvailable()) {
            onError("Speech recognition service is not available on this device.")
            return
        }

        try {
            cancel()

            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        isListening = true
                        Log.d(TAG, "SpeechRecognizer onReadyForSpeech")
                    }

                    override fun onBeginningOfSpeech() {
                        Log.d(TAG, "SpeechRecognizer onBeginningOfSpeech")
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // Normalize -2dB..10dB into 0f..1f range for visualizer
                        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                        onRmsChanged(normalized)
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        Log.d(TAG, "SpeechRecognizer onEndOfSpeech")
                        isListening = false
                    }

                    override fun onError(error: Int) {
                        isListening = false
                        val errorMessage = getErrorDescription(error)
                        Log.w(TAG, "SpeechRecognizer onError: $error ($errorMessage)")
                        onError(errorMessage)
                    }

                    override fun onResults(results: Bundle?) {
                        isListening = false
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()?.trim() ?: ""
                        Log.d(TAG, "SpeechRecognizer onResults: $text")
                        if (text.isNotBlank()) {
                            onFinalResult(text)
                        } else {
                            onError("No speech was detected. Please try again.")
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()?.trim() ?: ""
                        if (text.isNotBlank()) {
                            onPartialResult(text)
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)

                val speechLocale = when (languageCode) {
                    "bn" -> Locale("bn", "BD")
                    "hi" -> Locale("hi", "IN")
                    "ar" -> Locale("ar", "SA")
                    "es" -> Locale("es", "ES")
                    else -> Locale.US
                }
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, speechLocale.toLanguageTag())
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, speechLocale.toLanguageTag())
                putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("en-US", "bn-BD", "hi-IN"))
            }

            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            isListening = false
            Log.e(TAG, "Failed to start speech recognition", e)
            onError("Unable to initialize microphone: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    fun stop() {
        try {
            isListening = false
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping speech recognition", e)
        }
    }

    fun cancel() {
        try {
            isListening = false
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling speech recognition", e)
        }
    }

    fun destroy() {
        cancel()
    }

    private fun getErrorDescription(errorCode: Int): String {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Please check your microphone."
            SpeechRecognizer.ERROR_CLIENT -> "Speech recognition client error."
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required."
            SpeechRecognizer.ERROR_NETWORK -> "Network communication error. Please check connection."
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout while recognizing speech."
            SpeechRecognizer.ERROR_NO_MATCH -> "Couldn't understand speech. Please try speaking again."
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service is currently busy."
            SpeechRecognizer.ERROR_SERVER -> "Recognition server error occurred."
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected. Tap microphone to speak."
            else -> "Speech recognition error ($errorCode). Please try again."
        }
    }
}
