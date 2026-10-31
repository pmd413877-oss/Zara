package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "zara_ai_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        val KEY_API_KEY = stringPreferencesKey("gemini_api_key")
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_PREFERRED_LANGUAGE = stringPreferencesKey("preferred_language")
        val KEY_SPEECH_RATE = floatPreferencesKey("speech_rate")
        val KEY_SELECTED_VOICE = stringPreferencesKey("selected_voice_name")
        val KEY_AUTO_SPEAK = booleanPreferencesKey("auto_speak_response")
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_CONTINUOUS_CONVERSATION = booleanPreferencesKey("continuous_conversation")
        val KEY_VOICE_PITCH = floatPreferencesKey("voice_pitch")
    }

    val continuousConversationFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_CONTINUOUS_CONVERSATION] ?: true
    }

    val voicePitchFlow: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[KEY_VOICE_PITCH] ?: 1.05f
    }

    val apiKeyFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_API_KEY] ?: ""
    }

    val onboardingCompletedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_ONBOARDING_COMPLETED] ?: false
    }

    val preferredLanguageFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_PREFERRED_LANGUAGE] ?: "en"
    }

    val speechRateFlow: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[KEY_SPEECH_RATE] ?: 1.0f
    }

    val selectedVoiceFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_SELECTED_VOICE] ?: ""
    }

    val autoSpeakFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_AUTO_SPEAK] ?: true
    }

    val themeModeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_THEME_MODE] ?: "dark"
    }

    suspend fun setApiKey(apiKey: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_API_KEY] = apiKey.trim()
        }
    }

    suspend fun clearApiKey() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_API_KEY)
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setPreferredLanguage(languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_PREFERRED_LANGUAGE] = languageCode
        }
    }

    suspend fun setSpeechRate(rate: Float) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SPEECH_RATE] = rate
        }
    }

    suspend fun setSelectedVoice(voiceName: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SELECTED_VOICE] = voiceName
        }
    }

    suspend fun setAutoSpeak(autoSpeak: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTO_SPEAK] = autoSpeak
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = mode
        }
    }

    suspend fun setContinuousConversation(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_CONTINUOUS_CONVERSATION] = enabled
        }
    }

    suspend fun setVoicePitch(pitch: Float) {
        context.dataStore.edit { preferences ->
            preferences[KEY_VOICE_PITCH] = pitch
        }
    }

    suspend fun resetAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
