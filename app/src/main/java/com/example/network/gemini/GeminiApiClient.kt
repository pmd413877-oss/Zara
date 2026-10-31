package com.example.network.gemini

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class GeminiApiClient {

    companion object {
        private const val TAG = "GeminiApiClient"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
        private const val PRIMARY_MODEL = "gemini-2.5-flash"
        private const val FALLBACK_MODEL = "gemini-1.5-flash"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .build()

    suspend fun generateResponse(
        apiKey: String,
        prompt: String,
        languageCode: String = "en",
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalArgumentException("API Key is missing. Please configure your Gemini API key in Settings.")
            )
        }

        // Try primary model first, fallback to 1.5-flash if needed
        val firstAttempt = tryModel(PRIMARY_MODEL, apiKey, prompt, languageCode, conversationHistory)
        if (firstAttempt.isSuccess) {
            return@withContext firstAttempt
        }

        val err = firstAttempt.exceptionOrNull()
        if (err is ApiException && (err.code == 404 || err.code == 400)) {
            Log.w(TAG, "Primary model failed with ${err.code}, trying fallback model $FALLBACK_MODEL")
            return@withContext tryModel(FALLBACK_MODEL, apiKey, prompt, languageCode, conversationHistory)
        }

        return@withContext firstAttempt
    }

    suspend fun testConnection(apiKey: String): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Please enter an API key to test."))
        }
        try {
            val result = tryModel(
                model = PRIMARY_MODEL,
                apiKey = apiKey,
                prompt = "Respond with one word: 'Connected'.",
                languageCode = "en",
                conversationHistory = emptyList()
            )
            if (result.isSuccess) {
                Result.success("Connection successful! Gemini API is responsive.")
            } else {
                val fallback = tryModel(
                    model = FALLBACK_MODEL,
                    apiKey = apiKey,
                    prompt = "Respond with one word: 'Connected'.",
                    languageCode = "en",
                    conversationHistory = emptyList()
                )
                if (fallback.isSuccess) {
                    Result.success("Connection successful via fallback model! Gemini API is responsive.")
                } else {
                    fallback
                }
            }
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    private fun tryModel(
        model: String,
        apiKey: String,
        prompt: String,
        languageCode: String,
        conversationHistory: List<Pair<String, String>>
    ): Result<String> {
        val url = "$BASE_URL/$model:generateContent?key=$apiKey"
        val languageInstruction = when (languageCode) {
            "bn" -> "You are Zara AI, a warm, intelligent personal voice assistant. Respond fluently and naturally in Bengali (বাংলা). Keep voice answers concise, clear, and engaging (2 to 4 sentences unless asked for details)."
            "hi" -> "You are Zara AI, a warm, intelligent personal voice assistant. Respond fluently and naturally in Hindi (हिन्दी). Keep voice answers concise and clear."
            "ar" -> "You are Zara AI, a personal voice assistant. Respond fluently in Arabic. Keep voice answers concise and clear."
            "es" -> "You are Zara AI, a personal voice assistant. Respond fluently in Spanish. Keep voice answers concise and clear."
            else -> "You are Zara AI, a personal voice assistant. Respond conversationally, concisely, and warmly. Keep spoken answers engaging, helpful, and natural (2 to 4 sentences unless depth is requested)."
        }

        val requestJson = JSONObject()

        // System Instruction
        val sysInstructionObj = JSONObject()
        val sysPartsArray = JSONArray().apply {
            put(JSONObject().put("text", languageInstruction))
        }
        sysInstructionObj.put("parts", sysPartsArray)
        requestJson.put("systemInstruction", sysInstructionObj)

        // Contents
        val contentsArray = JSONArray()

        // Add previous turns (max 3 pairs)
        val recentHistory = conversationHistory.takeLast(3)
        for ((userMsg, assistantMsg) in recentHistory) {
            contentsArray.put(
                JSONObject()
                    .put("role", "user")
                    .put("parts", JSONArray().put(JSONObject().put("text", userMsg)))
            )
            contentsArray.put(
                JSONObject()
                    .put("role", "model")
                    .put("parts", JSONArray().put(JSONObject().put("text", assistantMsg)))
            )
        }

        // Current turn
        contentsArray.put(
            JSONObject()
                .put("role", "user")
                .put("parts", JSONArray().put(JSONObject().put("text", prompt)))
        )
        requestJson.put("contents", contentsArray)

        // Generation Config
        val genConfig = JSONObject()
            .put("temperature", 0.7)
            .put("maxOutputTokens", 800)
        requestJson.put("generationConfig", genConfig)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Content-Type", "application/json")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                val responseString = response.body?.string() ?: ""
                val code = response.code

                if (!response.isSuccessful) {
                    val errorMessage = parseErrorResponse(code, responseString)
                    return Result.failure(ApiException(code, errorMessage))
                }

                val parsedText = parseCandidateText(responseString)
                if (parsedText.isBlank()) {
                    Result.failure(ApiException(code, "Received empty response from Gemini."))
                } else {
                    Result.success(parsedText)
                }
            }
        } catch (e: Exception) {
            Result.failure(mapException(e))
        }
    }

    private fun parseCandidateText(jsonStr: String): String {
        return try {
            val root = JSONObject(jsonStr)
            val candidates = root.optJSONArray("candidates") ?: return ""
            if (candidates.length() == 0) return ""
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content") ?: return ""
            val parts = content.optJSONArray("parts") ?: return ""
            val sb = StringBuilder()
            for (i in 0 until parts.length()) {
                val part = parts.getJSONObject(i)
                val text = part.optString("text", "")
                sb.append(text)
            }
            sb.toString().trim()
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Gemini response JSON", e)
            ""
        }
    }

    private fun parseErrorResponse(code: Int, errorBody: String): String {
        return try {
            val root = JSONObject(errorBody)
            val error = root.optJSONObject("error")
            val message = error?.optString("message") ?: ""
            when {
                code == 400 -> "Invalid request or API key: $message"
                code == 403 -> "Invalid or restricted API key. Please check your Google AI Studio key."
                code == 429 -> "Quota limit exceeded. Please wait a moment before trying again."
                code in 500..599 -> "Gemini service temporarily unavailable ($code). Please try again later."
                message.isNotBlank() -> message
                else -> "Request failed with HTTP status $code"
            }
        } catch (e: Exception) {
            when (code) {
                403 -> "Invalid API key. Please check your credentials."
                429 -> "API rate limit reached. Please wait a few moments."
                else -> "HTTP $code: Server error occurred."
            }
        }
    }

    private fun mapException(e: Exception): Exception {
        return when (e) {
            is java.net.UnknownHostException -> IOException("No internet connection. Please verify your network.")
            is java.net.SocketTimeoutException -> IOException("Connection timed out. Gemini took too long to respond.")
            else -> e
        }
    }
}

class ApiException(val code: Int, message: String) : Exception(message)
