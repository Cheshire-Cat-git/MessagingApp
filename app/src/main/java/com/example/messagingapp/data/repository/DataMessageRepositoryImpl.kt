package com.example.messagingapp.data.repository

import com.example.messagingapp.data.local.DataMessageLocalDataSource
import com.example.messagingapp.domain.model.DataMessage
import com.example.messagingapp.domain.repository.DataMessageRepository

class DataMessageRepositoryImpl(
    private val localDataSource: DataMessageLocalDataSource
) : DataMessageRepository {

    override fun saveDataMessage(message: DataMessage) {
        localDataSource.save(message)
    }

    override fun getDataMessages(): List<DataMessage> {
        return localDataSource.getAll()
    }
}