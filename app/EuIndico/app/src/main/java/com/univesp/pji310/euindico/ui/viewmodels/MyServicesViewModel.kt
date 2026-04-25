package com.univesp.pji310.euindico.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univesp.pji310.euindico.data.local.UserPreferences
import com.univesp.pji310.euindico.data.model.AddProfessionRequest
import com.univesp.pji310.euindico.data.model.MyProfessionResult
import com.univesp.pji310.euindico.data.model.Profession
import com.univesp.pji310.euindico.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MyServicesState {
    object Loading : MyServicesState()
    data class Success(
        val myProfessions: List<MyProfessionResult>,
        val allCategories: List<Profession>
    ) : MyServicesState()
    data class Error(val message: String) : MyServicesState()
}

class MyServicesViewModel(private val userPreferences: UserPreferences) : ViewModel() {
    private val apiService = ApiClient.apiService
    
    private val _state = MutableStateFlow<MyServicesState>(MyServicesState.Loading)
    val state: StateFlow<MyServicesState> = _state

    init {
        loadData()
    }

    fun loadData() {
        val activeUsername = userPreferences.getUsername() ?: return
        _state.value = MyServicesState.Loading

        viewModelScope.launch {
            try {
                // Fetch user specific professions
                val profResponse = apiService.getUserProfessions(activeUsername)
                // Fetch all categories for the dropdown
                val catResponse = apiService.getProfessions()

                if (profResponse.isSuccessful && catResponse.isSuccessful) {
                    _state.value = MyServicesState.Success(
                        myProfessions = profResponse.body()?.data ?: emptyList(),
                        allCategories = catResponse.body()?.data ?: emptyList()
                    )
                } else {
                    _state.value = MyServicesState.Error("Falha ao carregar serviços")
                }
            } catch (e: Exception) {
                _state.value = MyServicesState.Error(e.localizedMessage ?: "Erro de rede")
            }
        }
    }

    fun addProfession(professionName: String) {
        val activeUsername = userPreferences.getUsername() ?: return
        val currentState = _state.value
        
        if (currentState is MyServicesState.Success) {
            val profId = currentState.allCategories.find { it.nome == professionName }?.id
            
            if (profId != null) {
                _state.value = MyServicesState.Loading
                viewModelScope.launch {
                    try {
                        val response = apiService.addUserProfession(
                            username = activeUsername,
                            request = AddProfessionRequest(id = profId)
                        )
                        if (response.isSuccessful) {
                            loadData() // Refresh list
                        } else {
                            _state.value = MyServicesState.Error("Falha ao adicionar serviço")
                            loadData() // Re-load to show old state
                        }
                    } catch (e: Exception) {
                        _state.value = MyServicesState.Error(e.localizedMessage ?: "Erro de rede")
                        loadData()
                    }
                }
            }
        }
    }
}
