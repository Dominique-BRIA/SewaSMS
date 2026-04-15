package com.sewasms

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sewasms.adapters.MessageAdapter
import com.sewasms.databinding.ActivityChatBinding
import com.sewasms.models.Message
import com.sewasms.utils.AudioConverter
import com.sewasms.utils.AudioRecorder
import com.sewasms.utils.AudioCompressor
import com.sewasms.utils.DatabaseHelper
import com.sewasms.utils.SMSManager
import com.sewasms.utils.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var db: DatabaseHelper
    private lateinit var smsManager: SMSManager
    private lateinit var audioRecorder: AudioRecorder
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var adapter: MessageAdapter

    private var phoneNumber: String = ""
    private var contactName: String = ""
    private var conversationId: Long = 0L
    private var isRecordingAudio = false
    private var recordingStartTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phoneNumber = intent.getStringExtra("phone_number") ?: ""
        contactName = intent.getStringExtra("contact_name") ?: phoneNumber

        db = DatabaseHelper(this)
        smsManager = SMSManager(this)
        audioRecorder = AudioRecorder(this)
        notificationHelper = NotificationHelper(this)

        conversationId = db.getConversationId(phoneNumber)

        setupUI()
        loadMessages()
    }

    private fun setupUI() {
        binding.tvContactName.text = contactName
        binding.tvPhoneNumber.text = phoneNumber
        binding.btnBack.setOnClickListener { finish() }

        adapter = MessageAdapter(phoneNumber) { message ->
            if (message.isAudio) {
                playAudio(message)
            }
        }

        binding.rvMessages.apply {
            adapter = this@ChatActivity.adapter
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
            }
        }

        binding.btnSend.setOnClickListener { sendTextMessage() }
        binding.btnAudio.setOnClickListener { toggleAudioRecording() }
    }

    private fun loadMessages() {
        GlobalScope.launch(Dispatchers.Main) {
            val messages = db.getMessages(conversationId)
            adapter.submitList(messages)
            binding.rvMessages.scrollToPosition(maxOf(0, messages.size - 1))
        }
    }

    private fun sendTextMessage() {
        val message = binding.etMessage.text.toString().trim()
        if (message.isEmpty()) return

        binding.etMessage.text.clear()

        GlobalScope.launch {
            val success = smsManager.sendTextMessage(phoneNumber, message)

            launch(Dispatchers.Main) {
                if (success) {
                    db.addMessage(
                        Message(
                            conversationId = conversationId,
                            phoneNumber = phoneNumber,
                            content = message,
                            isOutgoing = true,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    db.updateConversation(phoneNumber, message)
                    loadMessages()
                    Toast.makeText(this@ChatActivity, "Envoyé ✓", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ChatActivity, "Erreur d'envoi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun toggleAudioRecording() {
        if (!isRecordingAudio) {
            startRecording()
        } else {
            stopRecording()
        }
    }

    private fun startRecording() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                200
            )
            return
        }

        val audioDir = File(getExternalFilesDir(null), "audio")
        audioDir.mkdirs()

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val audioFile = File(audioDir, "audio_$timestamp.m4a")

        if (audioRecorder.startRecording(audioFile)) {
            isRecordingAudio = true
            recordingStartTime = System.currentTimeMillis()
            binding.btnAudio.apply {
                setImageResource(android.R.drawable.ic_media_pause)
                setBackgroundColor(resources.getColor(android.R.color.holo_red_light, null))
            }
            binding.tvRecordingTime.text = "00:00"
            updateRecordingTime()
        }
    }

    private fun updateRecordingTime() {
        if (isRecordingAudio) {
            val elapsed = (System.currentTimeMillis() - recordingStartTime) / 1000
            val minutes = elapsed / 60
            val seconds = elapsed % 60
            binding.tvRecordingTime.text = String.format("%02d:%02d", minutes, seconds)
            binding.tvRecordingTime.postDelayed({ updateRecordingTime() }, 1000)
        }
    }

    private fun stopRecording() {
        val audioFile = audioRecorder.stopRecording()
        isRecordingAudio = false

        binding.btnAudio.apply {
            setImageResource(android.R.drawable.ic_btn_speak_now)
            setBackgroundColor(resources.getColor(android.R.color.holo_blue_bright, null))
        }
        binding.tvRecordingTime.text = ""

        if (audioFile != null) {
            sendAudioMessage(audioFile)
        }
    }

    private fun sendAudioMessage(audioFile: File) {
        binding.tvStatus.text = "⏳ Compression..."

        GlobalScope.launch {
            val audioDir = File(getExternalFilesDir(null), "audio")
            val compressedFile = File(audioDir, "compressed_${System.currentTimeMillis()}.m4a")

            val isCompressed = AudioCompressor.compressAudio(audioFile, compressedFile)

            val finalAudioFile = if (isCompressed) {
                val ratio = AudioCompressor.getCompressionRatio(audioFile.length(), compressedFile.length())
                launch(Dispatchers.Main) {
                    Toast.makeText(
                        this@ChatActivity,
                        "Compression: $ratio réduit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                compressedFile
            } else {
                audioFile
            }

            val success = smsManager.sendAudioMessage(phoneNumber, finalAudioFile)

            launch(Dispatchers.Main) {
                if (success) {
                    db.addMessage(
                        Message(
                            conversationId = conversationId,
                            phoneNumber = phoneNumber,
                            content = AudioConverter.audioToSMS(finalAudioFile),
                            isOutgoing = true,
                            isAudio = true,
                            audioPath = finalAudioFile.absolutePath,
                            timestamp = System.currentTimeMillis()
                        )
                    )
                    db.updateConversation(phoneNumber, "🎤 Audio envoyé")
                    loadMessages()

                    val smsCount = AudioConverter.estimateSMSCount(finalAudioFile)
                    binding.tvStatus.text = "✅ ${smsCount} SMS envoyés"
                    Toast.makeText(
                        this@ChatActivity,
                        "Audio envoyé ($smsCount SMS)",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    binding.tvStatus.text = "❌ Erreur d'envoi"
                    Toast.makeText(this@ChatActivity, "Erreur d'envoi", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun playAudio(message: Message) {
        if (message.audioPath != null) {
            val audioFile = File(message.audioPath!!)
            if (audioFile.exists()) {
                audioRecorder.playAudio(audioFile)
            } else {
                Toast.makeText(this, "Fichier audio non trouvé", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        audioRecorder.stopPlayback()
        if (isRecordingAudio) {
            audioRecorder.stopRecording()
        }
    }
}
