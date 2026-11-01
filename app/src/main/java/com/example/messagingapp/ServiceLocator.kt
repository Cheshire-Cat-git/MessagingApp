package com.example.messagingapp

import android.content.Context
import com.example.messagingapp.data.local.RoomDataMessageDataSource
import com.example.messagingapp.data.local.db.AppDatabase
import com.example.messagingapp.data.notification.SystemNotificationManager
import com.example.messagingapp.data.repository.DataMessageRepositoryImpl
import com.example.messagingapp.data.repository.NotificationRepositoryImpl
import com.example.messagingapp.domain.repository.DataMessageRepository
import com.example.messagingapp.domain.repository.NotificationRepository
import com.example.messagingapp.domain.usecase.GetDataMessagesUseCase
import com.example.messagingapp.domain.usecase.GetNotificationsUseCase
import com.example.messagingapp.domain.usecase.HandleFcmMessageUseCase
import com.example.messagingapp.domain.usecase.SaveDataMessageUseCase

object ServiceLocator {

    @Volatile
    private var dataMessageRepository: DataMessageRepository? = null

    @Volatile
    private var notificationRepository: NotificationRepository? = null

    fun provideAppDatabase(context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    fun provideDataMessageRepository(context: Context): DataMessageRepository {
        return dataMessageRepository ?: synchronized(this) {
            val dao = provideAppDatabase(context).messageDao()
            dataMessageRepository ?: DataMessageRepositoryImpl(
                RoomDataMessageDataSource(dao)
            ).also { dataMessageRepository = it }
        }
    }

    fun provideNotificationRepository(context: Context): NotificationRepository {
        return notificationRepository ?: synchronized(this) {
            val dao = provideAppDatabase(context).messageDao()
            notificationRepository ?: NotificationRepositoryImpl(
                SystemNotificationManager(context.applicationContext),
                dao
            ).also { notificationRepository = it }
        }
    }

    fun provideGetDataMessagesUseCase(context: Context): GetDataMessagesUseCase {
        return GetDataMessagesUseCase(provideDataMessageRepository(context))
    }

    fun provideGetNotificationsUseCase(context: Context): GetNotificationsUseCase {
        return GetNotificationsUseCase(provideNotificationRepository(context))
    }

    fun provideSaveDataMessageUseCase(context: Context): SaveDataMessageUseCase {
        return SaveDataMessageUseCase(provideDataMessageRepository(context))
    }

    fun provideHandleFcmMessageUseCase(context: Context): HandleFcmMessageUseCase {
        return HandleFcmMessageUseCase(
            saveDataMessageUseCase = provideSaveDataMessageUseCase(context),
            notificationRepository = provideNotificationRepository(context)
        )
    }
}