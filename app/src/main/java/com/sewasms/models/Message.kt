package com.sewasms.models

import java.util.Date

data class Message(
    val id: Long = 0,
    val conversationId: Long = 0,
    val phoneNumber: String = "",
    val content: String = "",
    val isOutgoing: Boolean = false,
    val isAudio: Boolean = false,
    val audioPath: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = true
) {
    fun getFormattedTime(): String {
        val date = Date(timestamp)
        val format = java.text.SimpleDateFormat("HH:mm", java.util.Locale.US)
        return format.format(date)
    }

    fun isAudioMessage(): Boolean = content.contains("````AUDIO")
}
