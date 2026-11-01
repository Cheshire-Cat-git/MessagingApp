package com.example.messagingapp.domain.model

sealed class NotificationMessage {
    abstract val sender: String
    abstract val message: String

    data class Urgent(
        override val sender: String,
        override val message: String,
        val expirationDate: String
    ) : NotificationMessage()

    data class Regular(
        override val sender: String,
        override val message: String
    ) : NotificationMessage()
}