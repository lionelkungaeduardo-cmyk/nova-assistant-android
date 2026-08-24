package com.example.ai

enum class AIState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING,
    EXECUTING,
    OFFLINE,
    ERROR
}

sealed class NovaAction {
    data class OpenApp(val packageName: String, val appName: String) : NovaAction()
    data class SendWhatsApp(val phone: String? = null, val message: String? = null) : NovaAction()
    data class SearchYouTube(val query: String) : NovaAction()
    data class OpenBrowser(val urlOrQuery: String) : NovaAction()
    data class CreateTask(val title: String, val category: String = "Pessoal", val priority: String = "MÉDIA", val time: String = "") : NovaAction()
    data class SetAlarm(val hour: Int, val minutes: Int, val label: String) : NovaAction()
    data class CreateReminder(val title: String, val minutesFromNow: Long) : NovaAction()
    data class CreateCalendarEvent(val title: String, val date: String, val startTime: String) : NovaAction()
    data class CreateNote(val title: String, val content: String) : NovaAction()
    data class SaveMemory(val category: String, val key: String, val value: String) : NovaAction()
    data class OpenSettings(val subscreen: String? = null) : NovaAction()
    data object ReorganizeDay : NovaAction()
    data class TechSupport(val category: String, val query: String) : NovaAction()
    data class GenerateWebSite(val prompt: String) : NovaAction()
    data class GenerateContent(val platform: String, val topic: String) : NovaAction()
}

data class AIResponse(
    val replyText: String,
    val action: NovaAction? = null,
    val isOfflineMode: Boolean = false,
    val suggestedChips: List<String> = emptyList()
)

interface AIService {
    suspend fun processQuery(
        query: String,
        userProfileName: String,
        isOnline: Boolean,
        knownMemories: List<String> = emptyList()
    ): AIResponse
}
