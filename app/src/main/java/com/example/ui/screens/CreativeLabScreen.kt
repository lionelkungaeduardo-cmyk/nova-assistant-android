package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberButton
import com.example.ui.components.FrostedGlassBackground
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.NovaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreativeLabScreen(
    viewModel: NovaViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("🌐 Web Builder", "📝 Conteúdo IA", "📚 Modo Estudo", "❤️ Heart Mode")

    FrostedGlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "LABORATÓRIO CRIATIVO NOVA",
                                style = MaterialTheme.typography.titleMedium,
                                color = NovaTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Web, Criação de Conteúdo, Estudo & Modo Afeto",
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
                    contentColor = NovaCyberAmber,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = NovaCyberAmber
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
                                    color = if (selectedTab == index) NovaCyberAmber else NovaTextSecondary,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    when (selectedTab) {
                        0 -> WebBuilderSubTab()
                        1 -> ContentAiSubTab()
                        2 -> StudyModeSubTab()
                        3 -> HeartModeSubTab(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun WebBuilderSubTab() {
    var prompt by remember { mutableStateOf("Site futurista para o projeto TechNova") }
    var generatedCode by remember { mutableStateOf<String?>(null) }
    var viewMode by remember { mutableStateOf("CODE") } // CODE or PREVIEW
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        GlassCard {
            Text(
                text = "GERADOR DE WEBSITES & CÓDIGO HTML5/CSS3",
                style = MaterialTheme.typography.titleMedium,
                color = NovaCyan,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Descreva o site ou página web desejada para a NOVA gerar o código completo e responsivo.",
                style = MaterialTheme.typography.bodyMedium,
                color = NovaTextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Descrição do Site") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            CyberButton(
                text = "GERAR CÓDIGO DO SITE",
                icon = Icons.Default.Code,
                color = NovaCyan,
                textColor = NovaVoidBlack,
                onClick = {
                    generatedCode = generateWebTemplate(prompt)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        generatedCode?.let { code ->
            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CÓDIGO GERADO",
                        style = MaterialTheme.typography.titleMedium,
                        color = NovaLaserGreen,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("HTML Code", code))
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copiar", tint = NovaCyan)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NovaVoidBlack,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NovaBorder)
                ) {
                    Text(
                        text = code,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = NovaCyan,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

fun generateWebTemplate(prompt: String): String {
    return """
<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>NOVA - $prompt</title>
  <style>
    body {
      margin: 0;
      background: #030712;
      color: #F3F4F6;
      font-family: 'Segoe UI', system-ui, sans-serif;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      min-height: 100vh;
    }
    .hero {
      text-align: center;
      padding: 40px;
      border: 1px solid rgba(0, 240, 255, 0.3);
      border-radius: 16px;
      background: rgba(11, 19, 43, 0.6);
      backdrop-filter: blur(12px);
      box-shadow: 0 0 30px rgba(0, 240, 255, 0.2);
    }
    h1 {
      color: #00F0FF;
      font-size: 2.5rem;
      margin-bottom: 8px;
    }
    p { color: #9CA3AF; font-size: 1.1rem; }
    .btn {
      margin-top: 20px;
      padding: 12px 28px;
      background: #00F0FF;
      color: #030712;
      font-weight: bold;
      border: none;
      border-radius: 8px;
      cursor: pointer;
      transition: 0.3s;
    }
    .btn:hover { box-shadow: 0 0 20px #00F0FF; }
  </style>
</head>
<body>
  <div class="hero">
    <h1>$prompt</h1>
    <p>Projeto desenvolvido e gerado pelo assistente de IA NOVA.</p>
    <button class="btn" onclick="alert('Sistema TechNova Ativo!')">Explorar Módulos</button>
  </div>
</body>
</html>
""".trimIndent()
}

@Composable
fun ContentAiSubTab() {
    var topic by remember { mutableStateOf("Dicas de produtividade e IA para estudantes") }
    var selectedPlatform by remember { mutableStateOf("YouTube") }
    var generatedContent by remember { mutableStateOf<GeneratedPost?>(null) }
    val context = LocalContext.current

    val platforms = listOf("YouTube", "TikTok", "Instagram", "Blog Post", "Telegram")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        GlassCard {
            Text(
                text = "GERADOR DE CONTEÚDO & ROTEIROS",
                style = MaterialTheme.typography.titleMedium,
                color = NovaCyberAmber,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                label = { Text("Tema do Conteúdo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Plataforma:", style = MaterialTheme.typography.labelSmall, color = NovaTextMuted)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                platforms.take(3).forEach { p ->
                    FilterChip(
                        selected = selectedPlatform == p,
                        onClick = { selectedPlatform = p },
                        label = { Text(p, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            CyberButton(
                text = "CRIAR ROTEIRO / CONTEÚDO",
                icon = Icons.Default.AutoAwesome,
                color = NovaCyberAmber,
                textColor = NovaVoidBlack,
                onClick = {
                    generatedContent = createContentForPlatform(topic, selectedPlatform)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        generatedContent?.let { content ->
            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = content.title, style = MaterialTheme.typography.titleMedium, color = NovaCyan, fontWeight = FontWeight.Bold)
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Content", "${content.title}\n\n${content.body}\n\n${content.hashtags}"))
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copiar", tint = NovaCyan)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = content.body, style = MaterialTheme.typography.bodyMedium, color = NovaTextPrimary)

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = content.hashtags, style = MaterialTheme.typography.labelSmall, color = NovaLaserGreen)
            }
        }
    }
}

data class GeneratedPost(
    val title: String,
    val body: String,
    val hashtags: String
)

fun createContentForPlatform(topic: String, platform: String): GeneratedPost {
    return GeneratedPost(
        title = "🚀 $topic [$platform]",
        body = """
            [GANCHO INICIAL DE 3 SEGUNDOS]:
            "Você ainda perde horas tentando organizar sua rotina? Esse método mudou tudo."
            
            [DESENVOLVIMENTO]:
            1. Defina apenas 3 metas inegociáveis para o dia.
            2. Utilize um assistente como a NOVA para automatizar lembretes e agendamentos.
            3. Elimine distrações durante blocos de foco de 25 minutos.
            
            [CHAMADA PARA AÇÃO (CTA)]:
            Comente 'NOVA' para receber o guia completo de produtividade!
        """.trimIndent(),
        hashtags = "#Produtividade #InteligenciaArtificial #TechNova #Organizacao #DicasDeEstudo"
    )
}

@Composable
fun StudyModeSubTab() {
    var subject by remember { mutableStateOf("Estrutura de Dados e Algoritmos") }
    var level by remember { mutableStateOf("Intermediário") }
    var studyGuide by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        GlassCard {
            Text(
                text = "MODO ESTUDO & TUTOR INTELIGENTE",
                style = MaterialTheme.typography.titleMedium,
                color = NovaViolet,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "A NOVA sintetiza qualquer assunto complexo com explicações claras e exercícios práticos.",
                style = MaterialTheme.typography.bodyMedium,
                color = NovaTextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Matéria ou Conceito") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            CyberButton(
                text = "GERAR PLANO DE ESTUDO",
                icon = Icons.Default.School,
                color = NovaViolet,
                textColor = Color.White,
                onClick = {
                    studyGuide = """
                        📚 CONCEITO PRINCIPAL: $subject
                        
                        1. FUNDAMENTOS:
                        • Entenda a diferença entre armazenamento em array contíguo vs encadeamento em nós.
                        • Complexidade de tempo (Big-O notation): O(1), O(n), O(log n).
                        
                        2. EXERCÍCIO PRÁTICO:
                        Implemente uma lista encadeada simples com operações de inserção no início e remoção.
                        
                        3. DICA DA NOVA:
                        Sempre desenhe a memória no papel antes de escrever o algoritmo!
                    """.trimIndent()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        studyGuide?.let { guide ->
            GlassCard {
                Text(text = guide, style = MaterialTheme.typography.bodyMedium, color = NovaTextPrimary)
            }
        }
    }
}

@Composable
fun HeartModeSubTab(viewModel: NovaViewModel) {
    var recipient by remember { mutableStateOf("Pessoa Especial") }
    var selectedTone by remember { mutableStateOf("Romântico & Carinhoso") }
    var generatedMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val tones = listOf("Romântico & Carinhoso", "Bom Dia Doce", "Declaração Profunda", "Pedido de Desculpas")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        GlassCard {
            Text(
                text = "HEART MODE ❤️ • MENSAGENS ESPECIAIS",
                style = MaterialTheme.typography.titleMedium,
                color = NovaNeonPink,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Crie mensagens carinhosas, poéticas e marcantes para enviar a quem você ama.",
                style = MaterialTheme.typography.bodyMedium,
                color = NovaTextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = recipient,
                onValueChange = { recipient = it },
                label = { Text("Nome da Pessoa") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Tom da Mensagem:", style = MaterialTheme.typography.labelSmall, color = NovaTextMuted)
            tones.take(2).forEach { t ->
                FilterChip(
                    selected = selectedTone == t,
                    onClick = { selectedTone = t },
                    label = { Text(t, fontSize = 12.sp) },
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            CyberButton(
                text = "GERAR MENSAGEM DO CORAÇÃO",
                icon = Icons.Default.Favorite,
                color = NovaNeonPink,
                textColor = Color.White,
                onClick = {
                    generatedMessage = "Passando para lembrar que teu sorriso ilumina meu dia mais do que qualquer tecnologia futurista. Obrigado por ser minha pessoa especial, $recipient. ❤️"
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        generatedMessage?.let { msg ->
            GlassCard(borderColor = NovaNeonPink.copy(alpha = 0.5f)) {
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodyLarge,
                    color = NovaTextPrimary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CyberButton(
                        text = "ENVIAR WHATSAPP",
                        icon = Icons.Default.Send,
                        color = NovaLaserGreen,
                        textColor = NovaVoidBlack,
                        onClick = {
                            viewModel.appLauncherService.openWhatsApp(message = msg)
                        },
                        modifier = Modifier.weight(1f)
                    )

                    CyberButton(
                        text = "COPIAR",
                        icon = Icons.Default.ContentCopy,
                        color = NovaSurfaceCard,
                        textColor = NovaCyan,
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Heart Message", msg))
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
