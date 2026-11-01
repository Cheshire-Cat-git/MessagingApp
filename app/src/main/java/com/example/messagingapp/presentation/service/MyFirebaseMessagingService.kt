package com.example.messagingapp.presentation.service

import android.content.Intent
import com.example.messagingapp.ServiceLocator
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val handleFcmMessageUseCase = ServiceLocator.provideHandleFcmMessageUseCase(applicationContext)
        val data = remoteMessage.data
        val notification = remoteMessage.notification

        handleFcmMessageUseCase(
            data = data,
            notificationTitle = notification?.title,
            notificationBody = notification?.body
        )

        // Send broadcast to update UI
        val dataIntent = Intent(ACTION_DATA_MESSAGE_RECEIVED)
        applicationContext.sendBroadcast(dataIntent)

        val msgIntent = Intent(ACTION_MESSAGE_RECEIVED)
        applicationContext.sendBroadcast(msgIntent)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    companion object {
        const val ACTION_DATA_MESSAGE_RECEIVED = "com.example.messagingapp.DATA_MESSAGE_RECEIVED"
        const val ACTION_MESSAGE_RECEIVED = "com.example.messagingapp.MESSAGE_RECEIVED"
    }
}
