package com.univesp.pji310.euindico.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.univesp.pji310.euindico.data.local.UserPreferences
import com.univesp.pji310.euindico.data.remote.ApiClient

class ViewModelFactory(private val userPreferences: UserPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val apiService = ApiClient.apiService
        
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(userPreferences, apiService) as T
        }
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(userPreferences, apiService) as T
        }
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(userPreferences, apiService) as T
        }
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(apiService) as T
        }
        if (modelClass.isAssignableFrom(MyServicesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyServicesViewModel(userPreferences, apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
