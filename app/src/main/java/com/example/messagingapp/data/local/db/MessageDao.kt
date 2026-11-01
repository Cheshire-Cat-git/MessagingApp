package com.example.messagingapp.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMessage(message: MessageEntity): Long

    @Query("SELECT * FROM messages WHERE expirationTimeMillis IS NULL OR expirationTimeMillis > :currentTimeMillis ORDER BY receivedTimeMillis DESC")
    fun getValidMessages(currentTimeMillis: Long = System.currentTimeMillis()): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE messageType = :type AND (expirationTimeMillis IS NULL OR expirationTimeMillis > :currentTimeMillis) ORDER BY receivedTimeMillis DESC")
    fun getValidMessagesByType(type: String, currentTimeMillis: Long = System.currentTimeMillis()): List<MessageEntity>

    @Query("DELETE FROM messages WHERE expirationTimeMillis IS NOT NULL AND expirationTimeMillis <= :currentTimeMillis")
    fun deleteExpiredMessages(currentTimeMillis: Long = System.currentTimeMillis()): Int

    @Query("SELECT * FROM messages ORDER BY receivedTimeMillis DESC")
    fun getAllMessages(): List<MessageEntity>

    @Query("DELETE FROM messages")
    fun deleteAll()
}