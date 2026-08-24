package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AIState
import com.example.data.local.model.UserProfileEntity
import com.example.ui.components.CyberButton
import com.example.ui.components.FrostedGlassBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.HolographicCore
import com.example.ui.theme.*
import com.example.ui.viewmodel.NovaViewModel

@Composable
fun OnboardingScreen(
    viewModel: NovaViewModel,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(0) }
    var userNameInput by remember { mutableStateOf("Leonel") }

    FrostedGlassBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HolographicCore(
                aiState = AIState.IDLE,
                size = 180.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (step) {
                0 -> {
                    Text(
                        text = "NOVA",
                        style = MaterialTheme.typography.displayMedium,
                        color = NovaTextPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Sua Inteligência Pessoal",
                        style = MaterialTheme.typography.titleMedium,
                        color = NovaCyan
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GlassCard {
                        Text(
                            text = "Saudações. Eu sou a NOVA — um assistente de inteligência pessoal futurista projetado para organizar seu dia, executar automações no Android, diagnosticar computadores e smartphones, e criar conteúdos inteligentes.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = NovaTextPrimary,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    CyberButton(
                        text = "INICIAR CONFIGURAÇÃO",
                        icon = Icons.Default.ArrowForward,
                        onClick = { step = 1 },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                1 -> {
                    Text(
                        text = "COMO DEVO TE CHAMAR?",
                        style = MaterialTheme.typography.headlineSmall,
                        color = NovaTextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = userNameInput,
                        onValueChange = { userNameInput = it },
                        label = { Text("Seu Nome") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    CyberButton(
                        text = "ESTOU PRONTA",
                        icon = Icons.Default.Check,
                        onClick = {
                            viewModel.updateProfile(
                                UserProfileEntity(
                                    id = 1,
                                    userName = userNameInput.ifBlank { "Leonel" },
                                    isFirstUseDone = true
                                )
                            )
                            onComplete()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
