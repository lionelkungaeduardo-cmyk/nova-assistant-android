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
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberButton
import com.example.ui.components.FrostedGlassBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.NovaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechNovaScreen(
    viewModel: NovaViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("🖥️ PC Benchmark", "⚠️ Erros & BSOD", "📱 Suporte Android")

    FrostedGlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "TECHNOVA • ASSISTÊNCIA TÉCNICA",
                                style = MaterialTheme.typography.titleMedium,
                                color = NovaTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Diagnóstico Especializado de PC, Android & Jogos",
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
                // Tab Selector
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0x0DFFFFFF),
                    contentColor = NovaViolet,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = NovaViolet
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
                                    color = if (selectedTab == index) NovaViolet else NovaTextSecondary,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedTab) {
                        0 -> PcBenchmarkTab()
                        1 -> ErrorDecoderTab()
                        2 -> AndroidSupportTab()
                    }
                }
            }
        }
    }
}

@Composable
fun PcBenchmarkTab() {
    var cpu by remember { mutableStateOf("Intel Core i5-10400 / Ryzen 5 3600") }
    var gpu by remember { mutableStateOf("NVIDIA GTX 1660 Super 6GB") }
    var ram by remember { mutableStateOf("16 GB") }
    var storage by remember { mutableStateOf("SSD NVMe") }
    var selectedGame by remember { mutableStateOf("GTA V") }

    var resultAnalysis by remember { mutableStateOf<GameAnalysisResult?>(null) }

    val games = listOf("GTA V", "Cyberpunk 2077", "Valorant", "CS2", "Fortnite", "Warzone", "Minecraft com Shaders", "Elden Ring")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        GlassCard {
            Text(
                text = "MEU PC RODA ESTE JOGO?",
                style = MaterialTheme.typography.titleMedium,
                color = NovaCyan,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Insira as especificações do teu computador para calcular compatibilidade e FPS estimado.",
                style = MaterialTheme.typography.bodyMedium,
                color = NovaTextMuted
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = cpu,
                onValueChange = { cpu = it },
                label = { Text("Processador (CPU)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = gpu,
                onValueChange = { gpu = it },
                label = { Text("Placa de Vídeo (GPU)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = ram,
                    onValueChange = { ram = it },
                    label = { Text("RAM") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = storage,
                    onValueChange = { storage = it },
                    label = { Text("Armazenamento") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Selecione o Jogo Desejado:", style = MaterialTheme.typography.labelSmall, color = NovaTextMuted)
            Spacer(modifier = Modifier.height(4.dp))

            // Game picker chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                games.take(4).forEach { g ->
                    FilterChip(
                        selected = selectedGame == g,
                        onClick = { selectedGame = g },
                        label = { Text(g, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            CyberButton(
                text = "ANALISAR COMPATIBILIDADE",
                icon = Icons.Default.Speed,
                color = NovaViolet,
                textColor = Color.White,
                onClick = {
                    resultAnalysis = calculatePcCompatibility(cpu, gpu, ram, storage, selectedGame)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        resultAnalysis?.let { res ->
            val tierColor = when (res.tier) {
                "EXCELENTE" -> NovaLaserGreen
                "BOA" -> NovaCyan
                "LIMITADA" -> NovaCyberAmber
                else -> NovaNeonPink
            }

            GlassCard(borderColor = tierColor.copy(alpha = 0.5f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RESULTADO: $selectedGame",
                        style = MaterialTheme.typography.titleMedium,
                        color = NovaTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    StatusBadge(
                        label = res.tier,
                        isActive = true,
                        activeColor = tierColor
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "FPS Estimado: ${res.estimatedFps}",
                    style = MaterialTheme.typography.titleLarge,
                    color = tierColor,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(text = "Configuração Recomendada: ${res.recommendedSettings}", style = MaterialTheme.typography.bodyMedium, color = NovaTextPrimary)
                Text(text = "Resolução Alvo: ${res.targetResolution}", style = MaterialTheme.typography.bodyMedium, color = NovaTextSecondary)

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NovaSurfaceElevated
                ) {
                    Text(
                        text = "Observações Técnicas: ${res.notes}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NovaTextSecondary,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

data class GameAnalysisResult(
    val tier: String,
    val estimatedFps: String,
    val recommendedSettings: String,
    val targetResolution: String,
    val notes: String
)

fun calculatePcCompatibility(cpu: String, gpu: String, ram: String, storage: String, game: String): GameAnalysisResult {
    val gpuLower = gpu.lowercase()
    val isRtx = gpuLower.contains("rtx") || gpuLower.contains("rx 6") || gpuLower.contains("rx 7")
    val isGtx = gpuLower.contains("gtx") || gpuLower.contains("rx 5")
    val isIntegrated = gpuLower.contains("intel") || gpuLower.contains("vega") || gpuLower.contains("iris") || gpuLower.contains("uhd")

    return when (game) {
        "GTA V", "Valorant", "CS2" -> {
            if (isIntegrated) {
                GameAnalysisResult("BOA", "45 - 60 FPS", "Qualidade Média / Baixa", "1080p", "Joga perfeitamente com boa fluidez. Habilitar modo dual-channel de RAM se possível.")
            } else {
                GameAnalysisResult("EXCELENTE", "110 - 165+ FPS", "Qualidade Muito Alta", "1080p / 1440p", "Desempenho altíssimo sem gargalos. Estabilidade impecável para taxas de atualização altas.")
            }
        }
        "Cyberpunk 2077", "Warzone" -> {
            if (isRtx) {
                GameAnalysisResult("EXCELENTE", "75 - 90 FPS", "Qualidade Alta com DLSS/FSR Qualidade", "1080p / 1440p", "Compatibilidade total com Ray Tracing leve e upscaling ativado.")
            } else if (isGtx) {
                GameAnalysisResult("BOA", "50 - 65 FPS", "Qualidade Média com FSR Balanceado", "1080p", "Experiência sólida e estável travando em 60 FPS com texturas médias.")
            } else {
                GameAnalysisResult("LIMITADA", "25 - 35 FPS", "Qualidade Baixa com Resolução Reduzida", "720p", "Placa de vídeo integrada terá dificuldades com títulos pesados da geração.")
            }
        }
        else -> {
            GameAnalysisResult("BOA", "60 - 80 FPS", "Qualidade Alta", "1080p", "Equilíbrio excelente entre fidelidade gráfica e taxa de quadros.")
        }
    }
}

@Composable
fun ErrorDecoderTab() {
    var errorCode by remember { mutableStateOf("0xC000001D") }
    var decodedError by remember { mutableStateOf<ErrorSolution?>(null) }

    val presetErrors = listOf("0xC000001D", "0x80070005", "0x0000007B", "DPC_WATCHDOG_VIOLATION")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        GlassCard {
            Text(
                text = "DIAGNÓSTICO & RESOLUÇÃO DE ERROS",
                style = MaterialTheme.typography.titleMedium,
                color = NovaViolet,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Informe o código de erro do Windows, tela azul (BSOD) ou aplicativo para gerar uma solução guiada passo a passo.",
                style = MaterialTheme.typography.bodyMedium,
                color = NovaTextMuted
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = errorCode,
                onValueChange = { errorCode = it },
                label = { Text("Código de Erro / BSOD") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                presetErrors.forEach { err ->
                    FilterChip(
                        selected = errorCode == err,
                        onClick = { errorCode = err },
                        label = { Text(err, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            CyberButton(
                text = "DECODIFICAR ERRO",
                icon = Icons.Default.BugReport,
                color = NovaViolet,
                textColor = Color.White,
                onClick = {
                    decodedError = decodeSystemError(errorCode)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        decodedError?.let { err ->
            GlassCard {
                Text(
                    text = err.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = NovaCyan,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Causa Principal: ${err.cause}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NovaTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "GUIA DE RESOLUÇÃO PASSO A PASSO:",
                    style = MaterialTheme.typography.labelSmall,
                    color = NovaLaserGreen,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                err.steps.forEachIndexed { index, step ->
                    var isDone by remember { mutableStateOf(false) }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isDone) NovaSurfaceElevated else NovaSurfaceCard,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDone) NovaLaserGreen else NovaBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "PASSO ${index + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = NovaCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyMedium,
                                color = NovaTextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { isDone = !isDone },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDone) NovaLaserGreen else NovaSurfaceDark,
                                    contentColor = if (isDone) NovaVoidBlack else NovaTextPrimary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(if (isDone) "CONCLUÍDO" else "FIZ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class ErrorSolution(
    val name: String,
    val cause: String,
    val steps: List<String>
)

fun decodeSystemError(code: String): ErrorSolution {
    return when {
        code.contains("0xC000001D", true) -> ErrorSolution(
            name = "STATUS_ILLEGAL_INSTRUCTION (0xC000001D)",
            cause = "O software tentou executar uma instrução de CPU não suportada (ex: AVX, SSE4.2) ou executável corrompido.",
            steps = listOf(
                "Verifique se o seu processador possui suporte ao conjunto de instruções exigido pelo jogo.",
                "Execute o comando 'sfc /scannow' no Prompt de Comando (Admin) para corrigir arquivos de sistema corrompidos.",
                "Atualize o pacote Microsoft Visual C++ Redistributable (2015-2022).",
                "Reinstale o executável ou verifique a integridade dos arquivos na Steam/Epic Games."
            )
        )
        code.contains("0x80070005", true) -> ErrorSolution(
            name = "ERROR_ACCESS_DENIED (0x80070005)",
            cause = "Permissões insuficientes no Windows Update, registro ou pastas de instalação.",
            steps = listOf(
                "Execute o instalador/aplicativo como Administrador.",
                "Abra 'services.msc' e verifique se o serviço 'Windows Update' está em execução.",
                "Desative temporariamente antivírus de terceiros que possam bloquear a gravação no disco.",
                "Redefina as permissões da pasta SoftwareDistribution com 'net stop wuauserv'."
            )
        )
        else -> ErrorSolution(
            name = "ERRO DE SISTEMA $code",
            cause = "Instabilidade no subsistema do sistema operacional, driver ou memória.",
            steps = listOf(
                "Reinicie o computador para limpar o cache de memória volátil.",
                "Execute o Diagnóstico de Memória do Windows (mdsched.exe) para testar a integridade da RAM.",
                "Atualize os drivers da placa de vídeo e chipset diretamente do site do fabricante.",
                "Verifique o estado do disco com 'chkdsk C: /f /r'."
            )
        )
    }
}

@Composable
fun AndroidSupportTab() {
    var selectedBrand by remember { mutableStateOf("Samsung") }
    var selectedProblem by remember { mutableStateOf("Bateria descarregando rápido") }

    val brands = listOf("Samsung", "Tecno", "Infinix", "itel", "Xiaomi", "OPPO", "Huawei")
    val problems = listOf(
        "Bateria descarregando rápido",
        "Aplicativos fechando sozinho",
        "Wi-Fi / Bluetooth caindo",
        "Bootloop / Travado na logo",
        "Armazenamento cheio / Limpeza",
        "Entrar no Modo Recovery / Fastboot"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        GlassCard {
            Text(
                text = "SUPORTE TÉCNICO ANDROID",
                style = MaterialTheme.typography.titleMedium,
                color = NovaLaserGreen,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Soluções e diagnósticos sob medida para o seu modelo de smartphone.",
                style = MaterialTheme.typography.bodyMedium,
                color = NovaTextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Marca do Dispositivo:", style = MaterialTheme.typography.labelSmall, color = NovaTextMuted)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                brands.take(4).forEach { b ->
                    FilterChip(
                        selected = selectedBrand == b,
                        onClick = { selectedBrand = b },
                        label = { Text(b, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Problema Enfrentado:", style = MaterialTheme.typography.labelSmall, color = NovaTextMuted)
            problems.take(3).forEach { prob ->
                FilterChip(
                    selected = selectedProblem == prob,
                    onClick = { selectedProblem = prob },
                    label = { Text(prob, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                )
            }
        }

        // Diagnostic Guide
        GlassCard {
            Text(
                text = "DIAGNÓSTICO PARA $selectedBrand: $selectedProblem",
                style = MaterialTheme.typography.titleMedium,
                color = NovaCyan,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                selectedProblem.contains("Bateria") -> {
                    Text(text = "1. Acesse Configurações > Bateria e desative aplicativos com alto consumo em segundo plano.", style = MaterialTheme.typography.bodyMedium, color = NovaTextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "2. Em aparelhos $selectedBrand, ative o recurso 'Bateria Adaptável' ou 'Modo Ultra Economia'.", style = MaterialTheme.typography.bodyMedium, color = NovaTextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "3. Evite carregadores sem certificação ou com amperagem inadequada.", style = MaterialTheme.typography.bodyMedium, color = NovaTextPrimary)
                }
                selectedProblem.contains("Bootloop") -> {
                    Text(text = "AVISO DE SEGURANÇA: Não desligue o aparelho durante o processo.", style = MaterialTheme.typography.labelSmall, color = NovaNeonPink, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "1. Pressione e segure [Volume Mais + Botão Liga/Desliga] por 15 segundos para forçar o reinício no Recovery.", style = MaterialTheme.typography.bodyMedium, color = NovaTextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "2. No menu do Recovery do $selectedBrand, selecione 'Wipe Cache Partition' (não apaga fotos nem dados pessoais).", style = MaterialTheme.typography.bodyMedium, color = NovaTextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "3. Selecione 'Reboot System Now'.", style = MaterialTheme.typography.bodyMedium, color = NovaTextPrimary)
                }
                else -> {
                    Text(text = "1. Limpe o cache do aplicativo afetado em Configurações > Aplicativos > Armazenamento.", style = MaterialTheme.typography.bodyMedium, color = NovaTextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "2. Verifique se o Google Play Services está atualizado.", style = MaterialTheme.typography.bodyMedium, color = NovaTextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "3. Reinicie o smartphone para liberar memória RAM ocupada por processos zumbis.", style = MaterialTheme.typography.bodyMedium, color = NovaTextPrimary)
                }
            }
        }
    }
}
