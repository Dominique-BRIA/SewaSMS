package com.sewasms

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sewasms.adapters.ConversationAdapter
import com.sewasms.databinding.ActivityMainBinding
import com.sewasms.utils.DatabaseHelper
import com.sewasms.utils.ThemeManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: DatabaseHelper
    private lateinit var adapter: ConversationAdapter

    private val PERMISSION_CODE = 100
    private val requiredPermissions = arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.SEND_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.POST_NOTIFICATIONS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ThemeManager.initializeTheme(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)
        setupUI()
        requestPermissions()
        loadConversations()
    }

    private fun setupUI() {
        adapter = ConversationAdapter { conversation ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("phone_number", conversation.phoneNumber)
            intent.putExtra("contact_name", conversation.contactName)
            startActivity(intent)
        }

        binding.rvConversations.apply {
            adapter = this@MainActivity.adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        binding.fabNewMessage.setOnClickListener {
            showNewMessageDialog()
        }

        binding.btnMenu?.setOnClickListener {
            ThemeManager.toggleTheme(this)
            recreate()
        }
    }

    private fun loadConversations() {
        GlobalScope.launch(Dispatchers.Main) {
            val conversations = db.getConversations()
            adapter.submitList(conversations)

            binding.tvEmpty.text = if (conversations.isEmpty()) "Aucune conversation" else ""
        }
    }

    private fun showNewMessageDialog() {
        val dialog = NewConversationDialog(this) { phoneNumber ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("phone_number", phoneNumber)
            intent.putExtra("contact_name", phoneNumber)
            startActivity(intent)
        }
        dialog.show()
    }

    private fun requestPermissions() {
        val missingPermissions = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                missingPermissions.toTypedArray(),
                PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                if (!allGranted) {
                    Toast.makeText(this, "Permissions requises", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadConversations()
    }
}
