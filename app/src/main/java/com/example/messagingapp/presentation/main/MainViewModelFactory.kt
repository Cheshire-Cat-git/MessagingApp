package com.example.messagingapp.presentation.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.messagingapp.ServiceLocator

class MainViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            val getDataMessagesUseCase = ServiceLocator.provideGetDataMessagesUseCase(context)
            val getNotificationsUseCase = ServiceLocator.provideGetNotificationsUseCase(context)
            val notificationRepository = ServiceLocator.provideNotificationRepository(context)
            return MainViewModel(getDataMessagesUseCase, getNotificationsUseCase, notificationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
