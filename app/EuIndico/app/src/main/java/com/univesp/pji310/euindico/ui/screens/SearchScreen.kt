package com.univesp.pji310.euindico.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

import com.univesp.pji310.euindico.data.model.ProfessionalResult
import com.univesp.pji310.euindico.ui.viewmodels.SearchState
import com.univesp.pji310.euindico.ui.viewmodels.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: SearchViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedProfession by remember { mutableStateOf("") }
    var professionalToReview by remember { mutableStateOf<ProfessionalResult?>(null) }

    val searchState by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar
        TopSearchBar()

        LazyColumn(
            contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 100.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    text = "Buscar Profissionais",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Encontre os melhores serviços recomendados pela comunidade.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
            }

            // Professionals Section
            when (searchState) {
                is SearchState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is SearchState.Error -> {
                    item {
                        Text(
                            text = (searchState as SearchState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                is SearchState.Success -> {
                    val professionals = (searchState as SearchState.Success).professionals
                    val categories = (searchState as SearchState.Success).categories.map { it.nome }

                    item {
                        SearchFilterFields(
                            query = searchQuery, 
                            onQueryChange = { searchQuery = it }, 
                            profession = selectedProfession, 
                            onProfessionChange = { 
                                selectedProfession = it
                                viewModel.searchByProfession(it)
                            },
                            categories = categories
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            Text("Profissionais Disponíveis", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    if (professionals.isEmpty()) {
                        item {
                            Text("Nenhum profissional encontrado.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(16.dp))
                        }
                    } else {
                        items(professionals) { prof ->
                            ProfessionalCard(prof, onReviewClick = { professionalToReview = prof })
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }

        if (professionalToReview != null) {
            AvaliacaoDialog(
                professional = professionalToReview!!,
                onDismiss = { professionalToReview = null },
                onSubmit = { avaliacao, comentario ->
                    viewModel.submitReview(
                        idProfissao = professionalToReview!!.idProfissao,
                        idPrestador = professionalToReview!!.idPrestador,
                        avaliacao = avaliacao,
                        comentario = comentario
                    ) { success ->
                        professionalToReview = null
                        // Could show a snackbar here, ignoring for simplicity
                    }
                }
            )
        }
    }
}

@Composable
fun TopSearchBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.9f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = "https://lh3.googleusercontent.com/aida/ADBb0uj9EBiJqBrW5TButeN7qxeF3V3OJ--HmKwMOfe2Mm55dYbjM34H4u7B8w16wgGrDcCfFOLifw6PbghNd2EO4igS_prnbrSOp02QHMWytm5zIw0fI1FA5a8ffViL8Kazdqa_hZI6YmtoiS9AoY4aYVkXX0_oRy9_oAasQu8aTa0BUemp5OrUjDQXBKMtDaBWdC2fMbjI_lYdtQI8rpdsxNKFIL0Z4MpUMJ9uJQXyOLXT5qheTr28t_-_jlPclJeHYE-GsXHMi5GZxVo",
                contentDescription = null,
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Eu Indico!", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF0F172A))
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("JD", color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFilterFields(
    query: String, 
    onQueryChange: (String) -> Unit,
    profession: String,
    onProfessionChange: (String) -> Unit,
    categories: List<String>
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Search
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            placeholder = { Text("Ex: Eletricista, Encanador...", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )
        // Filter
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            TextField(
                value = if(profession.isEmpty()) "Todas as Categorias" else profession,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                val opts = listOf("Todas as Categorias") + categories
                opts.forEach { opt ->
                    DropdownMenuItem(
                        text = { Text(opt) },
                        onClick = {
                            onProfessionChange(if(opt == "Todas as Categorias") "" else opt)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfessionalCard(prof: ProfessionalResult, onReviewClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                // Profile & Name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(prof.nome?.firstOrNull()?.toString() ?: "?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(prof.nome ?: "Sem Nome", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(prof.cidade ?: "Região Padrão", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                
                // Profession Badge
                Box(modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text("P #${prof.idProfissao}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Medium)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                // Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(prof.avaliacao ?: "Sem Avaliação", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // Actions
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { }) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Contato", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.secondary)
                    }

                    Button(
                        onClick = onReviewClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Avaliar", color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AvaliacaoDialog(
    professional: ProfessionalResult,
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var avaliacao by remember { mutableStateOf(5) }
    var comentario by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nova Avaliação", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column {
                Text("Avaliando: ${professional.nome ?: "Profissional"}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Nota (1 a 5):", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    for (i in 1..5) {
                        val isSelected = i <= avaliacao
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Nota $i",
                            tint = if (isSelected) Color(0xFFF59E0B) else Color.LightGray,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { avaliacao = i }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = comentario,
                    onValueChange = { comentario = it },
                    label = { Text("Comentário") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(avaliacao, comentario) }) {
                Text("Enviar Avaliação")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
