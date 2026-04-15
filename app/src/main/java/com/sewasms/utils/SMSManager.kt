package com.sewasms.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log
import java.io.File

class SMSManager(private val context: Context) {

    fun sendAudioMessage(phoneNumber: String, audioFile: File): Boolean {
        return try {
            val encodedAudio = AudioConverter.audioToSMS(audioFile)
            sendSMSMessage(phoneNumber, encodedAudio)
            true
        } catch (e: Exception) {
            Log.e("SMSManager", "Erreur audio SMS", e)
            false
        }
    }

    fun sendTextMessage(phoneNumber: String, message: String): Boolean {
        return try {
            sendSMSMessage(phoneNumber, message)
            true
        } catch (e: Exception) {
            Log.e("SMSManager", "Erreur text SMS", e)
            false
        }
    }

    private fun sendSMSMessage(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            val parts = smsManager.divideMessage(message)

            val sentIntents = mutableListOf<PendingIntent>()
            parts.forEach { _ ->
                sentIntents.add(
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        Intent("SMS_SENT"),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                )
            }

            smsManager.sendMultipartTextMessage(
                phoneNumber,
                null,
                parts,
                sentIntents,
                null
            )

            Log.d("SMSManager", "SMS envoyé à $phoneNumber (${parts.size} parties)")
        } catch (e: SecurityException) {
            Log.e("SMSManager", "Permission refusée", e)
        }
    }
}
