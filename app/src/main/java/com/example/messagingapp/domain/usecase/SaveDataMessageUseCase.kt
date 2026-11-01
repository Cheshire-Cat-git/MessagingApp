package com.example.messagingapp.domain.usecase

import com.example.messagingapp.domain.model.DataMessage
import com.example.messagingapp.domain.repository.DataMessageRepository

class SaveDataMessageUseCase(
    private val repository: DataMessageRepository
) {
    operator fun invoke(message: DataMessage) {
        repository.saveDataMessage(message)
    }
}