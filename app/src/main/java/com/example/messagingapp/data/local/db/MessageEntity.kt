package com.example.messagingapp.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val messageType: String, // "URGENT", "REGULAR", "DATA"
    val sender: String? = null,
    val title: String? = null,
    val message: String,
    val icon: String? = null,
    val expirationTimeMillis: Long? = null,
    val receivedTimeMillis: Long = System.currentTimeMillis()
)