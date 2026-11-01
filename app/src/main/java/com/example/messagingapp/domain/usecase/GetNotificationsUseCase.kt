package com.example.messagingapp.domain.usecase

import com.example.messagingapp.domain.model.NotificationMessage
import com.example.messagingapp.domain.repository.NotificationRepository

class GetNotificationsUseCase(
    private val repository: NotificationRepository
) {
    operator fun invoke(): List<NotificationMessage> {
        return repository.getNotifications()
    }
}
