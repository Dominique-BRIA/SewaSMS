package com.sewasms

import android.content.Context
import android.app.AlertDialog
import android.widget.EditText

class NewConversationDialog(
    context: Context,
    private val onPhoneNumberEntered: (String) -> Unit
) {

    private val dialog = AlertDialog.Builder(context)
        .setTitle("Nouveau Message")
        .setMessage("Entrez le numéro de téléphone")
        .also { builder ->
            val input = EditText(context).apply {
                hint = "+212..."
                inputType = android.text.InputType.TYPE_CLASS_PHONE
            }
            builder.setView(input)

            builder.setPositiveButton("OK") { _, _ ->
                val phoneNumber = input.text.toString().trim()
                if (phoneNumber.isNotEmpty()) {
                    onPhoneNumberEntered(phoneNumber)
                }
            }

            builder.setNegativeButton("Annuler", null)
        }
        .create()

    fun show() = dialog.show()
}
