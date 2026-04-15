package com.sewasms.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val smsChannel = NotificationChannel(
                CHANNEL_SMS,
                "SMS Messages",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications pour SMS reçus"
                enableVibration(true)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )
            }

            val audioChannel = NotificationChannel(
                CHANNEL_AUDIO,
                "Audio Messages",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications pour audio reçus"
                enableVibration(true)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )
            }

            notificationManager.createNotificationChannel(smsChannel)
            notificationManager.createNotificationChannel(audioChannel)
        }
    }

    fun showSMSNotification(phoneNumber: String, message: String, notificationId: Int = 1) {
        val notification = NotificationCompat.Builder(context, CHANNEL_SMS)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(phoneNumber)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        notificationManager.notify(notificationId, notification)
    }

    fun showAudioNotification(phoneNumber: String, notificationId: Int = 2) {
        val notification = NotificationCompat.Builder(context, CHANNEL_AUDIO)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentTitle("Message audio de $phoneNumber")
            .setContentText("🎤 Audio reçu - Touchez pour écouter")
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(0, 300, 200, 300, 200, 300))
            .build()

        notificationManager.notify(notificationId, notification)
    }

    companion object {
        private const val CHANNEL_SMS = "sewa_sms_channel"
        private const val CHANNEL_AUDIO = "sewa_audio_channel"
    }
}
