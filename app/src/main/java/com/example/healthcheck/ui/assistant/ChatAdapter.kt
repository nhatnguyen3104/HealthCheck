package com.example.healthcheck.ui.assistant

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcheck.R
import com.example.healthcheck.databinding.ItemMessageBinding
import com.example.healthcheck.model.ChatMessage

class ChatAdapter(private val messages: MutableList<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMessageBinding.inflate(inflater, parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.binding.apply {
            tvMessage.text = message.message
            if (message.isFromUser) {
                ivAvatar.setImageResource(R.drawable.ic_user)
                (tvMessage.layoutParams as LinearLayout.LayoutParams).gravity = Gravity.END
                (ivAvatar.layoutParams as LinearLayout.LayoutParams).gravity = Gravity.END

            } else {
                ivAvatar.setImageResource(R.drawable.ic_ai)
                container.gravity = Gravity.START
            }
        }
    }

    override fun getItemCount() = messages.size

    fun updateMessages(newMessages: List<ChatMessage>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }
}
