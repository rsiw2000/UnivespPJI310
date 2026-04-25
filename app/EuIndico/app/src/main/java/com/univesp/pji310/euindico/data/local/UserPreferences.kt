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

    fun clear() {
        prefs.edit().clear().commit()
    }
}
