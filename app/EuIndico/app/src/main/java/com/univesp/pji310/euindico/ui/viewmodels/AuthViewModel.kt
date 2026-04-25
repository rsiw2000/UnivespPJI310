package com.univesp.pji310.euindico.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univesp.pji310.euindico.data.local.UserPreferences
import com.univesp.pji310.euindico.data.model.LoginRequest
import com.univesp.pji310.euindico.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    private val apiService = ApiClient.apiService

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // Helper to check if already logged in via Session
    init {
        if (userPreferences.getUsername() != null) {
            _authState.value = AuthState.Success
        }
    }

    fun clearState() {
        _authState.value = AuthState.Idle
    }

    fun login(username: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val request = LoginRequest(username = username, password = password)
                val response = apiService.login(request)
                
                if (response.isSuccessful) {
                    // Save session
                    userPreferences.saveUsername(username)
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error("Falha na autenticação: ${response.code()}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.localizedMessage ?: "Erro de conexão")
            }
        }
    }

    fun logout() {
        userPreferences.clear()
        _authState.value = AuthState.Idle
        viewModelScope.launch {
            try {
                apiService.logout()
            } catch (e: Exception) {
                // Ignore logout call failure, we clear local session anyway
            }
        }
    }
}
