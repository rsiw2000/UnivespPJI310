package com.univesp.pji310.euindico.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univesp.pji310.euindico.data.model.*
import com.univesp.pji310.euindico.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel : ViewModel() {
    private val apiService = ApiClient.apiService

    private val _state = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val state: StateFlow<RegisterState> = _state

    private val _statesList = MutableStateFlow<List<StateResponse>>(emptyList())
    val statesList: StateFlow<List<StateResponse>> = _statesList

    private val _citiesList = MutableStateFlow<List<CityResponse>>(emptyList())
    val citiesList: StateFlow<List<CityResponse>> = _citiesList

    init {
        loadStates()
    }

    fun clearState() {
        _state.value = RegisterState.Idle
    }

    fun loadStates() {
        viewModelScope.launch {
            try {
                val response = apiService.getStates()
                if (response.isSuccessful) {
                    _statesList.value = response.body()?.data ?: emptyList()
                }
            } catch (e: Exception) {
                // Fail silently
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

    fun register(request: RegisterRequest) {
        _state.value = RegisterState.Loading
        viewModelScope.launch {
            try {
                val response = apiService.register(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    _state.value = RegisterState.Success
                } else {
                    _state.value = RegisterState.Error(response.body()?.message ?: "Falha ao criar conta")
                }
            } catch (e: Exception) {
                _state.value = RegisterState.Error(e.localizedMessage ?: "Erro de conexão")
            }
        }
    }
}
