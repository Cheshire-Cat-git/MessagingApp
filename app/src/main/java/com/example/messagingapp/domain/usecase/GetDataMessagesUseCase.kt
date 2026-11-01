package com.example.messagingapp.domain.usecase

import com.example.messagingapp.domain.model.DataMessage
import com.example.messagingapp.domain.repository.DataMessageRepository

class GetDataMessagesUseCase(
    private val repository: DataMessageRepository
) {
    operator fun invoke(): List<DataMessage> {
        return repository.getDataMessages()
    }
}