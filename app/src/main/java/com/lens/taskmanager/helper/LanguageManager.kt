package com.lens.taskmanager.helper

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object LanguageManager {
    private const val PREF_LANG_KEY = "pref_lang_key"
    private const val PREF_DARK_MODE_KEY = "pref_dark_mode_key"
    private const val DEFAULT_LANG = "en"
    private const val DEFAULT_DARK_MODE = false

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun saveLanguage(language: String, context: Context) {
        val prefs = getSharedPreferences(context)
        prefs.edit().putString(PREF_LANG_KEY, language).apply()
    }

    fun getLanguage(context: Context): String {
        val prefs = getSharedPreferences(context)
        return prefs.getString(PREF_LANG_KEY, DEFAULT_LANG) ?: DEFAULT_LANG
    }

    fun saveDarkModeEnabled(isDarkModeEnabled: Boolean, context: Context) {
        val prefs = getSharedPreferences(context)
        prefs.edit().putBoolean(PREF_DARK_MODE_KEY, isDarkModeEnabled).apply()
    }

    fun isDarkModeEnabled(context: Context): Boolean {
        val prefs = getSharedPreferences(context)
        return prefs.getBoolean(PREF_DARK_MODE_KEY, DEFAULT_DARK_MODE)
    }
}
