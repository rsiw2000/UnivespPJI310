package com.univesp.pji310.euindico.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univesp.pji310.euindico.data.local.UserPreferences
import com.univesp.pji310.euindico.data.model.Profession
import com.univesp.pji310.euindico.data.model.ProfessionalResult
import com.univesp.pji310.euindico.data.remote.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SearchState {
    object Loading : SearchState()
    data class Success(
        val professionals: List<ProfessionalResult>, 
        val categories: List<Profession>
    ) : SearchState()
    data class Error(val message: String) : SearchState()
}

class SearchViewModel(private val userPreferences: UserPreferences) : ViewModel() {

    private val apiService = ApiClient.apiService
    
    private val _state = MutableStateFlow<SearchState>(SearchState.Loading)
    val state: StateFlow<SearchState> = _state

    private var allCategories: List<Profession> = emptyList()

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        _state.value = SearchState.Loading
        viewModelScope.launch {
            try {
                // Fetch categories
                val catResponse = apiService.getProfessions()
                val activeUsername = userPreferences.getUsername() ?: ""
                
                if (catResponse.isSuccessful) {
                    allCategories = catResponse.body()?.data ?: emptyList()
                    
                    // Fetch all professionals, for now we mock idProfissao=17 as requested in rest if no param, or just fetch all
                    // We will fetch 17 as default or first category
                    val defaultProfId = allCategories.firstOrNull()?.id ?: 17
                    val searchResponse = apiService.searchProfessionals(activeUsername, defaultProfId)
                    
                    if (searchResponse.isSuccessful) {
                        _state.value = SearchState.Success(
                            professionals = searchResponse.body()?.data ?: emptyList(),
                            categories = allCategories
                        )
                    } else {
                        _state.value = SearchState.Error("Falha ao buscar profissionais. ${searchResponse.code()}")
                    }
                } else {
                    _state.value = SearchState.Error("Falha ao buscar categorias. ${catResponse.code()}")
                }
            } catch (e: Exception) {
                 _state.value = SearchState.Error(e.localizedMessage ?: "Erro de rede")
            }
        }
    }

    fun searchByProfession(professionName: String) {
        val activeUsername = userPreferences.getUsername() ?: return
        val currentState = _state.value
        
        if (currentState is SearchState.Success) {
            val allCategories = currentState.categories
            val profId = allCategories.find { it.nome.equals(professionName, ignoreCase = true) }?.id
            
            if (profId != null) {
                _state.value = SearchState.Loading
                
                viewModelScope.launch {
                    try {
                        val searchResponse = apiService.searchProfessionals(activeUsername, profId)
                        if (searchResponse.isSuccessful) {
                            _state.value = SearchState.Success(
                                professionals = searchResponse.body()?.data ?: emptyList(),
                                categories = allCategories
                            )
                        } else {
                            _state.value = SearchState.Error("Falha na busca")
                        }
                    } catch (e: Exception) {
                        _state.value = SearchState.Error(e.localizedMessage ?: "Erro de rede")
                    }
                }
            } else {
                _state.value = SearchState.Success(emptyList(), allCategories)
            }
        }
    }

    fun submitReview(idProfissao: Int, idPrestador: Int, avaliacao: Int, comentario: String, onComplete: (Boolean) -> Unit) {
        val activeUsername = userPreferences.getUsername() ?: return
        viewModelScope.launch {
            try {
                val request = com.univesp.pji310.euindico.data.model.ReviewRequest(
                    idProfissao = idProfissao,
                    idPrestador = idPrestador,
                    avaliacao = avaliacao,
                    comentario = comentario
                )
                val response = apiService.createReview(activeUsername, request)
                onComplete(response.isSuccessful)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }
}
