package com.univesp.pji310.euindico.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.univesp.pji310.euindico.R

import com.univesp.pji310.euindico.ui.viewmodels.SettingsViewModel
import com.univesp.pji310.euindico.ui.viewmodels.SettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var neighborhood by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val vmState by viewModel.state.collectAsState()

    LaunchedEffect(vmState) {
        if (vmState is SettingsState.Success) {
            val user = (vmState as SettingsState.Success).profile
            name = user.nome?.substringBefore(" %") ?: ""
            email = user.email ?: user.username ?: ""
            phone = user.telefone ?: ""
            state = user.estado ?: ""
            city = user.cidade?.toString() ?: ""
            neighborhood = user.bairro ?: ""
            cpf = user.cpfCnpj?.toString() ?: ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.8f))
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.secondary)
            }
            Text("Eu Indico!", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.width(48.dp)) // To keep title centered
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            // Logo & Title
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.logo_v1_peq),
                        contentDescription = "Eu Indico Logo",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Editar Cadastro", fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                Text("Mantenha seus dados atualizados.", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp, bottom = 32.dp))
            }

            // Form
            InputLabel("Nome Completo")
            BasicTextField(value = name, onValueChange = { name = it }, placeholder = "Seu nome completo")

            Spacer(modifier = Modifier.height(16.dp))
            InputLabel("Email")
            BasicTextField(value = email, onValueChange = { email = it }, placeholder = "exemplo@email.com")

            Spacer(modifier = Modifier.height(16.dp))
            InputLabel("CPF/CNPJ")
            BasicTextField(value = cpf, onValueChange = { cpf = it }, placeholder = "000.000.000-00")

            Spacer(modifier = Modifier.height(16.dp))
            InputLabel("Telefone")
            BasicTextField(value = phone, onValueChange = { phone = it }, placeholder = "(00) 00000-0000")

            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    InputLabel("Estado")
                    DropdownField(value = state, onValueChange = { state = it }, options = listOf("SP", "RJ", "MG"))
                }
                Column(modifier = Modifier.weight(1f)) {
                    InputLabel("Cidade")
                    DropdownField(value = city, onValueChange = { city = it }, options = listOf("São Paulo", "São José dos Campos"))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            InputLabel("Bairro")
            BasicTextField(value = neighborhood, onValueChange = { neighborhood = it }, placeholder = "Digite seu bairro")

            Spacer(modifier = Modifier.height(16.dp))
            InputLabel("Senha")
            BasicTextField(value = password, onValueChange = { password = it }, placeholder = "••••••••", isPassword = true)

            Spacer(modifier = Modifier.height(16.dp))
            InputLabel("Confirme a senha")
            BasicTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, placeholder = "••••••••", isPassword = true)

            Spacer(modifier = Modifier.height(32.dp))

            // CTA Button
            val brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))
            var isSaving by remember { mutableStateOf(false) }
            Button(
                onClick = { 
                    isSaving = true
                    viewModel.updateProfile(
                        nome = name,
                        telefone = phone,
                        estado = state,
                        cidadeStr = city,
                        bairro = neighborhood
                    ) { success ->
                        isSaving = false
                        if (success) {
                            onBack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(brush, RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Salvar Alterações", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 1.sp)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
            Text(
                "EU INDICO! © 2024 - SISTEMA DE INDICAÇÕES",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
            )
        }
    }
}
