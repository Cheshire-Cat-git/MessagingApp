package com.example.messagingapp.presentation.main

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.example.messagingapp.domain.model.NotificationMessage
import com.example.messagingapp.domain.repository.NotificationRepository
import com.example.messagingapp.domain.usecase.GetDataMessagesUseCase
import com.example.messagingapp.domain.usecase.GetNotificationsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel(
    private val getDataMessagesUseCase: GetDataMessagesUseCase,
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun loadDataMessages() {
        val messages = getDataMessagesUseCase()
        _uiState.update { currentState ->
            currentState.copy(dataMessages = messages)
        }
    }

    fun loadNotifications() {
        val notifications = getNotificationsUseCase()
        _uiState.update { currentState ->
            currentState.copy(notifications = notifications)
        }
    }

    fun handleNotificationIntent(intent: Intent?) {
        if (intent == null) return
        val extras = intent.extras ?: return

        val sender = extras.getString("sender")
            ?: extras.getString("from")
            ?: extras.getString("sender_name")
        val message = extras.getString("message")
            ?: extras.getString("text")
            ?: extras.getString("body")
        val expirationDate = extras.getString("expiration_date")
            ?: extras.getString("expirationDate")
            ?: extras.getString("expire_date")
            ?: extras.getString("expires")
        val type = extras.getString("type")
            ?: extras.getString("message_type")
            ?: extras.getString("urgency")

        if (!sender.isNullOrEmpty() || !message.isNullOrEmpty()) {
            val isUrgent = type?.equals("urgent", ignoreCase = true) == true || !expirationDate.isNullOrEmpty()

            val notification = if (isUrgent && !expirationDate.isNullOrEmpty()) {
                NotificationMessage.Urgent(
                    sender = sender ?: "",
                    message = message ?: "",
                    expirationDate = expirationDate
                )
            } else {
                NotificationMessage.Regular(
                    sender = sender ?: "",
                    message = message ?: ""
                )
            }

            notificationRepository.saveNotification(notification)
            loadNotifications()

            _uiState.update { currentState ->
                currentState.copy(activeNotification = notification)
            }
        }
    }
}
