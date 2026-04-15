package com.sewasms.utils

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {

    private const val PREF_DARK_MODE = "dark_mode"

    fun initializeTheme(context: Context) {
        val isDarkMode = isDarkModeEnabled(context)
        applyTheme(isDarkMode)
    }

    fun toggleTheme(context: Context) {
        val isDarkMode = isDarkModeEnabled(context)
        setDarkMode(context, !isDarkMode)
        applyTheme(!isDarkMode)
    }

    private fun applyTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun isDarkModeEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            prefs.getBoolean(PREF_DARK_MODE, false)
        } else {
            false
        }
    }

    private fun setDarkMode(context: Context, isDarkMode: Boolean) {
        val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean(PREF_DARK_MODE, isDarkMode).apply()
    }
}
