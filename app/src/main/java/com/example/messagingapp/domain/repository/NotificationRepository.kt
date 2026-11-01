package com.example.messagingapp.domain.repository

import com.example.messagingapp.domain.model.NotificationMessage

interface NotificationRepository {
    fun createNotificationChannels()
    fun showNotification(notification: NotificationMessage)
    fun saveNotification(notification: NotificationMessage)
    fun getNotifications(): List<NotificationMessage>
}