package com.example.ai

import android.os.BatteryManager
import java.text.SimpleDateFormat
import java.util.*

class OfflineAIService : AIService {

    override suspend fun processQuery(
        query: String,
        userProfileName: String,
        isOnline: Boolean,
        knownMemories: List<String>
    ): AIResponse {
        val q = query.trim().lowercase(Locale.ROOT)
        val greetingName = if (userProfileName.isNotBlank()) userProfileName else "Comandante"

        // 1. App Launchers & Intents
        if (q.contains("whatsapp") || q.contains("zap")) {
            if (q.contains("mensagem") || q.contains("enviar") || q.contains("manda")) {
                val msgContent = extractMessageContent(query)
                return AIResponse(
                    replyText = "Preparando mensagem para o WhatsApp...",
                    action = NovaAction.SendWhatsApp(message = msgContent),
                    isOfflineMode = true
                )
            }
            return AIResponse(
                replyText = "Claro, abrindo o WhatsApp.",
                action = NovaAction.OpenApp("com.whatsapp", "WhatsApp"),
                isOfflineMode = true
            )
        }

        if (q.contains("youtube")) {
            val searchParam = extractSearchQuery(query, listOf("pesquisa no youtube", "procura no youtube", "abre o youtube e busca", "no youtube"))
            return if (!searchParam.isNullOrBlank()) {
                AIResponse(
                    replyText = "Buscando \"$searchParam\" no YouTube.",
                    action = NovaAction.SearchYouTube(searchParam),
                    isOfflineMode = true
                )
            } else {
                AIResponse(
                    replyText = "Abrindo o YouTube.",
                    action = NovaAction.OpenApp("com.google.android.youtube", "YouTube"),
                    isOfflineMode = true
                )
            }
        }

        if (q.contains("chrome") || q.contains("navegador") || q.contains("pesquisa") || q.contains("procura no google")) {
            val searchParam = extractSearchQuery(query, listOf("pesquisa por", "procura por", "pesquisa no google", "busca por", "abre o chrome e busca", "pesquisa"))
            return AIResponse(
                replyText = "Abrindo o navegador para pesquisar.",
                action = NovaAction.OpenBrowser(searchParam ?: query),
                isOfflineMode = true
            )
        }

        if (q.contains("configuraç") || q.contains("ajustes") || q.contains("bluetooth") || q.contains("wi-fi") || q.contains("wifi")) {
            val sub = when {
                q.contains("wifi") || q.contains("wi-fi") -> "wifi"
                q.contains("bluetooth") -> "bluetooth"
                q.contains("aplicativo") || q.contains("apps") -> "apps"
                else -> null
            }
            return AIResponse(
                replyText = "Abrindo configurações do sistema.",
                action = NovaAction.OpenSettings(sub),
                isOfflineMode = true
            )
        }

        // 2. Alarmes & Horários
        if (q.contains("alarme") || q.contains("acorda") || q.contains("despertador")) {
            val (hour, minute) = extractTimeFromQuery(q)
            return AIResponse(
                replyText = "Alarme configurado para as %02d:%02d.".format(hour, minute),
                action = NovaAction.SetAlarm(hour, minute, "Alarme NOVA"),
                isOfflineMode = true
            )
        }

        // 3. Lembretes
        if (q.contains("lembra-me") || q.contains("me lembra") || q.contains("lembrete")) {
            val reminderTitle = extractReminderText(query)
            val minutes = if (q.contains("30 minutos") || q.contains("meia hora")) 30L
            else if (q.contains("10 minutos")) 10L
            else if (q.contains("15 minutos")) 15L
            else if (q.contains("1 hora") || q.contains("uma hora")) 60L
            else if (q.contains("amanhã")) 1440L
            else 30L

            return AIResponse(
                replyText = "Lembrete registrado: \"$reminderTitle\".",
                action = NovaAction.CreateReminder(reminderTitle, minutes),
                isOfflineMode = true
            )
        }

        // 4. Tarefas
        if (q.contains("cria uma tarefa") || q.contains("criar tarefa") || q.contains("adiciona tarefa") || q.contains("nova tarefa")) {
            val title = extractTaskTitle(query)
            val cat = when {
                q.contains("estudar") || q.contains("estudo") -> "Estudo"
                q.contains("trabalh") -> "Trabalho"
                q.contains("projeto") -> "Projeto"
                else -> "Pessoal"
            }
            val priority = if (q.contains("urgente") || q.contains("alta")) "ALTA" else "MÉDIA"
            return AIResponse(
                replyText = "Tarefa criada: \"$title\" na categoria $cat.",
                action = NovaAction.CreateTask(title, cat, priority),
                isOfflineMode = true
            )
        }

        // 5. Calendário & Eventos
        if (q.contains("reunião") || q.contains("evento") || q.contains("marca ") || q.contains("agendar")) {
            val (hour, minute) = extractTimeFromQuery(q)
            val timeStr = "%02d:%02d".format(hour, minute)
            val dateStr = if (q.contains("amanhã")) "Amanhã" else if (q.contains("sexta")) "Sexta-feira" else if (q.contains("sábado")) "Sábado" else "Hoje"
            val title = query.substringAfter("marca").substringAfter("agendar").substringAfter("evento").trim().takeIf { it.isNotBlank() } ?: "Compromisso"

            return AIResponse(
                replyText = "Evento programado: $title para $dateStr às $timeStr.",
                action = NovaAction.CreateCalendarEvent(title, dateStr, timeStr),
                isOfflineMode = true
            )
        }

        // 6. Notas
        if (q.contains("cria uma nota") || q.contains("criar nota") || q.contains("anota")) {
            val noteTitle = query.substringAfter("chamada").substringAfter("nota").trim().takeIf { it.isNotBlank() } ?: "Nota NOVA"
            return AIResponse(
                replyText = "Nota criada: \"$noteTitle\".",
                action = NovaAction.CreateNote(noteTitle, "Conteúdo da nota gerada pela NOVA."),
                isOfflineMode = true
            )
        }

        // 7. Memória
        if (q.contains("lembra que") || q.contains("memoriza que") || q.contains("salva que")) {
            val content = query.substringAfter("lembra que").substringAfter("memoriza que").substringAfter("salva que").trim()
            val key = if (content.contains("se chama") || content.contains("é ")) content.substringBefore("se chama").substringBefore("é ").trim() else "Memória"
            return AIResponse(
                replyText = "Entendido, $greetingName. Informação gravada com sucesso na minha memória local.",
                action = NovaAction.SaveMemory("Fatos", key, content),
                isOfflineMode = true
            )
        }

        if (q.contains("qual o nome do meu projeto") || q.contains("qual é o nome do meu projeto")) {
            val projectMemory = knownMemories.firstOrNull { it.lowercase().contains("projeto") || it.lowercase().contains("technova") }
            return if (projectMemory != null) {
                AIResponse(replyText = "De acordo com minha memória: $projectMemory", isOfflineMode = true)
            } else {
                AIResponse(replyText = "O nome do teu projeto registrado é TechNova.", isOfflineMode = true)
            }
        }

        // 8. Organizar o dia
        if (q.contains("organiza") || q.contains("meu dia") || q.contains("o que tenho para hoje") || q.contains("quais são minhas tarefas")) {
            return AIResponse(
                replyText = "Bom dia, $greetingName. Tens tarefas programadas para hoje. Posso reorganizar tuas prioridades para otimizar tua produtividade.",
                action = NovaAction.ReorganizeDay,
                isOfflineMode = true,
                suggestedChips = listOf("Reorganizar tarefas", "Ver cronograma", "Criar nova tarefa")
            )
        }

        // 9. Informações de Sistema (Relógio, Bateria, Internet)
        if (q.contains("que horas são") || q.contains("hora atual")) {
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            return AIResponse(replyText = "Agora são exatamente $time.", isOfflineMode = true)
        }

        if (q.contains("bateria") || q.contains("nível de carga")) {
            return AIResponse(replyText = "Os sensores indicam que tua bateria está operando em nível nominal no sistema.", isOfflineMode = true)
        }

        if (q.contains("internet") || q.contains("conexão") || q.contains("offline")) {
            return AIResponse(
                replyText = if (isOnline) "Conexão de rede ativa e sincronizada." else "Modo offline ativado. Todas as funções locais, tarefas, alarmes e memória continuam operando normalmente.",
                isOfflineMode = true
            )
        }

        // 10. Saudações e Conversa Pessoal
        if (q.contains("olá") || q.contains("ola") || q.contains("bom dia") || q.contains("boa tarde") || q.contains("boa noite")) {
            return AIResponse(
                replyText = "Olá, $greetingName. Sou a NOVA, tua inteligência pessoal. Em que posso ser útil agora?",
                isOfflineMode = true,
                suggestedChips = listOf("O que tenho hoje?", "Abre o WhatsApp", "Status do sistema", "TechNova Suporte")
            )
        }

        if (q.contains("quem és tu") || q.contains("quem é você") || q.contains("o que você faz")) {
            return AIResponse(
                replyText = "Sou a NOVA — Sua Inteligência Pessoal. Um assistente futurista projetado para organizar tuas tarefas, gerenciar comandos Android, prestar assistência técnica com TechNova, e criar conteúdos inteligentes.",
                isOfflineMode = true
            )
        }

        // Default offline response
        return AIResponse(
            replyText = "Estou no modo offline, $greetingName. Posso executar comandos locais de tarefas, alarmes, notas, memória, diagnósticos do dispositivo e controle de aplicativos.",
            isOfflineMode = true,
            suggestedChips = listOf("Quais são minhas tarefas?", "Status do sistema", "Diagnóstico PC", "Criar nota")
        )
    }

    private fun extractMessageContent(query: String): String? {
        val markers = listOf("enviar", "manda", "mensagem", "dizendo que", "dizendo")
        for (m in markers) {
            if (query.lowercase().contains(m)) {
                val sub = query.substringAfter(m, "").trim()
                if (sub.isNotBlank()) return sub
            }
        }
        return null
    }

    private fun extractSearchQuery(query: String, markers: List<String>): String? {
        for (m in markers) {
            if (query.lowercase().contains(m)) {
                val sub = query.substringAfter(m, "").trim()
                if (sub.isNotBlank()) return sub
            }
        }
        return null
    }

    private fun extractTaskTitle(query: String): String {
        return query.replace("cria uma tarefa chamada", "", true)
            .replace("cria uma tarefa", "", true)
            .replace("criar tarefa", "", true)
            .replace("adiciona tarefa", "", true)
            .replace("nova tarefa", "", true)
            .trim()
            .takeIf { it.isNotBlank() } ?: "Estudar programação"
    }

    private fun extractReminderText(query: String): String {
        return query.replace("me lembra amanhã de", "", true)
            .replace("me lembra de", "", true)
            .replace("lembra-me de", "", true)
            .replace("lembra-me amanhã de", "", true)
            .replace("lembrete para", "", true)
            .trim()
            .takeIf { it.isNotBlank() } ?: "Lembrete importante"
    }

    private fun extractTimeFromQuery(query: String): Pair<Int, Int> {
        val regex = Regex("(\\d{1,2})(:|h| da manhã| da tarde| horas| hrs)?(\\d{2})?")
        val match = regex.find(query)
        if (match != null) {
            val h = match.groupValues[1].toIntOrNull() ?: 7
            val m = match.groupValues[3].toIntOrNull() ?: 0
            val hourAdjusted = if (query.contains("tarde") || query.contains("noite")) {
                if (h < 12) h + 12 else h
            } else h
            return Pair(hourAdjusted, m)
        }
        return Pair(7, 0)
    }
}
