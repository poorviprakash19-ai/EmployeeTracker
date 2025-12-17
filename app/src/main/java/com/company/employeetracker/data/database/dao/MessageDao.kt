package com.company.employeetracker.data.database.dao

import androidx.room.*
import com.company.employeetracker.data.database.entities.Message
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE (senderId = :userId OR receiverId = :userId) ORDER BY timestamp DESC")
    fun getMessagesForUser(userId: Int): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE senderId = :senderId AND receiverId = :receiverId OR senderId = :receiverId AND receiverId = :senderId ORDER BY timestamp ASC")
    fun getConversation(senderId: Int, receiverId: Int): Flow<List<Message>>

    @Query("SELECT COUNT(*) FROM messages WHERE receiverId = :userId AND isRead = 0")
    fun getUnreadCount(userId: Int): Flow<Int>

    @Query("SELECT * FROM messages WHERE receiverId = :userId AND isRead = 0 ORDER BY timestamp DESC")
    fun getUnreadMessages(userId: Int): Flow<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message): Long

    @Update
    suspend fun updateMessage(message: Message)

    @Query("UPDATE messages SET isRead = 1 WHERE id = :messageId")
    suspend fun markAsRead(messageId: Int)

    @Query("UPDATE messages SET isRead = 1 WHERE receiverId = :userId AND senderId = :senderId")
    suspend fun markConversationAsRead(userId: Int, senderId: Int)

    @Delete
    suspend fun deleteMessage(message: Message)

    @Query("SELECT DISTINCT senderId FROM messages WHERE receiverId = :userId UNION SELECT DISTINCT receiverId FROM messages WHERE senderId = :userId")
    fun getUserConversations(userId: Int): Flow<List<Int>>
}