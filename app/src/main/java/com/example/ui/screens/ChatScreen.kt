package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AIState
import com.example.data.local.model.ChatMessageEntity
import com.example.ui.components.FrostedGlassBackground
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.NovaViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: NovaViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val aiState by viewModel.aiState.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val telemetry by viewModel.telemetry.collectAsState()
    val context = LocalContext.current

    var messageInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    FrostedGlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "ASSISTENTE NOVA",
                                style = MaterialTheme.typography.titleMedium,
                                color = NovaTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = if (telemetry.isOnline) "Modo Online • Gemini AI Ativo" else "Modo Offline • Motor Local",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (telemetry.isOnline) NovaLaserGreen else NovaTextMuted,
                                fontSize = 11.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Voltar",
                                tint = NovaCyan
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.clearChat() }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Limpar Conversa",
                                tint = NovaTextMuted
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Quick suggested chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickPromptChip("📅 Organiza meu dia") {
                        viewModel.submitQuery("Organiza meu dia", readAloud = false)
                    }
                    QuickPromptChip("▶️ Abre o YouTube") {
                        viewModel.submitQuery("Abre o YouTube", readAloud = false)
                    }
                    QuickPromptChip("🧠 Nome do projeto?") {
                        viewModel.submitQuery("Qual é o nome do meu projeto?", readAloud = true)
                    }
                }

                // Message Feed
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(chatMessages, key = { it.id }) { msg ->
                        ChatBubble(
                            message = msg,
                            onSpeak = { viewModel.speakText(msg.text) },
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("NOVA Chat", msg.text))
                            }
                        )
                    }

                    if (aiState == AIState.THINKING) {
                        item {
                            Surface(
                                shape = CircleShape,
                                color = Color(0x0DFFFFFF),
                                border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        color = NovaCyan,
                                        strokeWidth = 2.dp
                                    )
                                    Text(
                                        text = "Processando com Inteligência Neural...",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NovaCyan,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Frosted Floating Bottom Input Bar
                Surface(
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = Color(0x14FFFFFF), // bg-white/8
                    border = BorderStroke(1.dp, Color(0x1AFFFFFF))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (isListening) viewModel.stopVoice() else viewModel.startVoiceInput()
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isListening) Brush.linearGradient(listOf(NovaLaserGreen, NovaCyan))
                                    else Brush.linearGradient(listOf(Color(0x1AFFFFFF), Color(0x1AFFFFFF)))
                                )
                                .testTag("chat_mic_button")
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Voz",
                                tint = if (isListening) NovaVoidBlack else NovaCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            placeholder = {
                                Text(
                                    text = if (isListening) "Ouvindo sua voz..." else "Digite sua mensagem...",
                                    color = NovaTextMuted,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_field"),
                            shape = CircleShape,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0x3306B6D4),
                                unfocusedBorderColor = Color(0x1AFFFFFF),
                                focusedTextColor = NovaTextPrimary,
                                unfocusedTextColor = NovaTextPrimary,
                                focusedContainerColor = Color(0x0DFFFFFF),
                                unfocusedContainerColor = Color(0x0DFFFFFF)
                            )
                        )

                        IconButton(
                            onClick = {
                                if (messageInput.isNotBlank()) {
                                    val text = messageInput
                                    messageInput = ""
                                    viewModel.submitQuery(text, readAloud = false)
                                }
                            },
                            enabled = messageInput.isNotBlank(),
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (messageInput.isNotBlank()) Brush.linearGradient(listOf(NovaCyan, NovaBlue))
                                    else Brush.linearGradient(listOf(Color(0x0DFFFFFF), Color(0x0DFFFFFF)))
                                )
                                .testTag("chat_send_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Enviar",
                                tint = if (messageInput.isNotBlank()) NovaVoidBlack else NovaTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessageEntity,
    onSpeak: () -> Unit,
    onCopy: () -> Unit
) {
    val isUser = message.isUser
    val timeFormatted = remember(message.timestamp) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (isUser) 20.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 20.dp
            ),
            color = if (isUser) Color(0x1F06B6D4) else Color(0x0DFFFFFF),
            border = BorderStroke(
                1.dp,
                if (isUser) Color(0x3306B6D4) else Color(0x1EFFFFFF)
            ),
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (!isUser) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NOVA INTELLIGENCE",
                            style = MaterialTheme.typography.labelSmall,
                            color = NovaCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = onSpeak, modifier = Modifier.size(24.dp)) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Ouvir",
                                    tint = NovaCyan,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                            IconButton(onClick = onCopy, modifier = Modifier.size(24.dp)) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copiar",
                                    tint = NovaTextMuted,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NovaTextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                if (message.actionType != null && message.actionPayload != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = CircleShape,
                        color = Color(0x1A10B981),
                        border = BorderStroke(1.dp, Color(0x3310B981))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = NovaLaserGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "AÇÃO EXECUTADA: ${message.actionPayload}",
                                style = MaterialTheme.typography.labelSmall,
                                color = NovaLaserGreen,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeFormatted,
                    style = MaterialTheme.typography.labelSmall,
                    color = NovaTextMuted,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun QuickPromptChip(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = Color(0x0DFFFFFF),
        border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = NovaCyan,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
