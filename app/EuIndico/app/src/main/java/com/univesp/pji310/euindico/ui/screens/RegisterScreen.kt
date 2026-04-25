package com.univesp.pji310.euindico.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.univesp.pji310.euindico.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onNavigateToLogin: () -> Unit) {
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
    var agreed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Logo Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.8f))
                .height(64.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.logo_v1_peq),
                contentDescription = "Eu Indico Logo",
                modifier = Modifier.height(48.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Crie sua conta",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Junte-se à maior rede de indicações da região.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 32.dp),
                textAlign = TextAlign.Center
            )

            // Basic Info
            InputLabel("Nome Completo")
            BasicTextField(value = name, onValueChange = { name = it }, placeholder = "Como quer ser chamado?")
            
            Spacer(modifier = Modifier.height(16.dp))
            InputLabel("Email")
            BasicTextField(value = email, onValueChange = { email = it }, placeholder = "seu@email.com")

            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    InputLabel("CPF/CNPJ")
                    BasicTextField(value = cpf, onValueChange = { cpf = it }, placeholder = "000.000.000-00")
                }
                Column(modifier = Modifier.weight(1f)) {
                    InputLabel("Telefone")
                    BasicTextField(value = phone, onValueChange = { phone = it }, placeholder = "(00) 00000-0000")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Location Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(16.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        InputLabel("Estado")
                        DropdownField(value = state, onValueChange = { state = it }, options = listOf("SP", "RJ", "MG"))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        InputLabel("Cidade")
                        DropdownField(value = city, onValueChange = { city = it }, options = listOf("São Paulo", "Rio de Janeiro"))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                InputLabel("Bairro")
                BasicTextField(
                    value = neighborhood, 
                    onValueChange = { neighborhood = it }, 
                    placeholder = "Ex: Centro",
                    backgroundColor = MaterialTheme.colorScheme.surface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Password Section
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    InputLabel("Senha")
                    BasicTextField(value = password, onValueChange = { password = it }, placeholder = "••••••••", isPassword = true)
                }
                Column(modifier = Modifier.weight(1f)) {
                    InputLabel("Confirmação da Senha")
                    BasicTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, placeholder = "••••••••", isPassword = true)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Terms
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = agreed,
                    onCheckedChange = { agreed = it },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.secondary)
                )
                Text(
                    text = "Eu li e aceito os termos de serviço e a política de privacidade do Eu Indico!.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CTA
            val brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))
            Button(
                onClick = onRegisterSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(brush, RoundedCornerShape(28.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Criar Conta", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigate to Login
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    "Já tem uma conta? ",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Fazer Login",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
            
            Spacer(modifier = Modifier.height(96.dp)) // padding for bottom nav safely
        }
    }
}

@Composable
fun InputLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicTextField(
    value: String, 
    onValueChange: (String) -> Unit, 
    placeholder: String, 
    isPassword: Boolean = false,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), fontSize = 14.sp) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    value: String, 
    onValueChange: (String) -> Unit, 
    options: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Selecione", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), fontSize = 14.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onValueChange(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}
