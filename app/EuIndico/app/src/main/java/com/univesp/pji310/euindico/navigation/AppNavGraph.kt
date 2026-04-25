package com.univesp.pji310.euindico.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.univesp.pji310.euindico.ui.components.AppBottomNavigation
import com.univesp.pji310.euindico.ui.screens.LoginScreen
import com.univesp.pji310.euindico.ui.screens.RegisterScreen
import com.univesp.pji310.euindico.ui.screens.SearchScreen
import com.univesp.pji310.euindico.ui.screens.MyServicesScreen
import com.univesp.pji310.euindico.ui.screens.SettingsScreen
import com.univesp.pji310.euindico.ui.screens.EditProfileScreen

import androidx.lifecycle.viewmodel.compose.viewModel
import com.univesp.pji310.euindico.data.local.UserPreferences
import com.univesp.pji310.euindico.ui.viewmodels.AuthViewModel
import com.univesp.pji310.euindico.ui.viewmodels.SearchViewModel
import com.univesp.pji310.euindico.ui.viewmodels.MyServicesViewModel
import com.univesp.pji310.euindico.ui.viewmodels.SettingsViewModel
import com.univesp.pji310.euindico.ui.viewmodels.RegisterViewModel
import com.univesp.pji310.euindico.ui.viewmodels.ViewModelFactory

@Composable
fun AppNavGraph(viewModelFactory: ViewModelFactory, userPreferences: UserPreferences) {
    val navController = rememberNavController()

    // Create ViewModels
    val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
    val searchViewModel: SearchViewModel = viewModel(factory = viewModelFactory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
    val myServicesViewModel: MyServicesViewModel = viewModel(factory = viewModelFactory)
    val registerViewModel: RegisterViewModel = viewModel(factory = viewModelFactory)

    // Determine Start Destination
    val startDest = if (userPreferences.getUsername() != null) Screen.Dashboard.route else Screen.Login.route

    // Screens that should show the Bottom Navigation
    val bottomBarRoutes = listOf(
        Screen.Dashboard.route,
        Screen.Search.route,
        Screen.MyServices.route,
        Screen.Settings.route,
        Screen.EditProfile.route
    )

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    Scaffold(
        bottomBar = {
            if (bottomBarRoutes.contains(currentRoute)) {
                AppBottomNavigation(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = { 
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        } 
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    viewModel = registerViewModel,
                    onRegisterSuccess = { navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    } },
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    } }
                )
            }
            composable(Screen.Dashboard.route) {
                com.univesp.pji310.euindico.ui.screens.DashboardHomeScreen(
                    onNavigateToSearch = { 
                        navController.navigate(Screen.Search.route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToMyServices = { 
                        navController.navigate(Screen.MyServices.route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToSettings = { 
                        navController.navigate(Screen.Settings.route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(viewModel = searchViewModel)
            }
            composable(Screen.MyServices.route) {
                MyServicesScreen(
                    viewModel = myServicesViewModel,
                    onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onLogout = { 
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        } 
                    },
                    onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) }
                )
            }
            composable(Screen.EditProfile.route) {
                EditProfileScreen(
                    viewModel = settingsViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
