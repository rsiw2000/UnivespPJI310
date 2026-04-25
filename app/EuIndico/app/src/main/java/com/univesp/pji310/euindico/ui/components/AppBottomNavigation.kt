package com.univesp.pji310.euindico.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.univesp.pji310.euindico.navigation.Screen
import com.univesp.pji310.euindico.ui.theme.md_theme_light_secondary

@Composable
fun AppBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem("Início", Icons.Default.Home, Screen.Dashboard.route), // Mapped to Dashboard Home
        BottomNavItem("Busca", Icons.Default.Search, Screen.Search.route),
        BottomNavItem("Perfil", Icons.Default.Person, Screen.MyServices.route),
        BottomNavItem("Config.", Icons.Default.Settings, Screen.Settings.route)
    )

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar(
        modifier = Modifier
            .shadow(16.dp, ambientColor = Color.Black.copy(alpha = 0.05f))
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        containerColor = Color.White.copy(alpha = 0.9f),
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                selected = isSelected,
                onClick = {
                    if (item.route != null && item.route != currentRoute) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Dashboard.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = md_theme_light_secondary,
                    selectedTextColor = md_theme_light_secondary,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFFEFF4FF)
                )
            )
        }
    }
}

data class BottomNavItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String?
)
