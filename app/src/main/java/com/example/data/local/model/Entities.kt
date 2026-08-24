package com.example.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val priority: String = "MÉDIA", // ALTA, MÉDIA, BAIXA
    val category: String = "Pessoal", // Estudo, Trabalho, Pessoal, Projeto, Importante, Outro
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val timestampMillis: Long,
    val isTriggered: Boolean = false,
    val isRecurring: Boolean = false,
    val repeatIntervalMinutes: Long = 0
)

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: String,
    val startTime: String,
    val endTime: String = "",
    val category: String = "Geral"
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String = "Geral",
    val isPinned: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // Perfil, Preferências, Objetivos, Projetos, Fatos, Importante
    val key: String,
    val value: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: String? = null, // OPEN_APP, CREATE_TASK, SET_ALARM, TECH_SUPPORT, WEB_PREVIEW
    val actionPayload: String? = null,
    val isExecuted: Boolean = false
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val userName: String = "Leonel",
    val language: String = "Português",
    val voiceSpeed: Float = 1.0f,
    val personality: String = "Objetiva & Futurista", // Objetiva & Futurista, Amigável & Empática, Científica
    val offlineOnly: Boolean = false,
    val powerSaving: Boolean = false,
    val isFirstUseDone: Boolean = false
)
