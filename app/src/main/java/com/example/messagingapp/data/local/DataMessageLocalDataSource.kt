package com.example.messagingapp.data.local

import com.example.messagingapp.domain.model.DataMessage

interface DataMessageLocalDataSource {
    fun save(message: DataMessage)
    fun getAll(): List<DataMessage>
}