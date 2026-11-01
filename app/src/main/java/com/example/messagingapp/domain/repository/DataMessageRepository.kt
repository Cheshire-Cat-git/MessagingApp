package com.example.messagingapp.domain.repository

import com.example.messagingapp.domain.model.DataMessage

interface DataMessageRepository {
    fun saveDataMessage(message: DataMessage)
    fun getDataMessages(): List<DataMessage>
}