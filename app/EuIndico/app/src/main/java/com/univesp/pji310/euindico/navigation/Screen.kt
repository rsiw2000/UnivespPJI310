package com.univesp.pji310.euindico.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Search : Screen("search")
    object MyServices : Screen("my_services")
    object Settings : Screen("settings")
    object EditProfile : Screen("edit_profile")
}
