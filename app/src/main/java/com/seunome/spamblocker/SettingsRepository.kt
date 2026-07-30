package com.seunome.spamblocker

import android.content.Context

class SettingsRepository(context: Context) {

    private val prefs = context.getSharedPreferences("spam_blocker_prefs", Context.MODE_PRIVATE)

    fun isBlockingEnabled(): Boolean {
        return prefs.getBoolean(KEY_BLOCK_UNKNOWN, true)
    }

    fun setBlockingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BLOCK_UNKNOWN, enabled).apply()
    }

    fun shouldSkipCallLog(): Boolean {
        return prefs.getBoolean(KEY_SKIP_CALL_LOG, false)
    }

    fun setSkipCallLog(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SKIP_CALL_LOG, enabled).apply()
    }

    fun shouldSkipNotification(): Boolean {
        return prefs.getBoolean(KEY_SKIP_NOTIFICATION, false)
    }

    fun setSkipNotification(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SKIP_NOTIFICATION, enabled).apply()
    }

    companion object {
        private const val KEY_BLOCK_UNKNOWN = "block_unknown"
        private const val KEY_SKIP_CALL_LOG = "skip_call_log"
        private const val KEY_SKIP_NOTIFICATION = "skip_notification"
    }
}
