package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AIState
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NovaViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: NovaViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val aiState by viewModel.aiState.collectAsState()
    val telemetry by viewModel.telemetry.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val pendingTasks by viewModel.pendingTasks.collectAsState()
    val calendarEvents by viewModel.calendarEvents.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val rmsLevel by viewModel.rmsLevel.collectAsState()

    val userName = userProfile?.userName ?: "Leonel"
    val isPowerSaving = userProfile?.powerSaving ?: false

    var textInput by remember { mutableStateOf("") }
    var isInputExpanded by remember { mutableStateOf(false) }
    val currentTime = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Bom dia"
        hour < 18 -> "Boa tarde"
        else -> "Boa noite"
    }

    FrostedGlassBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Frosted Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SISTEMA ATIVO",
                        style = MaterialTheme.typography.labelSmall,
                        color = NovaCyan,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.5.sp,
                        fontSize = 10.sp
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "NOVA",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NovaTextPrimary,
                            fontWeight = FontWeight.Light,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = ".",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NovaCyan,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Frosted Status Capsule
                Surface(
                    shape = CircleShape,
                    color = Color(0x0DFFFFFF), // bg-white/5
                    border = BorderStroke(1.dp, Color(0x1AFFFFFF)) // border-white/10
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (telemetry.isOnline) NovaLaserGreen else NovaTextMuted)
                        )
                        Text(
                            text = if (telemetry.isOnline) "ONLINE" else "OFFLINE",
                            style = MaterialTheme.typography.labelSmall,
                            color = NovaTextPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(12.dp)
                                .background(Color(0x33FFFFFF))
                        )
                        Text(
                            text = "${telemetry.batteryPercent}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = NovaTextPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp
                        )
                        Text(
                            text = if (telemetry.isCharging) "⚡" else "🔋",
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Holographic Frosted Glass Orb
            HolographicCore(
                aiState = aiState,
                rmsLevel = rmsLevel,
                size = 220.dp,
                isPowerSaving = isPowerSaving,
                onClick = {
                    if (aiState == AIState.LISTENING) {
                        viewModel.stopVoice()
                    } else {
                        viewModel.startVoiceInput()
                    }
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. User Greeting & AI State Response Message
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("$greeting, ")
                        withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.SemiBold)) {
                            append(userName)
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = NovaTextSecondary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (statusMessage.isNotBlank() && statusMessage != "NOVA pronta para auxiliar.") {
                        "\"$statusMessage\""
                    } else {
                        "\"Como posso auxiliar sua produtividade hoje?\""
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = NovaTextPrimary.copy(alpha = 0.92f),
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Frosted Glass 2-Column Overview Cards (Next Event & Tasks)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val nextEvent = calendarEvents.firstOrNull()
                val nextTime = nextEvent?.startTime ?: currentTime
                val nextDesc = nextEvent?.title ?: "Sincronia TechNova"

                val criticalCount = pendingTasks.count { it.priority == "Alta" }

                // Next Event Card
                FrostedMetricCard(
                    title = "PRÓXIMO EVENTO",
                    titleColor = NovaCyan,
                    icon = "⏰",
                    mainValue = nextTime,
                    subValue = nextDesc,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("my_day") }
                )

                // Tasks Card
                FrostedMetricCard(
                    title = "TAREFAS",
                    titleColor = NovaIndigo,
                    icon = "🎯",
                    mainValue = "${pendingTasks.size} Pendentes",
                    subValue = if (criticalCount > 0) "$criticalCount Prioridade Alta" else "Em dia",
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate("my_day") }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 5. Floating Frosted Interaction Bar (Input & Glowing Voice Button)
            Surface(
                shape = CircleShape,
                color = Color(0x0DFFFFFF), // bg-white/5
                border = BorderStroke(1.dp, Color(0x1AFFFFFF)), // border-white/10
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Keyboard / Text Toggle Button
                    IconButton(
                        onClick = { isInputExpanded = !isInputExpanded },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0x0DFFFFFF))
                    ) {
                        Text(text = "⌨️", fontSize = 18.sp)
                    }

                    // Text Field / Dynamic Query input
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        OutlinedTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            placeholder = {
                                Text(
                                    text = "Pergunte ou comande a NOVA...",
                                    color = NovaTextMuted,
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("home_text_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = NovaTextPrimary,
                                unfocusedTextColor = NovaTextPrimary,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }

                    // Glowing Action Button (Send if text is present, or Glowing Mic)
                    if (textInput.isNotBlank()) {
                        IconButton(
                            onClick = {
                                val query = textInput
                                textInput = ""
                                viewModel.submitQuery(query, readAloud = false)
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(NovaCyan, NovaBlue)
                                    )
                                )
                                .testTag("home_send_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Enviar",
                                tint = NovaVoidBlack,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                if (aiState == AIState.LISTENING) {
                                    viewModel.stopVoice()
                                } else {
                                    viewModel.startVoiceInput()
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        if (aiState == AIState.LISTENING) {
                                            listOf(NovaLaserGreen, NovaCyan)
                                        } else {
                                            listOf(NovaCyan, NovaBlue, NovaIndigo)
                                        }
                                    )
                                )
                                .testTag("home_mic_button")
                        ) {
                            Text(
                                text = if (aiState == AIState.LISTENING) "⏹️" else "🎙️",
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 6. Frosted Quick Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickPill("📅 O que tenho hoje?", modifier = Modifier.weight(1f)) {
                    viewModel.submitQuery("O que tenho para hoje?", readAloud = true)
                }
                QuickPill("💬 Abre o WhatsApp", modifier = Modifier.weight(1f)) {
                    viewModel.submitQuery("Abre o WhatsApp", readAloud = false)
                }
                QuickPill("⚡ Central de Comando", modifier = Modifier.weight(1f)) {
                    onNavigate("command_center")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 7. Intelligence Modules Grid
            Text(
                text = "MÓDULOS DE INTELIGÊNCIA",
                style = MaterialTheme.typography.labelSmall,
                color = NovaTextMuted,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureTile(
                        title = "Conversa IA",
                        subtitle = "Chat & Voz",
                        icon = Icons.Default.Forum,
                        accentColor = NovaCyan,
                        modifier = Modifier.weight(1f)
                    ) { onNavigate("chat") }

                    FeatureTile(
                        title = "Meu Dia",
                        subtitle = "Tarefas & Agenda",
                        icon = Icons.Default.CalendarMonth,
                        accentColor = NovaLaserGreen,
                        modifier = Modifier.weight(1f)
                    ) { onNavigate("my_day") }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureTile(
                        title = "TechNova",
                        subtitle = "Diagnóstico PC & Android",
                        icon = Icons.Default.Computer,
                        accentColor = NovaViolet,
                        modifier = Modifier.weight(1f)
                    ) { onNavigate("technova") }

                    FeatureTile(
                        title = "Central de Comando",
                        subtitle = "HUD & Telemetria",
                        icon = Icons.Default.Dashboard,
                        accentColor = NovaBlue,
                        modifier = Modifier.weight(1f)
                    ) { onNavigate("command_center") }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FeatureTile(
                        title = "Laboratório Criativo",
                        subtitle = "Web, Conteúdo & Estudo",
                        icon = Icons.Default.AutoAwesome,
                        accentColor = NovaCyberAmber,
                        modifier = Modifier.weight(1f)
                    ) { onNavigate("creative_lab") }

                    FeatureTile(
                        title = "Memória & Notas",
                        subtitle = "Base de Conhecimento",
                        icon = Icons.Default.Psychology,
                        accentColor = NovaNeonPink,
                        modifier = Modifier.weight(1f)
                    ) { onNavigate("memory_notes") }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FrostedMetricCard(
    title: String,
    titleColor: Color,
    icon: String,
    mainValue: String,
    subValue: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = Color(0x0DFFFFFF), // bg-white/5
        border = BorderStroke(1.dp, Color(0x1AFFFFFF)) // border-white/10
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = titleColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 0.8.sp
                )
                Text(text = icon, fontSize = 16.sp)
            }

            Column {
                Text(
                    text = mainValue,
                    style = MaterialTheme.typography.titleMedium,
                    color = NovaTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subValue.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = NovaTextSecondary,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun QuickPill(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = Color(0x0DFFFFFF),
        border = BorderStroke(1.dp, Color(0x1AFFFFFF)),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = NovaCyan,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun FeatureTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(84.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color(0x0DFFFFFF), // bg-white/5
        border = BorderStroke(1.dp, Color(0x1AFFFFFF)) // border-white/10
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = NovaTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = NovaTextSecondary,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
