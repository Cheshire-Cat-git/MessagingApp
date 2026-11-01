package com.example.messagingapp.presentation.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.messagingapp.databinding.ItemNotificationMessageBinding
import com.example.messagingapp.domain.model.NotificationMessage

class NotificationAdapter(
    private var notifications: List<NotificationMessage> = emptyList()
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemNotificationMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = notifications[position]
        holder.binding.tvSender.text = item.sender
        holder.binding.tvMessage.text = item.message

        when (item) {
            is NotificationMessage.Urgent -> {
                holder.binding.tvNotificationType.text = "Urgent Notification"
                holder.binding.layoutExpirationDate.visibility = View.VISIBLE
                holder.binding.tvExpirationDate.text = item.expirationDate
            }
            is NotificationMessage.Regular -> {
                holder.binding.tvNotificationType.text = "Regular Notification"
                holder.binding.layoutExpirationDate.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = notifications.size

    fun updateNotifications(newNotifications: List<NotificationMessage>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }
}
