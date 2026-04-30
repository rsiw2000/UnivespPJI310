package com.univesp.pji310.euindico.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univesp.pji310.euindico.data.local.UserPreferences
import com.univesp.pji310.euindico.data.model.*
import com.univesp.pji310.euindico.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SettingsState {
    object Loading : SettingsState()
    data class Success(val profile: UserProfile) : SettingsState()
    data class Error(val message: String) : SettingsState()
}

class SettingsViewModel(private val userPreferences: UserPreferences) : ViewModel() {
    private val apiService = ApiClient.apiService
    
    private val _state = MutableStateFlow<SettingsState>(SettingsState.Loading)
    val state: StateFlow<SettingsState> = _state
    
    private val _statesList = MutableStateFlow<List<StateResponse>>(emptyList())
    val statesList: StateFlow<List<StateResponse>> = _statesList

    private val _citiesList = MutableStateFlow<List<CityResponse>>(emptyList())
    val citiesList: StateFlow<List<CityResponse>> = _citiesList

    private val _isDarkMode = MutableStateFlow<Boolean?>(userPreferences.getTheme())
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode

    init {
        loadProfile()
        loadStates()
    }

    fun setTheme(isDark: Boolean?) {
        userPreferences.saveTheme(isDark)
        _isDarkMode.value = isDark
    }

    fun loadProfile() {
        val activeUsername = userPreferences.getUsername() ?: return
        _state.value = SettingsState.Loading

        viewModelScope.launch {
            try {
                val response = apiService.getUserProfile(activeUsername)
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.user
                    if (user != null) {
                        _state.value = SettingsState.Success(user)
                    } else {
                        _state.value = SettingsState.Error("Usuário não encontrado")
                    }
                } else {
                    _state.value = SettingsState.Error("Falha ao carregar perfil")
                }
            } catch (e: Exception) {
                _state.value = SettingsState.Error(e.localizedMessage ?: "Erro de rede")
            }
        }
    }

    fun loadStates() {
        viewModelScope.launch {
            try {
                val response = apiService.getStates()
                if (response.isSuccessful) {
                    _statesList.value = response.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                // Silently fail or log
            }
        }
    }

    fun loadCities(uf: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getCities(uf)
                if (response.isSuccessful) {
                    _citiesList.value = response.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                _citiesList.value = emptyList()
            }
        }
    }

    fun updateProfile(
        nome: String,
        telefone: String,
        estado: String,
        cidadeId: Int,
        bairro: String,
        onComplete: (Boolean) -> Unit
    ) {
        val activeUsername = userPreferences.getUsername() ?: return
        viewModelScope.launch {
            try {
                val request = com.univesp.pji310.euindico.data.model.UpdateProfileRequest(
                    nome = nome,
                    telefone = telefone,
                    estado = estado,
                    cidade = cidadeId,
                    bairro = bairro
                )
                val response = apiService.updateUserProfile(activeUsername, request)
                if (response.isSuccessful) {
                    loadProfile() // refresh state
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }
}
