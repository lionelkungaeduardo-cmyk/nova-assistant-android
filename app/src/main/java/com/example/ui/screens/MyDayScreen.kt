package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.model.TaskEntity
import com.example.ui.components.CyberButton
import com.example.ui.components.FrostedGlassBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.SciFiProgressBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.NovaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDayScreen(
    viewModel: NovaViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val userName = userProfile?.userName ?: "Leonel"

    var selectedCategory by remember { mutableStateOf("Todas") }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    val categories = listOf("Todas", "Estudo", "Trabalho", "Pessoal", "Projeto", "Importante")

    val filteredTasks = remember(tasks, selectedCategory) {
        if (selectedCategory == "Todas") tasks else tasks.filter { it.category == selectedCategory }
    }

    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    FrostedGlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "ORGANIZADOR • MEU DIA",
                                style = MaterialTheme.typography.titleMedium,
                                color = NovaTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Cronograma e Produtividade Pessoal",
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
                    actions = {
                        IconButton(onClick = { viewModel.reorganizeDayPlan() }) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Reorganizar Dia", tint = NovaCyberAmber)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddTaskDialog = true },
                    containerColor = NovaCyan,
                    contentColor = NovaVoidBlack,
                    shape = CircleShape,
                    modifier = Modifier.testTag("add_task_fab")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Nova Tarefa")
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Daily Progress Overview Card
                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "BOM DIA, ${userName.uppercase()}",
                                style = MaterialTheme.typography.titleMedium,
                                color = NovaTextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "$completedCount de $totalCount tarefas concluídas",
                                style = MaterialTheme.typography.labelSmall,
                                color = NovaTextSecondary
                            )
                        }
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NovaLaserGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SciFiProgressBar(
                        progress = progress,
                        label = "PROGRESSO DIÁRIO",
                        valueText = "${(progress * 100).toInt()}%",
                        color = NovaLaserGreen
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CyberButton(
                            text = "REORGANIZAR DIA",
                            icon = Icons.Default.AutoFixHigh,
                            onClick = { viewModel.reorganizeDayPlan() },
                            color = Color(0x1A06B6D4),
                            textColor = NovaCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Category Filter Scroll
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) NovaCyan else Color(0x0DFFFFFF),
                            border = BorderStroke(1.dp, if (isSelected) NovaCyan else Color(0x1AFFFFFF)),
                            modifier = Modifier.clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) NovaVoidBlack else NovaTextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Task List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    if (filteredTasks.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Nenhuma tarefa nesta categoria.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = NovaTextMuted
                                )
                            }
                        }
                    } else {
                        items(filteredTasks, key = { it.id }) { task ->
                            TaskItemCard(
                                task = task,
                                onToggle = { viewModel.toggleTaskCompletion(task) },
                                onDelete = { viewModel.deleteTask(task) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title, desc, time, priority, category ->
                viewModel.addTask(title, desc, time, priority, category)
                showAddTaskDialog = false
            }
        )
    }
}

@Composable
fun TaskItemCard(
    task: TaskEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val priorityColor = when (task.priority) {
        "ALTA" -> NovaNeonPink
        "MÉDIA" -> NovaCyberAmber
        else -> NovaLaserGreen
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .testTag("task_card_${task.id}"),
        shape = RoundedCornerShape(20.dp),
        color = if (task.isCompleted) Color(0x06FFFFFF) else Color(0x0DFFFFFF),
        border = BorderStroke(
            1.dp,
            if (task.isCompleted) Color(0x0DFFFFFF) else Color(0x1AFFFFFF)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Checkbox
            IconButton(
                onClick = onToggle,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = "Status",
                    tint = if (task.isCompleted) NovaLaserGreen else NovaTextMuted
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (task.isCompleted) NovaTextMuted else NovaTextPrimary,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NovaTextMuted,
                        maxLines = 2,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (task.time.isNotBlank()) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0x1406B6D4)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, tint = NovaCyan, modifier = Modifier.size(11.dp))
                                Text(text = task.time, style = MaterialTheme.typography.labelSmall, color = NovaCyan, fontSize = 10.sp)
                            }
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = priorityColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = task.priority,
                            style = MaterialTheme.typography.labelSmall,
                            color = priorityColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color(0x0DFFFFFF)
                    ) {
                        Text(
                            text = task.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = NovaTextSecondary,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Excluir", tint = NovaTextMuted, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, desc: String, time: String, priority: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("09:00") }
    var priority by remember { mutableStateOf("MÉDIA") }
    var category by remember { mutableStateOf("Pessoal") }

    val priorities = listOf("ALTA", "MÉDIA", "BAIXA")
    val categories = listOf("Estudo", "Trabalho", "Pessoal", "Projeto", "Importante")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "CRIAR NOVA TAREFA", style = MaterialTheme.typography.titleMedium, color = NovaCyan, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título da Tarefa") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Descrição (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Horário (ex: 14:30)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(text = "Prioridade:", style = MaterialTheme.typography.labelSmall, color = NovaTextMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    priorities.forEach { p ->
                        FilterChip(
                            selected = priority == p,
                            onClick = { priority = p },
                            label = { Text(p) }
                        )
                    }
                }

                Text(text = "Categoria:", style = MaterialTheme.typography.labelSmall, color = NovaTextMuted)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.take(3).forEach { c ->
                        FilterChip(
                            selected = category == c,
                            onClick = { category = c },
                            label = { Text(c) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, desc, time, priority, category)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NovaCyan, contentColor = NovaVoidBlack)
            ) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = NovaTextMuted)
            }
        },
        containerColor = NovaSpaceDark
    )
}
