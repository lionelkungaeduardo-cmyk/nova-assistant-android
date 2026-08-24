package com.example.data.repository

import com.example.data.local.dao.*
import com.example.data.local.model.*
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    val pendingTasks: Flow<List<TaskEntity>> = taskDao.getPendingTasks()

    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
    suspend fun deleteById(id: Long) = taskDao.deleteById(id)
    suspend fun deleteAll() = taskDao.deleteAll()
}

class ReminderRepository(private val reminderDao: ReminderDao) {
    val allReminders: Flow<List<ReminderEntity>> = reminderDao.getAllReminders()
    val activeReminders: Flow<List<ReminderEntity>> = reminderDao.getActiveReminders()

    suspend fun insertReminder(reminder: ReminderEntity): Long = reminderDao.insertReminder(reminder)
    suspend fun updateReminder(reminder: ReminderEntity) = reminderDao.updateReminder(reminder)
    suspend fun deleteReminder(reminder: ReminderEntity) = reminderDao.deleteReminder(reminder)
    suspend fun deleteById(id: Long) = reminderDao.deleteById(id)
}

class CalendarRepository(private val calendarEventDao: CalendarEventDao) {
    val allEvents: Flow<List<CalendarEventEntity>> = calendarEventDao.getAllEvents()

    suspend fun insertEvent(event: CalendarEventEntity): Long = calendarEventDao.insertEvent(event)
    suspend fun deleteEvent(event: CalendarEventEntity) = calendarEventDao.deleteEvent(event)
    suspend fun deleteById(id: Long) = calendarEventDao.deleteById(id)
}

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotes()

    fun searchNotes(query: String): Flow<List<NoteEntity>> = noteDao.searchNotes(query)
    suspend fun insertNote(note: NoteEntity): Long = noteDao.insertNote(note)
    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)
    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)
    suspend fun deleteById(id: Long) = noteDao.deleteById(id)
}

class MemoryRepository(private val memoryDao: MemoryDao) {
    val allMemories: Flow<List<MemoryEntity>> = memoryDao.getAllMemories()

    fun getMemoriesByCategory(category: String): Flow<List<MemoryEntity>> = memoryDao.getMemoriesByCategory(category)
    suspend fun searchMemories(query: String): List<MemoryEntity> = memoryDao.searchMemories(query)
    suspend fun insertMemory(memory: MemoryEntity): Long = memoryDao.insertMemory(memory)
    suspend fun updateMemory(memory: MemoryEntity) = memoryDao.updateMemory(memory)
    suspend fun deleteMemory(memory: MemoryEntity) = memoryDao.deleteMemory(memory)
    suspend fun deleteById(id: Long) = memoryDao.deleteById(id)
    suspend fun deleteAll() = memoryDao.deleteAll()
}

class ChatRepository(private val chatMessageDao: ChatMessageDao) {
    val allMessages: Flow<List<ChatMessageEntity>> = chatMessageDao.getAllMessages()

    suspend fun insertMessage(message: ChatMessageEntity): Long = chatMessageDao.insertMessage(message)
    suspend fun updateMessage(message: ChatMessageEntity) = chatMessageDao.updateMessage(message)
    suspend fun deleteById(id: Long) = chatMessageDao.deleteById(id)
    suspend fun deleteAll() = chatMessageDao.deleteAll()
}

class UserProfileRepository(private val userProfileDao: UserProfileDao) {
    val userProfile: Flow<UserProfileEntity?> = userProfileDao.getProfile()

    suspend fun getProfileDirect(): UserProfileEntity? = userProfileDao.getProfileDirect()
    suspend fun saveProfile(profile: UserProfileEntity) = userProfileDao.insertOrUpdateProfile(profile)
}
