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
        private val FALLBACK_CANDIDATES = listOf(
            "gemini-2.0-flash",
            "gemini-2.5-flash",
            "gemini-1.5-flash-latest",
            "gemini-flash-latest",
            "gemini-2.0-flash-lite",
            "gemini-1.5-pro",
            "gemini-1.5-flash"
        )
        private val API_VERSIONS = listOf("v1beta", "v1")
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(35, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    // Cache the detected working model name and API version
    @Volatile
    private var cachedWorkingModel: String? = null
    @Volatile
    private var cachedApiVersion: String = "v1beta"

    suspend fun generateResponse(
        apiKey: String,
        prompt: String,
        languageCode: String = "en",
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): Result<String> = withContext(Dispatchers.IO) {
        val cleanKey = apiKey.trim()
        if (cleanKey.isBlank()) {
            return@withContext Result.failure(
                IllegalArgumentException("API Key is missing. Please configure your Gemini API key in Settings.")
            )
        }

        // 1. If we have a cached working model, try it first
        cachedWorkingModel?.let { cached ->
            val result = tryModel(cachedApiVersion, cached, cleanKey, prompt, languageCode, conversationHistory)
            if (result.isSuccess) {
                return@withContext result
            }
            Log.w(TAG, "Cached model $cached failed, refreshing model discovery...")
            cachedWorkingModel = null
        }

        // 2. Discover available models from the user's API key
        val discoveredModels = queryAvailableModels(cleanKey)
        val candidateModels = if (discoveredModels.isNotEmpty()) {
            discoveredModels
        } else {
            FALLBACK_CANDIDATES
        }

        var lastError: Exception? = null

        // 3. Try candidates across API versions
        for (apiVersion in API_VERSIONS) {
            for (model in candidateModels) {
                Log.d(TAG, "Attempting Gemini request with $apiVersion/models/$model")
                val attempt = tryModel(apiVersion, model, cleanKey, prompt, languageCode, conversationHistory)
                if (attempt.isSuccess) {
                    cachedWorkingModel = model
                    cachedApiVersion = apiVersion
                    Log.i(TAG, "Successfully connected to Gemini model: $apiVersion/models/$model")
                    return@withContext attempt
                }

                val err = attempt.exceptionOrNull() as? Exception
                lastError = err

                // If auth error (403/400 invalid key), don't loop endlessly
                if (err is ApiException && (err.code == 403 || (err.code == 400 && err.message?.contains("API_KEY_INVALID", true) == true))) {
                    return@withContext attempt
                }
            }
        }

        return@withContext Result.failure(lastError ?: ApiException(500, "Unable to find an active Gemini model for this API key."))
    }

    suspend fun testConnection(apiKey: String): Result<String> = withContext(Dispatchers.IO) {
        val cleanKey = apiKey.trim()
        if (cleanKey.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Please enter an API key to test."))
        }

        // Try discovery or candidates
        val models = queryAvailableModels(cleanKey).ifEmpty { FALLBACK_CANDIDATES }

        for (apiVersion in API_VERSIONS) {
            for (model in models) {
                val result = tryModel(
                    apiVersion = apiVersion,
                    model = model,
                    apiKey = cleanKey,
                    prompt = "Hello! Answer with one word: Connected.",
                    languageCode = "en",
                    conversationHistory = emptyList()
                )
                if (result.isSuccess) {
                    cachedWorkingModel = model
                    cachedApiVersion = apiVersion
                    return@withContext Result.success("Connection successful! Connected to $model ($apiVersion).")
                }
            }
        }

        Result.failure(ApiException(404, "Connection test failed: No supported Gemini models found for this key."))
    }

    private fun queryAvailableModels(apiKey: String): List<String> {
        val url = "https://generativelanguage.googleapis.com/v1beta/models?key=$apiKey"
        val request = Request.Builder().url(url).get().build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return emptyList()
                val body = response.body?.string() ?: return emptyList()
                val json = JSONObject(body)
                val modelsArray = json.optJSONArray("models") ?: return emptyList()

                val supported = mutableListOf<String>()
                for (i in 0 until modelsArray.length()) {
                    val m = modelsArray.getJSONObject(i)
                    val name = m.optString("name", "").removePrefix("models/")
                    val methods = m.optJSONArray("supportedGenerationMethods")
                    var canGenerate = false
                    if (methods != null) {
                        for (j in 0 until methods.length()) {
                            if (methods.optString(j) == "generateContent") {
                                canGenerate = true
                                break
                            }
                        }
                    }
                    if (canGenerate && name.isNotBlank()) {
                        supported.add(name)
                    }
                }

                // Sort: flash models first, then 2.x, then others
                supported.sortedWith(
                    compareByDescending<String> { it.contains("2.5") || it.contains("2.0") }
                        .thenByDescending { it.contains("flash") }
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to query available models: ${e.message}")
            emptyList()
        }
    }

    private fun tryModel(
        apiVersion: String,
        model: String,
        apiKey: String,
        prompt: String,
        languageCode: String,
        conversationHistory: List<Pair<String, String>>
    ): Result<String> {
        val cleanModel = model.removePrefix("models/")
        val url = "https://generativelanguage.googleapis.com/$apiVersion/models/$cleanModel:generateContent?key=$apiKey"

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
                code == 404 -> "Model not available ($code): $message"
                code == 429 -> "Quota limit exceeded. Please wait a moment before trying again."
                code in 500..599 -> "Gemini service temporarily unavailable ($code). Please try again later."
                message.isNotBlank() -> message
                else -> "Request failed with HTTP status $code"
            }
        } catch (e: Exception) {
            when (code) {
                403 -> "Invalid API key. Please check your credentials."
                404 -> "Model not found ($code)."
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
