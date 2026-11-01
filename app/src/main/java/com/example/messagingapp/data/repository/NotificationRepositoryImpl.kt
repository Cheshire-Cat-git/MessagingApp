package com.example.messagingapp.data.repository

import com.example.messagingapp.data.local.ExpirationParser
import com.example.messagingapp.data.local.db.MessageDao
import com.example.messagingapp.data.local.db.MessageEntity
import com.example.messagingapp.data.notification.SystemNotificationManager
import com.example.messagingapp.domain.model.NotificationMessage
import com.example.messagingapp.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val notificationManager: SystemNotificationManager,
    private val messageDao: MessageDao? = null
) : NotificationRepository {

    override fun createNotificationChannels() {
        notificationManager.createChannels()
    }

    override fun showNotification(notification: NotificationMessage) {
        notificationManager.showNotification(notification)
        saveNotification(notification)
    }

    override fun saveNotification(notification: NotificationMessage) {
        messageDao?.let { dao ->
            val now = System.currentTimeMillis()
            dao.deleteExpiredMessages(now)

            val entity = when (notification) {
                is NotificationMessage.Urgent -> {
                    val expMillis = ExpirationParser.parseToMillis(notification.expirationDate)
                    MessageEntity(
                        messageType = "URGENT",
                        sender = notification.sender,
                        message = notification.message,
                        expirationTimeMillis = expMillis,
                        receivedTimeMillis = now
                    )
                }
                is NotificationMessage.Regular -> {
                    MessageEntity(
                        messageType = "REGULAR",
                        sender = notification.sender,
                        message = notification.message,
                        expirationTimeMillis = null,
                        receivedTimeMillis = now
                    )
                }
            }
            dao.insertMessage(entity)
        }
    }

    override fun getNotifications(): List<NotificationMessage> {
        val dao = messageDao ?: return emptyList()
        val now = System.currentTimeMillis()
        dao.deleteExpiredMessages(now)

        val entities = dao.getValidMessages(now).filter {
            it.messageType == "URGENT" || it.messageType == "REGULAR"
        }

        return entities.map { entity ->
            if (entity.messageType == "URGENT") {
                val expStr = entity.expirationTimeMillis?.let { ExpirationParser.formatMillis(it) } ?: ""
                NotificationMessage.Urgent(
                    sender = entity.sender ?: "Unknown",
                    message = entity.message,
                    expirationDate = expStr
                )
            } else {
                NotificationMessage.Regular(
                    sender = entity.sender ?: "Unknown",
                    message = entity.message
                )
            }
        }
    }
}