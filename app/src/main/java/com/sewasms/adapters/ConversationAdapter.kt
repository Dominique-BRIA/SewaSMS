package com.sewasms.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sewasms.databinding.ItemConversationBinding
import com.sewasms.models.Conversation

class ConversationAdapter(
    private val onItemClick: (Conversation) -> Unit
) : ListAdapter<Conversation, ConversationAdapter.ViewHolder>(ConversationDiffCallback()) {

    inner class ViewHolder(private val binding: ItemConversationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(conversation: Conversation) {
            binding.apply {
                tvContactName.text = conversation.contactName.ifEmpty { conversation.phoneNumber }
                tvPhoneNumber.text = conversation.phoneNumber
                tvLastMessage.text = conversation.lastMessage
                tvTime.text = conversation.getFormattedTime()

                if (conversation.unreadCount > 0) {
                    tvUnread.text = conversation.unreadCount.toString()
                    tvUnread.visibility = android.view.View.VISIBLE
                } else {
                    tvUnread.visibility = android.view.View.GONE
                }

                root.setOnClickListener { onItemClick(conversation) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ConversationDiffCallback : DiffUtil.ItemCallback<Conversation>() {
        override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation) = oldItem == newItem
    }
}
