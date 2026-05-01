package com.univesp.pji310.euindico.data.local

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("eu_indico_prefs", Context.MODE_PRIVATE)

    fun saveUsername(username: String) {
        prefs.edit().putString("USERNAME", username).apply()
    }

    fun getUsername(): String? {
        return prefs.getString("USERNAME", null)
    }

    fun saveTheme(isDarkMode: Boolean?) {
        val editor = prefs.edit()
        if (isDarkMode == null) {
            editor.remove("THEME_DARK")
        } else {
            editor.putBoolean("THEME_DARK", isDarkMode)
        }
        editor.apply()
    }

    /**
     * Returns null for system default, true for dark, false for light.
     */
    fun getTheme(): Boolean? {
        return if (!prefs.contains("THEME_DARK")) null else prefs.getBoolean("THEME_DARK", false)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
