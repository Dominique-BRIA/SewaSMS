package com.sewasms.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sewasms.databinding.ItemMessageBinding
import com.sewasms.models.Message

class MessageAdapter(
    private val myPhoneNumber: String,
    private val onAudioClick: (Message) -> Unit
) : ListAdapter<Message, MessageAdapter.ViewHolder>(MessageDiffCallback()) {

    inner class ViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.apply {
                val isOutgoing = message.isOutgoing
                val params = tvMessage.layoutParams as ViewGroup.MarginLayoutParams

                if (isOutgoing) {
                    params.marginStart = 60
                    params.marginEnd = 0
                } else {
                    params.marginStart = 0
                    params.marginEnd = 60
                }
                tvMessage.layoutParams = params

                if (message.isAudio) {
                    tvMessage.text = "🎤 Audio"
                    tvMessage.setOnClickListener { onAudioClick(message) }
                } else {
                    tvMessage.text = message.content
                }

                tvTime.text = message.getFormattedTime()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Message, newItem: Message) = oldItem == newItem
    }
}
