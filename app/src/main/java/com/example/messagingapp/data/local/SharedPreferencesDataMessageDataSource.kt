package com.example.messagingapp.data.local

import android.content.Context
import com.example.messagingapp.domain.model.DataMessage
import org.json.JSONArray
import org.json.JSONObject

class SharedPreferencesDataMessageDataSource(
    context: Context
) : DataMessageLocalDataSource {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun save(message: DataMessage) {
        val currentList = getAll().toMutableList()
        currentList.add(0, message)
        val jsonArray = JSONArray()
        for (item in currentList) {
            val obj = JSONObject().apply {
                put("icon", item.icon)
                put("title", item.title)
                put("message", item.message)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_DATA_MESSAGES, jsonArray.toString()).apply()
    }

    override fun getAll(): List<DataMessage> {
        val jsonString = prefs.getString(KEY_DATA_MESSAGES, null) ?: return emptyList()
        val list = mutableListOf<DataMessage>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    DataMessage(
                        icon = obj.optString("icon", ""),
                        title = obj.optString("title", ""),
                        message = obj.optString("message", "")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    companion object {
        private const val PREFS_NAME = "messaging_app_prefs"
        private const val KEY_DATA_MESSAGES = "data_messages"
    }
}