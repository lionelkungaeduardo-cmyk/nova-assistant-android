package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.*
import com.example.automation.AppLauncherService
import com.example.automation.DeviceService
import com.example.automation.DeviceTelemetry
import com.example.data.local.NovaDatabase
import com.example.data.local.model.*
import com.example.data.repository.*
import com.example.voice.VoiceService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NovaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = NovaDatabase.getDatabase(application)
    val taskRepo = TaskRepository(db.taskDao())
    val reminderRepo = ReminderRepository(db.reminderDao())
    val calendarRepo = CalendarRepository(db.calendarEventDao())
    val noteRepo = NoteRepository(db.noteDao())
    val memoryRepo = MemoryRepository(db.memoryDao())
    val chatRepo = ChatRepository(db.chatMessageDao())
    val profileRepo = UserProfileRepository(db.userProfileDao())

    val deviceService = DeviceService(application)
    val appLauncherService = AppLauncherService(application)
    val voiceService = VoiceService(application)

    private val offlineAIService = OfflineAIService()
    private val onlineAIService = OnlineAIService(offlineAIService)

    // UI States
    private val _aiState = MutableStateFlow(AIState.IDLE)
    val aiState: StateFlow<AIState> = _aiState.asStateFlow()

    private val _telemetry = MutableStateFlow(deviceService.getTelemetry())
    val telemetry: StateFlow<DeviceTelemetry> = _telemetry.asStateFlow()

    private val _statusMessage = MutableStateFlow("Pronta para ajudar.")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    // Data streams
    val tasks: StateFlow<List<TaskEntity>> = taskRepo.allTasks.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val pendingTasks: StateFlow<List<TaskEntity>> = taskRepo.pendingTasks.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val reminders: StateFlow<List<ReminderEntity>> = reminderRepo.allReminders.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val calendarEvents: StateFlow<List<CalendarEventEntity>> = calendarRepo.allEvents.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val notes: StateFlow<List<NoteEntity>> = noteRepo.allNotes.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val memories: StateFlow<List<MemoryEntity>> = memoryRepo.allMemories.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val chatMessages: StateFlow<List<ChatMessageEntity>> = chatRepo.allMessages.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val userProfile: StateFlow<UserProfileEntity?> = profileRepo.userProfile.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    val isListening: StateFlow<Boolean> = voiceService.isListening
    val isSpeaking: StateFlow<Boolean> = voiceService.isSpeaking
    val rmsLevel: StateFlow<Float> = voiceService.rmsLevel

    init {
        // Refresh telemetry periodically
        viewModelScope.launch(Dispatchers.IO) {
            seedDefaultDataIfEmpty()
            while (true) {
                _telemetry.value = deviceService.getTelemetry()
                delay(4000)
            }
        }

        // Synchronize speaking state with AIState
        viewModelScope.launch {
            voiceService.isSpeaking.collect { speaking ->
                if (speaking) {
                    _aiState.value = AIState.SPEAKING
                } else if (_aiState.value == AIState.SPEAKING) {
                    _aiState.value = AIState.IDLE
                }
            }
        }
    }

    private suspend fun seedDefaultDataIfEmpty() {
        val currentProfile = profileRepo.getProfileDirect()
        if (currentProfile == null) {
            profileRepo.saveProfile(
                UserProfileEntity(
                    id = 1,
                    userName = "Leonel",
                    language = "Português",
                    voiceSpeed = 1.0f,
                    personality = "Objetiva & Futurista",
                    offlineOnly = false,
                    powerSaving = false,
                    isFirstUseDone = true
                )
            )

            // Seed initial tasks
            taskRepo.insertTask(TaskEntity(title = "Estudar informática e IA", description = "Revisar conceitos de redes neurais", time = "09:00", priority = "ALTA", category = "Estudo"))
            taskRepo.insertTask(TaskEntity(title = "Desenvolver projeto TechNova", description = "Avançar na arquitetura do app", time = "11:30", priority = "ALTA", category = "Projeto"))
            taskRepo.insertTask(TaskEntity(title = "Revisar compromissos do dia", description = "Sincronizar agenda", time = "15:00", priority = "MÉDIA", category = "Trabalho"))
            taskRepo.insertTask(TaskEntity(title = "Treino físico diário", description = "Caminhada ou academia", time = "18:00", priority = "MÉDIA", category = "Pessoal"))

            // Seed initial memories
            memoryRepo.insertMemory(MemoryEntity(category = "Projetos", key = "Projeto Principal", value = "TechNova"))
            memoryRepo.insertMemory(MemoryEntity(category = "Perfil", key = "Nome do Usuário", value = "Leonel"))
            memoryRepo.insertMemory(MemoryEntity(category = "Preferências", key = "Tema", value = "Futurista Holográfico Escuro"))

            // Seed initial notes
            noteRepo.insertNote(NoteEntity(title = "Ideias para o TechNova", content = "1. Interface com HUD futurista\n2. Sistema de voz responsivo com feedback tátil\n3. Diagnóstico de PC e Android integrado", isPinned = true))

            // Seed initial events
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            calendarRepo.insertEvent(CalendarEventEntity(title = "Reunião de Alinhamento TechNova", description = "Apresentação dos módulos principais", date = todayStr, startTime = "18:00", endTime = "19:00", category = "Projeto"))

            // Initial welcome chat
            chatRepo.insertMessage(
                ChatMessageEntity(
                    text = "Saudações, Leonel. Sou a NOVA, tua inteligência pessoal. Todos os módulos de tarefas, voz, automação e diagnóstico estão operacionais.",
                    isUser = false
                )
            )
        }
    }

    fun submitQuery(queryText: String, readAloud: Boolean = false) {
        if (queryText.isBlank()) return

        val profile = userProfile.value ?: UserProfileEntity()
        viewModelScope.launch(Dispatchers.IO) {
            // 1. Record user message
            chatRepo.insertMessage(ChatMessageEntity(text = queryText, isUser = true))

            _aiState.value = AIState.THINKING
            _statusMessage.value = "Processando comando..."

            val memoriesList = memories.value.map { "${it.key}: ${it.value}" }
            val isOnline = _telemetry.value.isOnline && !profile.offlineOnly

            val aiServiceToUse = if (isOnline) onlineAIService else offlineAIService
            val response = aiServiceToUse.processQuery(queryText, profile.userName, isOnline, memoriesList)

            // Execute action if present
            var actionPayload: String? = null
            var actionType: String? = null

            if (response.action != null) {
                _aiState.value = AIState.EXECUTING
                _statusMessage.value = "Executando ação..."
                when (val act = response.action) {
                    is NovaAction.OpenApp -> {
                        actionType = "OPEN_APP"
                        actionPayload = act.appName
                        val res = appLauncherService.openApp(act.packageName, act.appName)
                        _statusMessage.value = res.message
                    }
                    is NovaAction.SendWhatsApp -> {
                        actionType = "WHATSAPP"
                        actionPayload = act.message
                        val res = appLauncherService.openWhatsApp(message = act.message, phone = act.phone)
                        _statusMessage.value = res.message
                    }
                    is NovaAction.SearchYouTube -> {
                        actionType = "YOUTUBE"
                        actionPayload = act.query
                        val res = appLauncherService.openYouTube(act.query)
                        _statusMessage.value = res.message
                    }
                    is NovaAction.OpenBrowser -> {
                        actionType = "BROWSER"
                        actionPayload = act.urlOrQuery
                        val res = appLauncherService.openBrowser(act.urlOrQuery)
                        _statusMessage.value = res.message
                    }
                    is NovaAction.CreateTask -> {
                        actionType = "CREATE_TASK"
                        actionPayload = act.title
                        taskRepo.insertTask(TaskEntity(title = act.title, category = act.category, priority = act.priority, time = act.time))
                    }
                    is NovaAction.SetAlarm -> {
                        actionType = "SET_ALARM"
                        actionPayload = "${act.hour}:${act.minutes}"
                        appLauncherService.setSystemAlarm(act.hour, act.minutes, act.label)
                    }
                    is NovaAction.CreateReminder -> {
                        actionType = "CREATE_REMINDER"
                        actionPayload = act.title
                        val triggerTime = System.currentTimeMillis() + (act.minutesFromNow * 60 * 1000)
                        val remId = reminderRepo.insertReminder(ReminderEntity(title = act.title, timestampMillis = triggerTime))
                        appLauncherService.scheduleLocalReminder(remId, act.title, triggerTime)
                    }
                    is NovaAction.CreateCalendarEvent -> {
                        actionType = "CALENDAR"
                        actionPayload = act.title
                        calendarRepo.insertEvent(CalendarEventEntity(title = act.title, date = act.date, startTime = act.startTime))
                    }
                    is NovaAction.CreateNote -> {
                        actionType = "CREATE_NOTE"
                        actionPayload = act.title
                        noteRepo.insertNote(NoteEntity(title = act.title, content = act.content))
                    }
                    is NovaAction.SaveMemory -> {
                        actionType = "SAVE_MEMORY"
                        actionPayload = "${act.key}: ${act.value}"
                        memoryRepo.insertMemory(MemoryEntity(category = act.category, key = act.key, value = act.value))
                    }
                    is NovaAction.OpenSettings -> {
                        actionType = "SETTINGS"
                        actionPayload = act.subscreen
                        appLauncherService.openSettings(act.subscreen)
                    }
                    is NovaAction.ReorganizeDay -> {
                        actionType = "REORGANIZE"
                        reorganizeDayPlan()
                    }
                    else -> {}
                }
            }

            // Save AI reply in DB
            chatRepo.insertMessage(
                ChatMessageEntity(
                    text = response.replyText,
                    isUser = false,
                    actionType = actionType,
                    actionPayload = actionPayload,
                    isExecuted = true
                )
            )

            _statusMessage.value = if (response.isOfflineMode) "Modo offline • Executado" else "Online • Sincronizado"

            if (readAloud) {
                _aiState.value = AIState.SPEAKING
                voiceService.setSpeechRate(profile.voiceSpeed)
                voiceService.speak(response.replyText)
            } else {
                _aiState.value = AIState.IDLE
            }
        }
    }

    fun startVoiceInput() {
        _aiState.value = AIState.LISTENING
        _statusMessage.value = "Ouvindo comando de voz..."
        voiceService.startListening(
            onResult = { spokenText ->
                _statusMessage.value = "Comando: \"$spokenText\""
                submitQuery(spokenText, readAloud = true)
            },
            onError = { errMsg ->
                _aiState.value = AIState.ERROR
                _statusMessage.value = errMsg
                viewModelScope.launch {
                    delay(2500)
                    _aiState.value = AIState.IDLE
                    _statusMessage.value = "Pronta para ajudar."
                }
            }
        )
    }

    fun stopVoice() {
        voiceService.stopListening()
        voiceService.stopSpeaking()
        _aiState.value = AIState.IDLE
    }

    fun speakText(text: String) {
        val speed = userProfile.value?.voiceSpeed ?: 1.0f
        voiceService.setSpeechRate(speed)
        voiceService.speak(text)
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepo.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun addTask(title: String, description: String, time: String, priority: String, category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepo.insertTask(TaskEntity(title = title, description = description, time = time, priority = priority, category = category))
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            taskRepo.deleteTask(task)
        }
    }

    fun reorganizeDayPlan() {
        viewModelScope.launch(Dispatchers.IO) {
            val all = tasks.value
            // Sort by priority (ALTA first, then MÉDIA, then BAIXA)
            val sorted = all.sortedWith(
                compareBy<TaskEntity> { it.isCompleted }
                    .thenBy {
                        when (it.priority) {
                            "ALTA" -> 0
                            "MÉDIA" -> 1
                            else -> 2
                        }
                    }
            )
            // Re-assign optimal times
            val times = listOf("08:30", "10:00", "11:30", "14:00", "16:00", "18:30", "20:00")
            sorted.forEachIndexed { index, task ->
                if (!task.isCompleted) {
                    val assignedTime = times.getOrElse(index) { "18:00" }
                    taskRepo.updateTask(task.copy(time = assignedTime))
                }
            }
            _statusMessage.value = "Cronograma reorganizado com foco nas tarefas de alta prioridade."
        }
    }

    fun addNote(title: String, content: String, category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepo.insertNote(NoteEntity(title = title, content = content, category = category))
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            noteRepo.deleteNote(note)
        }
    }

    fun addMemory(category: String, key: String, value: String) {
        viewModelScope.launch(Dispatchers.IO) {
            memoryRepo.insertMemory(MemoryEntity(category = category, key = key, value = value))
        }
    }

    fun deleteMemory(memory: MemoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            memoryRepo.deleteMemory(memory)
        }
    }

    fun forgetAllMemories() {
        viewModelScope.launch(Dispatchers.IO) {
            memoryRepo.deleteAll()
        }
    }

    fun clearChat() {
        viewModelScope.launch(Dispatchers.IO) {
            chatRepo.deleteAll()
        }
    }

    fun updateProfile(profile: UserProfileEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            profileRepo.saveProfile(profile)
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceService.destroy()
    }
}
