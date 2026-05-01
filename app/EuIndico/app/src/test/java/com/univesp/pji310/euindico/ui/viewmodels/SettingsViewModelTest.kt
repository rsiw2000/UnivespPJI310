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
class SettingsViewModelTest {

    private lateinit var viewModel: SettingsViewModel
    private val userPreferences: UserPreferences = mockk(relaxed = true)
    private val apiService: ApiService = mockk()
    
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Default mocks for init
        every { userPreferences.getUsername() } returns "user@test.com"
        every { userPreferences.getTheme() } returns false
        coEvery { apiService.getUserProfile(any()) } returns Response.success(UserResponse(success = true, user = mockk()))
        coEvery { apiService.getStates() } returns Response.success(DataWrapper(success = true, data = emptyList()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadProfile updates state on success`() = runTest {
        val userProfile = UserProfile(id = 1, nome = "Test User", email = "user@test.com", telefone = "123", estado = "SP", cidade = 1, bairro = "Bairro")
        coEvery { apiService.getUserProfile("user@test.com") } returns Response.success(UserResponse(success = true, user = userProfile))
        
        viewModel = SettingsViewModel(userPreferences, apiService)
        
        val currentState = viewModel.state.value
        assert(currentState is SettingsState.Success)
        assertEquals(userProfile, (currentState as SettingsState.Success).profile)
    }

    @Test
    fun `updateProfile calls API and refreshes profile`() = runTest {
        coEvery { apiService.updateUserProfile(any(), any()) } returns Response.success(mockk())
        coEvery { apiService.getUserProfile(any()) } returns Response.success(UserResponse(success = true, user = mockk()))
        
        viewModel = SettingsViewModel(userPreferences, apiService)
        
        var result = false
        viewModel.updateProfile("New Name", "123", "SP", 1, "New Bairro", onComplete = { result = it })
        
        assert(result)
        coVerify { apiService.updateUserProfile(any(), any()) }
        coVerify(exactly = 2) { apiService.getUserProfile(any()) } // Once in init, once after update
    }

    @Test
    fun `setTheme updates preferences and state`() {
        viewModel = SettingsViewModel(userPreferences, apiService)
        
        viewModel.setTheme(true)
        
        verify { userPreferences.saveTheme(true) }
        assertEquals(true, viewModel.isDarkMode.value)
    }
}
