package com.sewasms.utils

import android.util.Base64
import java.io.File

object AudioConverter {

    fun audioToSMS(audioFile: File): String {
        return try {
            val audioBytes = audioFile.readBytes()
            val base64String = Base64.encodeToString(audioBytes, Base64.NO_WRAP)
            "````AUDIO\n$base64String"
        } catch (e: Exception) {
            "ERROR: ${e.message}"
        }
    }

    fun smsToAudio(smsContent: String, outputFile: File): Boolean {
        return try {
            val base64Content = smsContent
                .replace("````AUDIO\n", "")
                .replace("````AUDIO", "")
                .trim()

            val audioBytes = Base64.decode(base64Content, Base64.NO_WRAP)
            outputFile.writeBytes(audioBytes)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun isAudioMessage(message: String): Boolean {
        return message.contains("````AUDIO")
    }

    fun estimateSMSCount(audioFile: File): Int {
        val base64Size = (audioFile.length() * 4 / 3).toInt()
        return maxOf(1, (base64Size / 160))
    }

    fun extractAudioContent(message: String): String {
        return message
            .replace("````AUDIO\n", "")
            .replace("````AUDIO", "")
            .trim()
    }
}
