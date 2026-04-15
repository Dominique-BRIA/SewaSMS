package com.sewasms.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.sewasms.models.Conversation
import com.sewasms.models.Message

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "sewasms.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE conversations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                phone_number TEXT UNIQUE NOT NULL,
                contact_name TEXT,
                last_message TEXT,
                timestamp LONG,
                unread_count INTEGER DEFAULT 0
            )
        """)

        db.execSQL("""
            CREATE TABLE messages (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                conversation_id INTEGER NOT NULL,
                phone_number TEXT NOT NULL,
                content TEXT NOT NULL,
                is_outgoing INTEGER DEFAULT 0,
                is_audio INTEGER DEFAULT 0,
                audio_path TEXT,
                timestamp LONG,
                is_read INTEGER DEFAULT 1,
                FOREIGN KEY(conversation_id) REFERENCES conversations(id)
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun getConversations(): List<Conversation> {
        val conversations = mutableListOf<Conversation>()
        val cursor = readableDatabase.query(
            "conversations",
            null,
            null,
            null,
            null,
            null,
            "timestamp DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                conversations.add(
                    Conversation(
                        id = it.getLong(it.getColumnIndexOrThrow("id")),
                        phoneNumber = it.getString(it.getColumnIndexOrThrow("phone_number")),
                        contactName = it.getString(it.getColumnIndexOrThrow("contact_name")) ?: "",
                        lastMessage = it.getString(it.getColumnIndexOrThrow("last_message")) ?: "",
                        timestamp = it.getLong(it.getColumnIndexOrThrow("timestamp")),
                        unreadCount = it.getInt(it.getColumnIndexOrThrow("unread_count"))
                    )
                )
            }
        }
        return conversations
    }

    fun getConversationId(phoneNumber: String): Long {
        val cursor = readableDatabase.query(
            "conversations",
            arrayOf("id"),
            "phone_number = ?",
            arrayOf(phoneNumber),
            null,
            null,
            null
        )

        cursor.use {
            return if (it.moveToFirst()) {
                it.getLong(it.getColumnIndexOrThrow("id"))
            } else {
                createConversation(phoneNumber)
            }
        }
    }

    private fun createConversation(phoneNumber: String): Long {
        val values = ContentValues().apply {
            put("phone_number", phoneNumber)
            put("contact_name", phoneNumber)
            put("timestamp", System.currentTimeMillis())
        }
        return writableDatabase.insert("conversations", null, values)
    }

    fun updateConversation(phoneNumber: String, lastMessage: String) {
        val values = ContentValues().apply {
            put("last_message", lastMessage)
            put("timestamp", System.currentTimeMillis())
        }
        writableDatabase.update(
            "conversations",
            values,
            "phone_number = ?",
            arrayOf(phoneNumber)
        )
    }

    fun getMessages(conversationId: Long): List<Message> {
        val messages = mutableListOf<Message>()
        val cursor = readableDatabase.query(
            "messages",
            null,
            "conversation_id = ?",
            arrayOf(conversationId.toString()),
            null,
            null,
            "timestamp ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                messages.add(
                    Message(
                        id = it.getLong(it.getColumnIndexOrThrow("id")),
                        conversationId = it.getLong(it.getColumnIndexOrThrow("conversation_id")),
                        phoneNumber = it.getString(it.getColumnIndexOrThrow("phone_number")),
                        content = it.getString(it.getColumnIndexOrThrow("content")),
                        isOutgoing = it.getInt(it.getColumnIndexOrThrow("is_outgoing")) == 1,
                        isAudio = it.getInt(it.getColumnIndexOrThrow("is_audio")) == 1,
                        audioPath = it.getString(it.getColumnIndexOrThrow("audio_path")),
                        timestamp = it.getLong(it.getColumnIndexOrThrow("timestamp")),
                        isRead = it.getInt(it.getColumnIndexOrThrow("is_read")) == 1
                    )
                )
            }
        }
        return messages
    }

    fun addMessage(message: Message): Long {
        val values = ContentValues().apply {
            put("conversation_id", message.conversationId)
            put("phone_number", message.phoneNumber)
            put("content", message.content)
            put("is_outgoing", message.isOutgoing)
            put("is_audio", message.isAudio)
            put("audio_path", message.audioPath)
            put("timestamp", message.timestamp)
            put("is_read", message.isRead)
        }
        return writableDatabase.insert("messages", null, values)
    }
}
