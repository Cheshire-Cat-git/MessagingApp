package com.example.messagingapp.domain.usecase

import com.example.messagingapp.domain.model.DataMessage
import com.example.messagingapp.domain.model.NotificationMessage
import com.example.messagingapp.domain.repository.NotificationRepository

class HandleFcmMessageUseCase(
    private val saveDataMessageUseCase: SaveDataMessageUseCase,
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(data: Map<String, String>, notificationTitle: String?, notificationBody: String?) {
        val type = data["type"] ?: data["message_type"] ?: data["urgency"] ?: ""
        val icon = data["icon"] ?: data["icon_name"] ?: ""
        val title = data["title"] ?: ""
        val hasExpiration = data.containsKey("expiration_date") || data.containsKey("expirationDate") ||
                data.containsKey("expire_date") || data.containsKey("expires")

        val isDataMessage = type.equals("data", ignoreCase = true) ||
                (icon.isNotEmpty() && title.isNotEmpty()) ||
                (title.isNotEmpty() && data.containsKey("message") && !data.containsKey("sender"))

        if (isDataMessage) {
            val msgTitle = if (title.isNotEmpty()) title else (data["title"] ?: "Data Message")
            val msgBody = data["message"] ?: data["text"] ?: data["body"] ?: notificationBody ?: ""
            val msgIcon = if (icon.isNotEmpty()) icon else "ic_info"

            val dataMessage = DataMessage(
                icon = msgIcon,
                title = msgTitle,
                message = msgBody
            )
            saveDataMessageUseCase(dataMessage)
            return
        }

        val sender = data["sender"] ?: data["from"] ?: data["sender_name"] ?: notificationTitle ?: "Unknown Sender"
        val message = data["message"] ?: data["text"] ?: data["body"] ?: notificationBody ?: ""
        val expirationDate = data["expiration_date"] ?: data["expirationDate"] ?: data["expire_date"] ?: data["expires"]

        val isUrgent = type.equals("urgent", ignoreCase = true) || data["is_urgent"] == "true" || hasExpiration

        val notificationMessage = if (isUrgent && !expirationDate.isNullOrEmpty()) {
            NotificationMessage.Urgent(sender, message, expirationDate)
        } else {
            NotificationMessage.Regular(sender, message)
        }

        notificationRepository.showNotification(notificationMessage)
    }
}