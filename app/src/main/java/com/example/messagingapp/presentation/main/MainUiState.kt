package com.example.messagingapp.presentation.main

import com.example.messagingapp.domain.model.DataMessage
import com.example.messagingapp.domain.model.NotificationMessage

data class MainUiState(
    val dataMessages: List<DataMessage> = emptyList(),
    val notifications: List<NotificationMessage> = emptyList(),
    val activeNotification: NotificationMessage? = null
)