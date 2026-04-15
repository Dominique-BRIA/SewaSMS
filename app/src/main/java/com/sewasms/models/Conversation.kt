package com.sewasms.models

import java.util.Date

data class Conversation(
    val id: Long = 0,
    val phoneNumber: String = "",
    val contactName: String = "",
    val lastMessage: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0
) {
    fun getFormattedTime(): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60000 -> "À l'instant"
            diff < 3600000 -> "${diff / 60000}m"
            diff < 86400000 -> "${diff / 3600000}h"
            diff < 604800000 -> "${diff / 86400000}j"
            else -> {
                val date = Date(timestamp)
                java.text.SimpleDateFormat("dd/MM/yy", java.util.Locale.US).format(date)
            }
        }
    }
}
