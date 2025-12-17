package com.company.employeetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.company.employeetracker.data.database.AppDatabase
import com.company.employeetracker.data.database.entities.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessageViewModel(application: Application) : AndroidViewModel(application) {
    private val messageDao = AppDatabase.getDatabase(application).messageDao()

    private val _userMessages = MutableStateFlow<List<Message>>(emptyList())
    val userMessages: StateFlow<List<Message>> = _userMessages

    private val _conversation = MutableStateFlow<List<Message>>(emptyList())
    val conversation: StateFlow<List<Message>> = _conversation

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    private val _unreadMessages = MutableStateFlow<List<Message>>(emptyList())
    val unreadMessages: StateFlow<List<Message>> = _unreadMessages

    fun loadMessagesForUser(userId: Int) {
        viewModelScope.launch {
            messageDao.getMessagesForUser(userId).collect { messages ->
                _userMessages.value = messages
            }
        }
    }

    fun loadConversation(userId: Int, otherUserId: Int) {
        viewModelScope.launch {
            messageDao.getConversation(userId, otherUserId).collect { messages ->
                _conversation.value = messages
            }
        }
    }

    fun loadUnreadCount(userId: Int) {
        viewModelScope.launch {
            messageDao.getUnreadCount(userId).collect { count ->
                _unreadCount.value = count
            }
        }
    }

    fun loadUnreadMessages(userId: Int) {
        viewModelScope.launch {
            messageDao.getUnreadMessages(userId).collect { messages ->
                _unreadMessages.value = messages
            }
        }
    }

    fun sendMessage(senderId: Int, receiverId: Int, messageText: String, messageType: String = "DIRECT", reviewId: Int? = null) {
        viewModelScope.launch {
            val message = Message(
                senderId = senderId,
                receiverId = receiverId,
                message = messageText,
                messageType = messageType,
                relatedReviewId = reviewId
            )
            messageDao.insertMessage(message)
        }
    }

    fun markAsRead(messageId: Int) {
        viewModelScope.launch {
            messageDao.markAsRead(messageId)
        }
    }

    fun markConversationAsRead(userId: Int, senderId: Int) {
        viewModelScope.launch {
            messageDao.markConversationAsRead(userId, senderId)
        }
    }

    fun deleteMessage(message: Message) {
        viewModelScope.launch {
            messageDao.deleteMessage(message)
        }
    }
}