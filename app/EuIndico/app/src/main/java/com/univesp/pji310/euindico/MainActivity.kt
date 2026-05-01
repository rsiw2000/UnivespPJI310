package com.univesp.pji310.euindico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.univesp.pji310.euindico.data.local.UserPreferences
import com.univesp.pji310.euindico.navigation.AppNavGraph
import com.univesp.pji310.euindico.ui.theme.EuIndicoTheme
import com.univesp.pji310.euindico.ui.viewmodels.SettingsViewModel
import com.univesp.pji310.euindico.ui.viewmodels.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val userPreferences = UserPreferences(applicationContext)
        val viewModelFactory = ViewModelFactory(userPreferences)
        val settingsViewModel = viewModelFactory.create(SettingsViewModel::class.java)

        enableEdgeToEdge()
        setContent {
            val isDarkModeOverride by settingsViewModel.isDarkMode.collectAsState()
            val darkTheme = isDarkModeOverride ?: isSystemInDarkTheme()

            EuIndicoTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph(viewModelFactory = viewModelFactory, userPreferences = userPreferences)
                }
            }
        }
    }
}