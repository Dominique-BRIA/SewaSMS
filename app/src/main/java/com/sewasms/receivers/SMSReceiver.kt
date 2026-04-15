package com.sewasms.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.sewasms.models.Message
import com.sewasms.utils.AudioConverter
import com.sewasms.utils.DatabaseHelper
import com.sewasms.utils.NotificationHelper
import java.io.File

class SMSReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        context ?: return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val notificationHelper = NotificationHelper(context)

        for (message in messages) {
            val senderNumber = message.originatingAddress ?: "Inconnu"
            val messageBody = message.messageBody
            val timestamp = message.timestampMillis

            Log.d("SMSReceiver", "SMS de $senderNumber")

            val db = DatabaseHelper(context)
            val conversationId = db.getConversationId(senderNumber)

            if (AudioConverter.isAudioMessage(messageBody)) {
                handleAudioMessage(context, db, senderNumber, conversationId, messageBody, timestamp)
                notificationHelper.showAudioNotification(senderNumber)
            } else {
                handleTextMessage(db, senderNumber, conversationId, messageBody, timestamp)
                notificationHelper.showSMSNotification(senderNumber, messageBody)
            }
        }
    }

    private fun handleAudioMessage(
        context: Context,
        db: DatabaseHelper,
        sender: String,
        conversationId: Long,
        encodedAudio: String,
        timestamp: Long
    ) {
        try {
            val audioDir = File(context.cacheDir, "received_audio")
            audioDir.mkdirs()

            val audioFile = File(audioDir, "audio_${timestamp}.m4a")

            if (AudioConverter.smsToAudio(encodedAudio, audioFile)) {
                db.addMessage(
                    Message(
                        conversationId = conversationId,
                        phoneNumber = sender,
                        content = encodedAudio,
                        isOutgoing = false,
                        isAudio = true,
                        audioPath = audioFile.absolutePath,
                        timestamp = timestamp
                    )
                )
                db.updateConversation(sender, "🎤 Audio")
                Log.d("SMSReceiver", "Audio décodé de $sender")
            }
        } catch (e: Exception) {
            Log.e("SMSReceiver", "Erreur audio", e)
        }
    }

    private fun handleTextMessage(
        db: DatabaseHelper,
        sender: String,
        conversationId: Long,
        messageBody: String,
        timestamp: Long
    ) {
        db.addMessage(
            Message(
                conversationId = conversationId,
                phoneNumber = sender,
                content = messageBody,
                isOutgoing = false,
                isAudio = false,
                timestamp = timestamp
            )
        )
        db.updateConversation(sender, messageBody)
    }
}
