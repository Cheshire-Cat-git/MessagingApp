package com.example.messagingapp.data.local

import com.example.messagingapp.data.local.db.MessageDao
import com.example.messagingapp.data.local.db.MessageEntity
import com.example.messagingapp.domain.model.DataMessage

class RoomDataMessageDataSource(
    private val messageDao: MessageDao
) : DataMessageLocalDataSource {

    override fun save(message: DataMessage) {
        val entity = MessageEntity(
            messageType = "DATA",
            title = message.title,
            message = message.message,
            icon = message.icon
        )
        messageDao.insertMessage(entity)
    }

    override fun getAll(): List<DataMessage> {
        val now = System.currentTimeMillis()
        messageDao.deleteExpiredMessages(now)

        val entities = messageDao.getValidMessagesByType("DATA", now)
        return entities.map { entity ->
            DataMessage(
                icon = entity.icon ?: "ic_info",
                title = entity.title ?: "Data Message",
                message = entity.message
            )
        }
    }
}