package com.univesp.pji310.euindico.ui.viewmodels

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
class RegisterViewModelTest {

    private lateinit var viewModel: RegisterViewModel
    private val apiService: ApiService = mockk()
    
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Mock getStates for init call in ViewModel
        coEvery { apiService.getStates() } returns Response.success(DataWrapper(success = true, data = emptyList()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `register success updates state to Success`() = runTest {
        val request = RegisterRequest(
            nome = "User",
            email = "user@test.com",
            password = "pass",
            confirmPassword = "pass",
            cpfCnpj = "12345678901",
            telefone = "11999999999",
            estado = "SP",
            cidade = 1,
            bairro = "Bairro"
        )
        val response = Response.success(GenericResponse(success = true, message = "OK"))
        
        coEvery { apiService.register(any()) } returns response
        
        viewModel = RegisterViewModel(apiService)
        viewModel.register(request)
        
        assertEquals(RegisterState.Success, viewModel.state.value)
    }

    @Test
    fun `register failure updates state to Error`() = runTest {
        val request = RegisterRequest(
            nome = "User",
            email = "user@test.com",
            password = "pass",
            confirmPassword = "pass",
            cpfCnpj = "12345678901",
            telefone = "11999999999",
            estado = "SP",
            cidade = 1,
            bairro = "Bairro"
        )
        val response = Response.success(GenericResponse(success = false, message = "Email already exists"))
        
        coEvery { apiService.register(any()) } returns response
        
        viewModel = RegisterViewModel(apiService)
        viewModel.register(request)
        
        assert(viewModel.state.value is RegisterState.Error)
        assertEquals("Email already exists", (viewModel.state.value as RegisterState.Error).message)
    }

    @Test
    fun `loadStates updates statesList`() = runTest {
        val states = listOf(StateResponse("SP", "São Paulo"))
        val response = Response.success(DataWrapper(success = true, data = states))
        
        coEvery { apiService.getStates() } returns response
        
        viewModel = RegisterViewModel(apiService)
        // loadStates is called in init
        
        assertEquals(states, viewModel.statesList.value)
    }
}
