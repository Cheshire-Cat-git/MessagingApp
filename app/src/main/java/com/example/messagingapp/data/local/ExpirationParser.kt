package com.example.messagingapp.data.local

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object ExpirationParser {
    fun parseToMillis(dateStr: String?): Long? {
        if (dateStr.isNullOrBlank()) return null

        dateStr.toLongOrNull()?.let { return it }

        val dateFormats = listOf(
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd"
        )

        for (format in dateFormats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                sdf.timeZone = TimeZone.getDefault()
                val date = sdf.parse(dateStr)
                if (date != null) return date.time
            } catch (e: Exception) {
                // try next format
            }
        }
        return null
    }

    fun formatMillis(millis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(java.util.Date(millis))
    }
}