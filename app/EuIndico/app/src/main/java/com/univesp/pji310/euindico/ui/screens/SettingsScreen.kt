package com.univesp.pji310.euindico.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.univesp.pji310.euindico.R

import com.univesp.pji310.euindico.ui.viewmodels.SettingsState
import com.univesp.pji310.euindico.ui.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onLogout: () -> Unit, onNavigateToEditProfile: () -> Unit) {
    val state by viewModel.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopSearchBar() // Reusing the identical top bar

        LazyColumn(
            contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 100.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = "Configurações",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // User Profile Header
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(0.5.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            androidx.compose.foundation.Image(
                                painter = painterResource(id = R.drawable.logo_v1_peq),
                                contentDescription = null,
                                modifier = Modifier.size(64.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer)
                            )
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp).align(Alignment.BottomEnd).background(Color.White, CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("MINHA CONTA", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
                            
                            when (state) {
                                is SettingsState.Loading -> {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp).padding(vertical = 4.dp))
                                }
                                is SettingsState.Success -> {
                                    val user = (state as SettingsState.Success).profile
                                    val cleanName = user.nome?.substringBefore(" %") ?: "Usuário"
                                    Text(cleanName, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                    Text(user.email ?: user.username ?: "Sem Email", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                is SettingsState.Error -> {
                                    Text("Erro ao carregar", fontSize = 14.sp, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        IconButton(onClick = onNavigateToEditProfile) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }

            // App Preferences
            item {
                SectionHeader(icon = Icons.Default.Settings, title = "Preferências do App")
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(0.5.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                ) {
                    Column {
                        PreferenceRow(Icons.Default.Palette, "Tema", "Padrão do sistema (Claro)")
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f))
                        PreferenceRow(Icons.Default.Notifications, "Notificações", "Ativadas • Resumos diários", showToggle = true)
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f))
                        PreferenceRow(Icons.Default.Language, "Idioma", "Português (Brasil)")
                    }
                }
            }

            // API Configuration
            item {
                SectionHeader(icon = Icons.Default.Terminal, title = "Configuração de API")
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
                ) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        ApiConfigRow("URL DO ENDPOINT", "https://api.editorial-precision.io/v1/sync", Icons.Default.Edit)
                        ApiConfigRow("TOKEN DE AUTENTICAÇÃO", "Ativo • Válido até Out 2025", Icons.Default.Refresh, isToken = true)
                    }
                }
            }

            // Logout Button
            item {
                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sair da Conta", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.error, fontSize = 16.sp)
                }

                Text(
                    text = "VERSÃO 2.4.12-EUINDICO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    letterSpacing = 1.sp,
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp, start = 8.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun PreferenceRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, showToggle: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { }.padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        if (showToggle) {
            Switch(
                checked = true, 
                onCheckedChange = null,
                colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.secondary, checkedTrackColor = MaterialTheme.colorScheme.primaryContainer)
            )
        } else {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
        }
    }
}

@Composable
fun ApiConfigRow(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isToken: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp).background(Color.White, RoundedCornerShape(8.dp)).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                if(isToken) {
                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF10B981), CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(subtitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(top = 2.dp))
            }
        }
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.outlineVariant)
    }
}
