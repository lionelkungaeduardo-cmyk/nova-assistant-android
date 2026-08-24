package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.NovaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommandCenterScreen(
    viewModel: NovaViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val telemetry by viewModel.telemetry.collectAsState()
    val pendingTasks by viewModel.pendingTasks.collectAsState()
    val calendarEvents by viewModel.calendarEvents.collectAsState()
    val memories by viewModel.memories.collectAsState()

    FrostedGlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "CENTRAL DE COMANDO NOVA",
                                style = MaterialTheme.typography.titleMedium,
                                color = NovaTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "HUD de Diagnóstico & Telemetria do Sistema",
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // System Status HUD Banner
                GlassCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "STATUS GERAL DO DISPOSITIVO",
                                style = MaterialTheme.typography.titleMedium,
                                color = NovaTextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${telemetry.manufacturer} ${telemetry.deviceModel}",
                                style = MaterialTheme.typography.labelSmall,
                                color = NovaCyan
                            )
                        }
                        StatusBadge(
                            label = if (telemetry.isOnline) "CONECTADO" else "OFFLINE",
                            isActive = telemetry.isOnline,
                            activeColor = NovaLaserGreen,
                            inactiveColor = NovaNeonPink
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Hardware Meters
                    SciFiProgressBar(
                        progress = telemetry.batteryPercent / 100f,
                        label = "BATERIA",
                        valueText = "${telemetry.batteryPercent}% ${if (telemetry.isCharging) "(Carregando)" else ""}",
                        color = if (telemetry.isCharging) NovaCyberAmber else if (telemetry.batteryPercent > 20) NovaCyan else NovaNeonPink
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SciFiProgressBar(
                        progress = telemetry.usedRamPercent / 100f,
                        label = "MEMÓRIA RAM UTILIZADA",
                        valueText = "%.1f GB / %.1f GB (%d%%)".format(
                            telemetry.totalRamGb - telemetry.availableRamGb,
                            telemetry.totalRamGb,
                            telemetry.usedRamPercent
                        ),
                        color = NovaViolet
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SciFiProgressBar(
                        progress = telemetry.usedStoragePercent / 100f,
                        label = "ARMAZENAMENTO INTERNO",
                        valueText = "%.1f GB / %.1f GB (%d%%)".format(
                            telemetry.totalStorageGb - telemetry.availableStorageGb,
                            telemetry.totalStorageGb,
                            telemetry.usedStoragePercent
                        ),
                        color = NovaBlue
                    )
                }

                // Connectivity & OS Info Grid
                Text(
                    text = "ESPECIFICAÇÕES DE HARDWARE & REDE",
                    style = MaterialTheme.typography.labelSmall,
                    color = NovaTextMuted,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TelemetryItem(
                        title = "Rede Ativa",
                        value = telemetry.networkType,
                        icon = if (telemetry.isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                        accentColor = if (telemetry.isOnline) NovaLaserGreen else NovaNeonPink,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryItem(
                        title = "Sistema Operacional",
                        value = telemetry.osVersion,
                        icon = Icons.Default.Android,
                        accentColor = NovaCyan,
                        modifier = Modifier.weight(1f)
                    )
                }

                // AI Status & Overview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TelemetryItem(
                        title = "Memórias Gravadas",
                        value = "${memories.size} registros",
                        icon = Icons.Default.Memory,
                        accentColor = NovaNeonPink,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryItem(
                        title = "Tarefas Ativas",
                        value = "${pendingTasks.size} pendentes",
                        icon = Icons.Default.TaskAlt,
                        accentColor = NovaCyberAmber,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Quick App & System Launcher Matrix
                Text(
                    text = "EXECUÇÃO DIRETA & INTEGRAÇÃO DE APLICATIVOS",
                    style = MaterialTheme.typography.labelSmall,
                    color = NovaTextMuted,
                    letterSpacing = 0.5.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppLauncherCard(
                            appName = "WhatsApp",
                            subtitle = "Mensagens & Contatos",
                            icon = Icons.Default.Chat,
                            accentColor = NovaLaserGreen,
                            modifier = Modifier.weight(1f)
                        ) {
                            viewModel.appLauncherService.openWhatsApp()
                        }

                        AppLauncherCard(
                            appName = "YouTube",
                            subtitle = "Vídeos & Pesquisa",
                            icon = Icons.Default.PlayCircleFilled,
                            accentColor = NovaNeonPink,
                            modifier = Modifier.weight(1f)
                        ) {
                            viewModel.appLauncherService.openYouTube()
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppLauncherCard(
                            appName = "Chrome / Web",
                            subtitle = "Navegador de Internet",
                            icon = Icons.Default.Language,
                            accentColor = NovaCyan,
                            modifier = Modifier.weight(1f)
                        ) {
                            viewModel.appLauncherService.openBrowser("https://google.com")
                        }

                        AppLauncherCard(
                            appName = "Configurações",
                            subtitle = "Ajustes do Android",
                            icon = Icons.Default.Settings,
                            accentColor = NovaViolet,
                            modifier = Modifier.weight(1f)
                        ) {
                            viewModel.appLauncherService.openSettings()
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppLauncherCard(
                            appName = "Alarmes",
                            subtitle = "Relógio do Sistema",
                            icon = Icons.Default.Alarm,
                            accentColor = NovaCyberAmber,
                            modifier = Modifier.weight(1f)
                        ) {
                            viewModel.appLauncherService.setSystemAlarm(8, 0, "Alarme NOVA")
                        }

                        AppLauncherCard(
                            appName = "Wi-Fi & Redes",
                            subtitle = "Ajustes de Conexão",
                            icon = Icons.Default.NetworkCheck,
                            accentColor = NovaBlue,
                            modifier = Modifier.weight(1f)
                        ) {
                            viewModel.appLauncherService.openSettings("wifi")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun AppLauncherCard(
    appName: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(76.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Color(0x0DFFFFFF),
        border = BorderStroke(1.dp, Color(0x1AFFFFFF))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(text = appName, style = MaterialTheme.typography.titleMedium, color = NovaTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = NovaTextSecondary, fontSize = 10.sp)
            }
        }
    }
}
