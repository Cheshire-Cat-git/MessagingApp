package com.example.messagingapp.presentation.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.messagingapp.R
import com.example.messagingapp.databinding.ItemDataMessageBinding
import com.example.messagingapp.domain.model.DataMessage

class DataMessageAdapter(
    private var messages: List<DataMessage> = emptyList()
) : RecyclerView.Adapter<DataMessageAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemDataMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDataMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = messages[position]
        holder.binding.tvTitle.text = item.title
        holder.binding.tvMessage.text = item.message

        val context = holder.itemView.context
        val iconResId = getIconResourceId(item.icon, context)
        holder.binding.ivIcon.setImageResource(iconResId)
    }

    override fun getItemCount(): Int = messages.size

    fun updateMessages(newMessages: List<DataMessage>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    private fun getIconResourceId(iconName: String, context: Context): Int {
        if (iconName.isBlank()) return R.drawable.ic_message
        val resId = context.resources.getIdentifier(iconName, "drawable", context.packageName)
        return if (resId != 0) resId else R.drawable.ic_message
    }
}