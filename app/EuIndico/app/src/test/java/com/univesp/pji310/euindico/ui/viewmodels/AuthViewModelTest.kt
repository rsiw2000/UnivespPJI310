package com.univesp.pji310.euindico.ui.viewmodels

import com.univesp.pji310.euindico.data.local.UserPreferences
import com.univesp.pji310.euindico.data.model.LoginRequest
import com.univesp.pji310.euindico.data.remote.ApiService
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var viewModel: AuthViewModel
    private val userPreferences: UserPreferences = mockk(relaxed = true)
    private val apiService: ApiService = mockk()
    
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is Success when username exists`() {
        every { userPreferences.getUsername() } returns "test_user"
        
        viewModel = AuthViewModel(userPreferences, apiService)
        
        assertEquals(AuthState.Success, viewModel.authState.value)
    }

    @Test
    fun `initial state is Idle when username is null`() {
        every { userPreferences.getUsername() } returns null
        
        viewModel = AuthViewModel(userPreferences, apiService)
        
        assertEquals(AuthState.Idle, viewModel.authState.value)
    }

    @Test
    fun `login success updates state and saves session`() = runTest {
        val username = "user@test.com"
        val password = "password"
        val response = Response.success(mockk<com.univesp.pji310.euindico.data.model.GenericResponse>())
        
        coEvery { apiService.login(any()) } returns response
        every { userPreferences.getUsername() } returns null
        
        viewModel = AuthViewModel(userPreferences, apiService)
        viewModel.login(username, password)
        
        assertEquals(AuthState.Success, viewModel.authState.value)
        verify { userPreferences.saveUsername(username) }
    }

    @Test
    fun `login failure updates state with error`() = runTest {
        val response = Response.error<com.univesp.pji310.euindico.data.model.GenericResponse>(
            401, 
            "".toResponseBody(null)
        )
        
        coEvery { apiService.login(any()) } returns response
        every { userPreferences.getUsername() } returns null
        
        viewModel = AuthViewModel(userPreferences, apiService)
        viewModel.login("user", "pass")
        
        assert(viewModel.authState.value is AuthState.Error)
        val errorState = viewModel.authState.value as AuthState.Error
        assert(errorState.message.contains("401"))
    }

    @Test
    fun `logout clears preferences and updates state`() = runTest {
        coEvery { apiService.logout() } returns Response.success(mockk())
        every { userPreferences.getUsername() } returns "user"
        
        viewModel = AuthViewModel(userPreferences, apiService)
        viewModel.logout()
        
        assertEquals(AuthState.Idle, viewModel.authState.value)
        verify { userPreferences.clear() }
    }
}
