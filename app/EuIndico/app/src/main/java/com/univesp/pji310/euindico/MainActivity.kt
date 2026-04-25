package com.univesp.pji310.euindico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.univesp.pji310.euindico.data.local.UserPreferences
import com.univesp.pji310.euindico.navigation.AppNavGraph
import com.univesp.pji310.euindico.ui.theme.EuIndicoTheme
import com.univesp.pji310.euindico.ui.viewmodels.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val userPreferences = UserPreferences(applicationContext)
        val viewModelFactory = ViewModelFactory(userPreferences)

        enableEdgeToEdge()
        setContent {
            EuIndicoTheme {
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