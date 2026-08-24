package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.local.model.UserProfileEntity
import com.example.ui.components.CyberButton
import com.example.ui.components.FrostedGlassBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.NovaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsSettingsScreen(
    viewModel: NovaViewModel,
    onBack: () -> Unit,
    onRestartOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val context = LocalContext.current

    var userName by remember(userProfile) { mutableStateOf(userProfile?.userName ?: "Leonel") }
    var voiceSpeed by remember(userProfile) { mutableStateOf(userProfile?.voiceSpeed ?: 1.0f) }
    var offlineOnly by remember(userProfile) { mutableStateOf(userProfile?.offlineOnly ?: false) }
    var powerSaving by remember(userProfile) { mutableStateOf(userProfile?.powerSaving ?: false) }
    var personality by remember(userProfile) { mutableStateOf(userProfile?.personality ?: "Objetiva & Futurista") }

    FrostedGlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "CONFIGURAÇÕES & PRIVACIDADE",
                                style = MaterialTheme.typography.titleMedium,
                                color = NovaTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Permissões do Android & Personalização da IA",
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // User Profile Section
                GlassCard {
                    Text(
                        text = "PERFIL DO USUÁRIO",
                        style = MaterialTheme.typography.titleMedium,
                        color = NovaCyan,
                        fontWeight = FontWeight.SemiBold
                    )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("Nome do Usuário") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Velocidade da Voz da NOVA: %.1fx".format(voiceSpeed),
                    style = MaterialTheme.typography.bodyMedium,
                    color = NovaTextPrimary
                )
                Slider(
                    value = voiceSpeed,
                    onValueChange = { voiceSpeed = it },
                    valueRange = 0.5f..1.8f,
                    steps = 12,
                    colors = SliderDefaults.colors(
                        thumbColor = NovaCyan,
                        activeTrackColor = NovaCyan,
                        inactiveTrackColor = NovaBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Forçar Modo 100% Offline", style = MaterialTheme.typography.bodyMedium, color = NovaTextPrimary)
                        Text(text = "Desativa consultas online para privacidade total", style = MaterialTheme.typography.labelSmall, color = NovaTextMuted)
                    }
                    Switch(
                        checked = offlineOnly,
                        onCheckedChange = { offlineOnly = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NovaVoidBlack, checkedTrackColor = NovaCyan)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Economia de Energia no HUD", style = MaterialTheme.typography.bodyMedium, color = NovaTextPrimary)
                        Text(text = "Reduz rotações complexas e partículas da orbe", style = MaterialTheme.typography.labelSmall, color = NovaTextMuted)
                    }
                    Switch(
                        checked = powerSaving,
                        onCheckedChange = { powerSaving = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = NovaVoidBlack, checkedTrackColor = NovaCyan)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                CyberButton(
                    text = "SALVAR ALTERAÇÕES",
                    icon = Icons.Default.Save,
                    color = NovaCyan,
                    textColor = NovaVoidBlack,
                    onClick = {
                        userProfile?.let {
                            viewModel.updateProfile(
                                it.copy(
                                    userName = userName,
                                    voiceSpeed = voiceSpeed,
                                    offlineOnly = offlineOnly,
                                    powerSaving = powerSaving,
                                    personality = personality
                                )
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Permissions Control Hub
            GlassCard {
                Text(
                    text = "STATUS DE PERMISSÕES DO ANDROID",
                    style = MaterialTheme.typography.titleMedium,
                    color = NovaLaserGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "A NOVA respeita os padrões de segurança do Android e só executa automações autorizadas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NovaTextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                val hasMic = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                val hasNotif = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                } else true

                PermissionItem(
                    title = "Microfone & Comando de Voz",
                    description = "Necessário para reconhecimento de fala em tempo real",
                    isGranted = hasMic,
                    icon = Icons.Default.Mic
                ) {
                    openAppSettings(context)
                }

                Spacer(modifier = Modifier.height(8.dp))

                PermissionItem(
                    title = "Notificações & Alarmes",
                    description = "Para alertas de lembretes e tarefas agendadas",
                    isGranted = hasNotif,
                    icon = Icons.Default.Notifications
                ) {
                    openAppSettings(context)
                }

                Spacer(modifier = Modifier.height(8.dp))

                PermissionItem(
                    title = "Integração de Aplicativos",
                    description = "Disparo de ações no WhatsApp, YouTube e Navegador",
                    isGranted = true,
                    icon = Icons.Default.Apps
                ) {}
            }

            // Reset Tour
            OutlinedButton(
                onClick = onRestartOnboarding,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Repetir Tutorial de Introdução", color = NovaTextSecondary)
            }
        }
    }
}
}

@Composable
fun PermissionItem(
    title: String,
    description: String,
    isGranted: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color(0x0DFFFFFF),
        border = BorderStroke(1.dp, Color(0x1AFFFFFF))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = if (isGranted) NovaLaserGreen else NovaNeonPink)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, color = NovaTextPrimary, fontWeight = FontWeight.SemiBold)
                Text(text = description, style = MaterialTheme.typography.labelSmall, color = NovaTextMuted)
            }
            StatusBadge(
                label = if (isGranted) "ATIVO" else "PENDENTE",
                isActive = isGranted,
                activeColor = NovaLaserGreen,
                inactiveColor = NovaNeonPink
            )
        }
    }
}

fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}
