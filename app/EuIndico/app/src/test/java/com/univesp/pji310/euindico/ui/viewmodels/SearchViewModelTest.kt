package com.univesp.pji310.euindico.ui.viewmodels

import com.univesp.pji310.euindico.data.local.UserPreferences
import com.univesp.pji310.euindico.data.model.*
import com.univesp.pji310.euindico.data.remote.ApiService
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel
    private val userPreferences: UserPreferences = mockk(relaxed = true)
    private val apiService: ApiService = mockk()
    
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Mock default behavior for init block
        coEvery { apiService.getProfessions() } returns Response.success(DataWrapper(success = true, data = emptyList()))
        coEvery { apiService.searchProfessionals(any(), any()) } returns Response.success(DataWrapper(success = true, data = emptyList()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadInitialData updates state with professionals and categories`() = runTest {
        val categories = listOf(Profession(id = 1, nome = "Pedreiro"))
        val professionals = listOf(ProfessionalResult(idPrestador = 1, idProfissao = 1, nome = "João", contato = "123", avaliacao = "4.5", estado = "SP", cidade = "SP", bairro = "Bairro"))
        
        coEvery { apiService.getProfessions() } returns Response.success(DataWrapper(success = true, data = categories))
        coEvery { apiService.searchProfessionals(any(), 1) } returns Response.success(DataWrapper(success = true, data = professionals))
        every { userPreferences.getUsername() } returns "user@test.com"
        
        viewModel = SearchViewModel(userPreferences, apiService)
        
        val currentState = viewModel.state.value
        assert(currentState is SearchState.Success)
        val successState = currentState as SearchState.Success
        assertEquals(categories, successState.categories)
        assertEquals(professionals, successState.professionals)
    }

    @Test
    fun `searchByProfession updates state with new professionals`() = runTest {
        val categories = listOf(Profession(id = 1, nome = "Pedreiro"))
        coEvery { apiService.getProfessions() } returns Response.success(DataWrapper(success = true, data = categories))
        coEvery { apiService.searchProfessionals(any(), any()) } returns Response.success(DataWrapper(success = true, data = emptyList()))
        every { userPreferences.getUsername() } returns "user@test.com"
        
        viewModel = SearchViewModel(userPreferences, apiService)
        
        // Mock the specific search
        val filteredProfessionals = listOf(mockk<ProfessionalResult>())
        coEvery { apiService.searchProfessionals(any(), 1) } returns Response.success(DataWrapper(success = true, data = filteredProfessionals))
        
        viewModel.searchByProfession("Pedreiro")
        
        val currentState = viewModel.state.value
        assert(currentState is SearchState.Success)
        assertEquals(filteredProfessionals, (currentState as SearchState.Success).professionals)
    }

    @Test
    fun `submitReview returns success status`() = runTest {
        coEvery { apiService.createReview(any(), any()) } returns Response.success(GenericResponse(success = true, message = "OK"))
        every { userPreferences.getUsername() } returns "user@test.com"
        
        viewModel = SearchViewModel(userPreferences, apiService)
        
        var result = false
        viewModel.submitReview(1, 1, 5, "Ótimo", onComplete = { result = it })
        
        assert(result)
    }
}
