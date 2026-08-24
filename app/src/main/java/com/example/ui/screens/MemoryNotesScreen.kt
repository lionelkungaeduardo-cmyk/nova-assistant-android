package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.model.MemoryEntity
import com.example.data.local.model.NoteEntity
import com.example.ui.components.CyberButton
import com.example.ui.components.FrostedGlassBackground
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.NovaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryNotesScreen(
    viewModel: NovaViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("🧠 Memória da IA", "📝 Bloco de Notas")

    FrostedGlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "BASE DE CONHECIMENTO",
                                style = MaterialTheme.typography.titleMedium,
                                color = NovaTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Memória Contínua & Notas Pessoais",
                                style = MaterialTheme.typography.labelSmall,
                                color = NovaTextMuted
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Voltar", tint = NovaCyan)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0x0DFFFFFF),
                    contentColor = NovaNeonPink,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = NovaNeonPink
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selectedTab == index) NovaNeonPink else NovaTextSecondary,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    if (selectedTab == 0) {
                        MemoriesTab(viewModel)
                    } else {
                        NotesTab(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MemoriesTab(viewModel: NovaViewModel) {
    val memories by viewModel.memories.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showForgetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MEMÓRIAS REGISTRADAS (${memories.size})",
                style = MaterialTheme.typography.titleMedium,
                color = NovaCyan,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NovaCyan, contentColor = NovaVoidBlack),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Adicionar", fontSize = 12.sp)
                }
                if (memories.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { showForgetDialog = true },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Esquecer Tudo", fontSize = 12.sp, color = NovaNeonPink)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (memories.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                        Text(text = "Nenhuma memória registrada ainda.", color = NovaTextMuted)
                    }
                }
            } else {
                items(memories, key = { it.id }) { mem ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NovaSurfaceCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorderGlowing.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = mem.category.uppercase(), style = MaterialTheme.typography.labelSmall, color = NovaNeonPink, fontWeight = FontWeight.Bold)
                                Text(text = mem.key, style = MaterialTheme.typography.titleMedium, color = NovaTextPrimary, fontWeight = FontWeight.SemiBold)
                                Text(text = mem.value, style = MaterialTheme.typography.bodyMedium, color = NovaTextSecondary)
                            }
                            IconButton(onClick = { viewModel.deleteMemory(mem) }) {
                                Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "Excluir", tint = NovaTextMuted)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var cat by remember { mutableStateOf("Projetos") }
        var key by remember { mutableStateOf("") }
        var value by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("ADICIONAR MEMÓRIA", color = NovaCyan) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = cat, onValueChange = { cat = it }, label = { Text("Categoria") })
                    OutlinedTextField(value = key, onValueChange = { key = it }, label = { Text("Chave / Conceito") })
                    OutlinedTextField(value = value, onValueChange = { value = it }, label = { Text("Informação a Memorizar") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (key.isNotBlank() && value.isNotBlank()) {
                            viewModel.addMemory(cat, key, value)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NovaCyan, contentColor = NovaVoidBlack)
                ) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancelar", color = NovaTextMuted) }
            },
            containerColor = NovaSurfaceCard
        )
    }

    if (showForgetDialog) {
        AlertDialog(
            onDismissRequest = { showForgetDialog = false },
            title = { Text("CONFIRMAR LIMPEZA DE MEMÓRIA", color = NovaNeonPink) },
            text = { Text("Tem certeza que deseja apagar todas as memórias salvas na NOVA?", color = NovaTextPrimary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.forgetAllMemories()
                        showForgetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NovaNeonPink)
                ) { Text("Esquecer Tudo") }
            },
            dismissButton = {
                TextButton(onClick = { showForgetDialog = false }) { Text("Cancelar", color = NovaTextMuted) }
            },
            containerColor = NovaSurfaceCard
        )
    }
}

@Composable
fun NotesTab(viewModel: NovaViewModel) {
    val notes by viewModel.notes.collectAsState()
    var showAddNoteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BLOCO DE NOTAS (${notes.size})",
                style = MaterialTheme.typography.titleMedium,
                color = NovaLaserGreen,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { showAddNoteDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = NovaLaserGreen, contentColor = NovaVoidBlack),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Nova Nota", fontSize = 12.sp)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notes, key = { it.id }) { note ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NovaSurfaceCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = note.title, style = MaterialTheme.typography.titleMedium, color = NovaTextPrimary, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { viewModel.deleteNote(note) }, modifier = Modifier.size(24.dp)) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Excluir", tint = NovaTextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = note.content, style = MaterialTheme.typography.bodyMedium, color = NovaTextSecondary)
                    }
                }
            }
        }
    }

    if (showAddNoteDialog) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Geral") }

        AlertDialog(
            onDismissRequest = { showAddNoteDialog = false },
            title = { Text("NOVA NOTA", color = NovaLaserGreen) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, singleLine = true)
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Conteúdo") }, minLines = 3)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addNote(title, content, category)
                            showAddNoteDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NovaLaserGreen, contentColor = NovaVoidBlack)
                ) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { showAddNoteDialog = false }) { Text("Cancelar", color = NovaTextMuted) }
            },
            containerColor = NovaSurfaceCard
        )
    }
}
