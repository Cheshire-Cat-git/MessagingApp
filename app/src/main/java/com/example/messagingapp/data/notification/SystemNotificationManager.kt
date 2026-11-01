package com.example.messagingapp.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.messagingapp.R
import com.example.messagingapp.domain.model.NotificationMessage
import com.example.messagingapp.presentation.main.MainActivity

class SystemNotificationManager(
    private val context: Context
) {
    fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val urgentChannel = NotificationChannel(
                CHANNEL_ID_URGENT,
                "Urgent Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for urgent notifications with sound"
                val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
                setSound(soundUri, audioAttributes)
                enableVibration(true)
            }

            val regularChannel = NotificationChannel(
                CHANNEL_ID_REGULAR,
                "Regular Notifications",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for regular notifications without sound"
                setSound(null, null)
                enableVibration(false)
            }

            notificationManager.createNotificationChannel(urgentChannel)
            notificationManager.createNotificationChannel(regularChannel)
        }
    }

    fun showNotification(notification: NotificationMessage) {
        createChannels()

        val isUrgent = notification is NotificationMessage.Urgent
        val channelId = if (isUrgent) CHANNEL_ID_URGENT else CHANNEL_ID_REGULAR

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("type", if (isUrgent) "urgent" else "regular")
            putExtra("sender", notification.sender)
            putExtra("message", notification.message)
            if (notification is NotificationMessage.Urgent) {
                putExtra("expiration_date", notification.expirationDate)
                putExtra("expirationDate", notification.expirationDate)
            }
        }

        val notificationId = System.currentTimeMillis().toInt()
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(notification.sender)
            .setContentText(notification.message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(if (isUrgent) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_LOW)

        if (isUrgent) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            builder.setSound(soundUri)
            builder.setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
        } else {
            builder.setSound(null)
            builder.setDefaults(0)
        }

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    companion object {
        const val CHANNEL_ID_URGENT = "urgent_channel"
        const val CHANNEL_ID_REGULAR = "regular_channel"
    }
}