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
class MyServicesViewModelTest {

    private lateinit var viewModel: MyServicesViewModel
    private val userPreferences: UserPreferences = mockk(relaxed = true)
    private val apiService: ApiService = mockk()
    
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Default mocks for init
        every { userPreferences.getUsername() } returns "user@test.com"
        coEvery { apiService.getUserProfessions(any()) } returns Response.success(DataWrapper(success = true, data = emptyList()))
        coEvery { apiService.getProfessions() } returns Response.success(DataWrapper(success = true, data = emptyList()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadData updates state with user professions and all categories`() = runTest {
        val userProfessions = listOf(MyProfessionResult(id = 1, nome = "Pedreiro"))
        val allCategories = listOf(Profession(id = 1, nome = "Pedreiro"))
        
        coEvery { apiService.getUserProfessions("user@test.com") } returns Response.success(DataWrapper(success = true, data = userProfessions))
        coEvery { apiService.getProfessions() } returns Response.success(DataWrapper(success = true, data = allCategories))
        
        viewModel = MyServicesViewModel(userPreferences, apiService)
        
        val currentState = viewModel.state.value
        assert(currentState is MyServicesState.Success)
        val successState = currentState as MyServicesState.Success
        assertEquals(userProfessions, successState.myProfessions)
        assertEquals(allCategories, successState.allCategories)
    }

    @Test
    fun `addProfession calls API and refreshes data`() = runTest {
        val allCategories = listOf(Profession(id = 10, nome = "Eletricista"))
        coEvery { apiService.getProfessions() } returns Response.success(DataWrapper(success = true, data = allCategories))
        coEvery { apiService.addUserProfession(any(), any()) } returns Response.success(GenericResponse(success = true, message = "OK"))
        
        viewModel = MyServicesViewModel(userPreferences, apiService)
        
        viewModel.addProfession("Eletricista")
        
        coVerify { apiService.addUserProfession("user@test.com", any()) }
        assertEquals("Serviço adicionado com sucesso!", viewModel.actionMessage.value)
        coVerify(exactly = 2) { apiService.getUserProfessions(any()) } // Once in init, once after add
    }

    @Test
    fun `addProfession shows error when profession not found`() = runTest {
        val allCategories = listOf(Profession(id = 10, nome = "Eletricista"))
        coEvery { apiService.getProfessions() } returns Response.success(DataWrapper(success = true, data = allCategories))
        
        viewModel = MyServicesViewModel(userPreferences, apiService)
        
        viewModel.addProfession("Encanador") // Not in categories
        
        coVerify(exactly = 0) { apiService.addUserProfession(any(), any()) }
    }
}
