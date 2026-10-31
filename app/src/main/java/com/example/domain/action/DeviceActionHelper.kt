package com.example.domain.action

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log

enum class ActionType {
    OPEN_APP,
    SEARCH_YOUTUBE,
    SEARCH_WEB,
    SEARCH_PLAYSTORE,
    NONE
}

data class DeviceActionResult(
    val isHandled: Boolean,
    val actionType: ActionType,
    val feedbackMessage: String,
    val targetName: String? = null,
    val targetIntent: Intent? = null,
    val success: Boolean = false
)

class DeviceActionHelper(private val context: Context) {

    companion object {
        private const val TAG = "DeviceActionHelper"

        private val POPULAR_APPS = mapOf(
            "youtube" to "com.google.android.youtube",
            "ইউটিউব" to "com.google.android.youtube",
            "chrome" to "com.android.chrome",
            "ক্রোম" to "com.android.chrome",
            "browser" to "com.android.chrome",
            "whatsapp" to "com.whatsapp",
            "হোয়াটসঅ্যাপ" to "com.whatsapp",
            "facebook" to "com.facebook.katana",
            "ফেসবুক" to "com.facebook.katana",
            "instagram" to "com.instagram.android",
            "ইনস্টাগ্রাম" to "com.instagram.android",
            "ইনস্টা" to "com.instagram.android",
            "telegram" to "org.telegram.messenger",
            "টেলিগ্রাম" to "org.telegram.messenger",
            "messenger" to "com.facebook.orca",
            "মেসেঞ্জার" to "com.facebook.orca",
            "tiktok" to "com.zhiliaoapp.musically",
            "টিকটক" to "com.zhiliaoapp.musically",
            "camera" to "camera_intent",
            "ক্যামেরা" to "camera_intent",
            "gallery" to "gallery_intent",
            "গ্যালারি" to "gallery_intent",
            "photos" to "com.google.android.apps.photos",
            "ছবি" to "gallery_intent",
            "settings" to "settings_intent",
            "সেটিংস" to "settings_intent",
            "calculator" to "calculator_intent",
            "ক্যালকুলেটর" to "calculator_intent",
            "clock" to "clock_intent",
            "ঘড়ি" to "clock_intent",
            "alarm" to "clock_intent",
            "অ্যালার্ম" to "clock_intent",
            "play store" to "com.android.vending",
            "প্লে স্টোর" to "com.android.vending",
            "gmail" to "com.google.android.gm",
            "ইমেইল" to "com.google.android.gm",
            "মেইল" to "com.google.android.gm",
            "maps" to "com.google.android.apps.maps",
            "ম্যাপ" to "com.google.android.apps.maps",
            "map" to "com.google.android.apps.maps",
            "spotify" to "com.spotify.music",
            "স্পটিফাই" to "com.spotify.music"
        )
    }

    /**
     * Inspects the prompt and executes device actions if it matches
     * app opening or search requests.
     */
    fun processVoiceCommand(prompt: String, isBengali: Boolean): DeviceActionResult? {
        val lower = prompt.trim().lowercase()

        // 1. Check Search Commands first
        val searchResult = handleSearchCommand(lower, isBengali)
        if (searchResult != null) return searchResult

        // 2. Check App Launch Commands
        val launchResult = handleAppLaunchCommand(lower, prompt, isBengali)
        if (launchResult != null) return launchResult

        return null
    }

    private fun handleSearchCommand(lower: String, isBengali: Boolean): DeviceActionResult? {
        // Bengali Search patterns:
        // "ইউটিউবে সার্চ করো গান", "গুগলে সার্চ করো বিটকয়েন", "প্লে স্টোরে সার্চ করো ফ্রি ফায়ার", "সার্চ করো লার্ন ট্রেডিং"
        val isYouTubeSearch = lower.contains("ইউটিউবে সার্চ") || lower.contains("youtube search") ||
                lower.startsWith("search on youtube") || lower.startsWith("search youtube for") ||
                lower.contains("search in youtube")

        if (isYouTubeSearch) {
            val query = extractSearchQuery(
                lower,
                listOf("ইউটিউবে সার্চ করো", "ইউটিউবে সার্চ", "youtube search", "search on youtube for", "search on youtube", "search youtube for")
            )
            return executeYouTubeSearch(query, isBengali)
        }

        val isPlayStoreSearch = lower.contains("প্লে স্টোরে সার্চ") || lower.contains("play store search") ||
                lower.startsWith("search on play store") || lower.startsWith("search play store for")

        if (isPlayStoreSearch) {
            val query = extractSearchQuery(
                lower,
                listOf("প্লে স্টোরে সার্চ করো", "প্লে স্টোরে সার্চ", "play store search", "search on play store for", "search on play store", "search play store for")
            )
            return executePlayStoreSearch(query, isBengali)
        }

        val isWebSearch = lower.contains("গুগলে সার্চ") || lower.contains("google search") ||
                lower.startsWith("search google for") || lower.startsWith("search on google") ||
                lower.startsWith("সার্চ করো") || (lower.endsWith("সার্চ করো") && !lower.contains("ওপেন")) ||
                lower.startsWith("search for ") || lower.startsWith("find ")

        if (isWebSearch) {
            val query = extractSearchQuery(
                lower,
                listOf("গুগলে সার্চ করো", "গুগলে সার্চ", "google search", "search google for", "search on google for", "search on google", "সার্চ করো", "search for", "find")
            )
            return executeWebSearch(query, isBengali)
        }

        return null
    }

    private fun extractSearchQuery(lower: String, prefixes: List<String>): String {
        var cleaned = lower
        for (prefix in prefixes) {
            if (cleaned.contains(prefix)) {
                cleaned = cleaned.substringAfter(prefix).trim()
                break
            }
        }
        // Remove trailing filler words
        cleaned = cleaned.removeSuffix("সার্চ করো").removeSuffix("খুঁজে দাও").removeSuffix("সার্চ").trim()
        return if (cleaned.isNotBlank()) cleaned else "trending"
    }

    private fun executeYouTubeSearch(query: String, isBengali: Boolean): DeviceActionResult {
        try {
            val appIntent = Intent(Intent.ACTION_SEARCH).apply {
                setPackage("com.google.android.youtube")
                putExtra("query", query)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val intentToLaunch = if (isIntentAvailable(appIntent)) {
                appIntent
            } else {
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(query)}")
                ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            }

            context.startActivity(intentToLaunch)
            val feedback = if (isBengali) {
                "ইউটিউবে '$query' সার্চ করে দেখাচ্ছি..."
            } else {
                "Searching for '$query' on YouTube..."
            }
            return DeviceActionResult(
                isHandled = true,
                actionType = ActionType.SEARCH_YOUTUBE,
                feedbackMessage = feedback,
                targetName = "YouTube",
                targetIntent = intentToLaunch,
                success = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error executing YouTube search", e)
            return DeviceActionResult(
                isHandled = true,
                actionType = ActionType.SEARCH_YOUTUBE,
                feedbackMessage = if (isBengali) "ইউটিউব সার্চ সম্পন্ন করা সম্ভব হয়নি।" else "Could not open YouTube search.",
                success = false
            )
        }
    }

    private fun executePlayStoreSearch(query: String, isBengali: Boolean): DeviceActionResult {
        try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://search?q=${Uri.encode(query)}")
            ).apply {
                setPackage("com.android.vending")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val intentToLaunch = if (isIntentAvailable(intent)) {
                intent
            } else {
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/search?q=${Uri.encode(query)}")
                ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            }

            context.startActivity(intentToLaunch)
            val feedback = if (isBengali) {
                "প্লে স্টোরে '$query' সার্চ করছি..."
            } else {
                "Searching for '$query' on Google Play Store..."
            }
            return DeviceActionResult(
                isHandled = true,
                actionType = ActionType.SEARCH_PLAYSTORE,
                feedbackMessage = feedback,
                targetName = "Play Store",
                targetIntent = intentToLaunch,
                success = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error executing Play Store search", e)
            return DeviceActionResult(
                isHandled = true,
                actionType = ActionType.SEARCH_PLAYSTORE,
                feedbackMessage = if (isBengali) "প্লে স্টোর সার্চ করা সম্ভব হয়নি।" else "Could not open Play Store search.",
                success = false
            )
        }
    }

    private fun executeWebSearch(query: String, isBengali: Boolean): DeviceActionResult {
        try {
            val searchIntent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra(SearchManager.QUERY, query)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val intentToLaunch = if (isIntentAvailable(searchIntent)) {
                searchIntent
            } else {
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")
                ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            }

            context.startActivity(intentToLaunch)
            val feedback = if (isBengali) {
                "গুগলে '$query' সার্চ করছি..."
            } else {
                "Searching Google for '$query'..."
            }
            return DeviceActionResult(
                isHandled = true,
                actionType = ActionType.SEARCH_WEB,
                feedbackMessage = feedback,
                targetName = "Google",
                targetIntent = intentToLaunch,
                success = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error executing Web search", e)
            return DeviceActionResult(
                isHandled = true,
                actionType = ActionType.SEARCH_WEB,
                feedbackMessage = if (isBengali) "সার্চ করা সম্ভব হয়নি।" else "Search failed.",
                success = false
            )
        }
    }

    private fun handleAppLaunchCommand(lower: String, originalPrompt: String, isBengali: Boolean): DeviceActionResult? {
        val triggers = listOf(
            "ওপেন করো", "ওপেন করে দাও", "খুলে দাও", "খোলো", "চালু করো", "চালু করে দাও",
            "open", "launch", "start"
        )

        var isAppRequest = false
        var appNameKeyword = ""

        for (trigger in triggers) {
            if (lower.contains(trigger)) {
                isAppRequest = true
                appNameKeyword = lower
                    .replace(trigger, "")
                    .replace("আমার", "")
                    .replace("এই", "")
                    .replace("অ্যাপসটা", "")
                    .replace("অ্যাপসটি", "")
                    .replace("অ্যাপটা", "")
                    .replace("অ্যাপটি", "")
                    .replace("অ্যাপস", "")
                    .replace("অ্যাপ", "")
                    .replace("the", "")
                    .replace("app", "")
                    .replace("please", "")
                    .trim()
                break
            }
        }

        if (!isAppRequest || appNameKeyword.isBlank()) return null

        // Try to match popular app shortcuts
        val mappedTarget = POPULAR_APPS[appNameKeyword] ?: POPULAR_APPS.entries.find {
            appNameKeyword.contains(it.key) || it.key.contains(appNameKeyword)
        }?.value

        if (mappedTarget != null) {
            return launchSpecialOrPackage(mappedTarget, appNameKeyword, isBengali)
        }

        // Try dynamically querying installed applications on device
        val dynamicApp = findInstalledApp(appNameKeyword)
        if (dynamicApp != null) {
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage(dynamicApp.packageName)?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            if (intent != null) {
                try {
                    context.startActivity(intent)
                    val label = dynamicApp.loadLabel(pm).toString()
                    val feedback = if (isBengali) {
                        "$label অ্যাপটি ওপেন করছি..."
                    } else {
                        "Opening $label..."
                    }
                    return DeviceActionResult(
                        isHandled = true,
                        actionType = ActionType.OPEN_APP,
                        feedbackMessage = feedback,
                        targetName = label,
                        targetIntent = intent,
                        success = true
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to launch installed app ${dynamicApp.packageName}", e)
                }
            }
        }

        // If not found installed, offer to open Play Store for that app
        val feedback = if (isBengali) {
            "আপনার ফোনে '$appNameKeyword' অ্যাপটি পাওয়া যায়নি। প্লে স্টোরে খুঁজছি..."
        } else {
            "'$appNameKeyword' is not installed. Looking it up on Play Store..."
        }
        val playStoreResult = executePlayStoreSearch(appNameKeyword, isBengali)
        return playStoreResult.copy(
            actionType = ActionType.OPEN_APP,
            feedbackMessage = feedback
        )
    }

    private fun launchSpecialOrPackage(target: String, appName: String, isBengali: Boolean): DeviceActionResult {
        try {
            val intent: Intent = when (target) {
                "camera_intent" -> Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                "gallery_intent" -> Intent(Intent.ACTION_VIEW).apply {
                    type = "image/*"
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                "settings_intent" -> Intent(Settings.ACTION_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                "calculator_intent" -> {
                    val calcIntent = context.packageManager.getLaunchIntentForPackage("com.google.android.calculator")
                        ?: context.packageManager.getLaunchIntentForPackage("com.sec.android.app.popupcalculator")
                        ?: Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_APP_CALCULATOR)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    calcIntent.apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                }
                "clock_intent" -> Intent(AlarmClock.ACTION_SHOW_ALARMS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                else -> {
                    context.packageManager.getLaunchIntentForPackage(target)?.apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    } ?: Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$target")
                    ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                }
            }

            context.startActivity(intent)
            val niceName = appName.replaceFirstChar { it.uppercase() }
            val feedback = if (isBengali) {
                "$niceName ওপেন করছি..."
            } else {
                "Opening $niceName..."
            }
            return DeviceActionResult(
                isHandled = true,
                actionType = ActionType.OPEN_APP,
                feedbackMessage = feedback,
                targetName = niceName,
                targetIntent = intent,
                success = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error launching target $target", e)
            val niceName = appName.replaceFirstChar { it.uppercase() }
            return DeviceActionResult(
                isHandled = true,
                actionType = ActionType.OPEN_APP,
                feedbackMessage = if (isBengali) "$niceName ওপেন করা সম্ভব হয়নি।" else "Could not open $niceName.",
                success = false
            )
        }
    }

    private fun findInstalledApp(query: String): android.content.pm.ApplicationInfo? {
        return try {
            val pm = context.packageManager
            val installed = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            val lowerQuery = query.lowercase().replace(" ", "")

            installed.firstOrNull { app ->
                val label = app.loadLabel(pm).toString().lowercase().replace(" ", "")
                val pkg = app.packageName.lowercase()
                label == lowerQuery || label.contains(lowerQuery) || pkg.contains(lowerQuery)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error searching installed apps", e)
            null
        }
    }

    private fun isIntentAvailable(intent: Intent): Boolean {
        return try {
            val activities = context.packageManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            activities.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
}
