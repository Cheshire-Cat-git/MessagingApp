package com.example.messagingapp.presentation.main

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messagingapp.ServiceLocator
import com.example.messagingapp.databinding.ActivityMainBinding
import com.example.messagingapp.domain.model.DataMessage
import com.example.messagingapp.domain.model.NotificationMessage
import com.example.messagingapp.presentation.service.MyFirebaseMessagingService
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dataMessageAdapter: DataMessageAdapter
    private lateinit var notificationAdapter: NotificationAdapter

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(this)
    }

    private val messageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.loadDataMessages()
            viewModel.loadNotifications()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ServiceLocator.provideNotificationRepository(this).createNotificationChannels()
        requestNotificationPermission()

        setupRecyclerViews()
        viewModel.handleNotificationIntent(intent)
        viewModel.loadDataMessages()
        viewModel.loadNotifications()

        observeUiState()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        viewModel.handleNotificationIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter().apply {
            addAction(MyFirebaseMessagingService.ACTION_DATA_MESSAGE_RECEIVED)
            addAction(MyFirebaseMessagingService.ACTION_MESSAGE_RECEIVED)
        }
        ContextCompat.registerReceiver(
            this,
            messageReceiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        viewModel.loadDataMessages()
        viewModel.loadNotifications()
    }

    override fun onPause() {
        super.onPause()
        try {
            unregisterReceiver(messageReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private fun setupRecyclerViews() {
        dataMessageAdapter = DataMessageAdapter()
        binding.rvDataMessages.layoutManager = LinearLayoutManager(this)
        binding.rvDataMessages.adapter = dataMessageAdapter

        notificationAdapter = NotificationAdapter()
        binding.rvNotifications.layoutManager = LinearLayoutManager(this)
        binding.rvNotifications.adapter = notificationAdapter
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderDataMessages(state.dataMessages)
                    renderNotifications(state.notifications)
                    renderNotificationDetail(state.activeNotification)
                }
            }
        }
    }

    private fun renderDataMessages(dataMessages: List<DataMessage>) {
        if (dataMessages.isEmpty()) {
            binding.tvEmptyDataMessages.visibility = View.VISIBLE
            binding.rvDataMessages.visibility = View.GONE
        } else {
            binding.tvEmptyDataMessages.visibility = View.GONE
            binding.rvDataMessages.visibility = View.VISIBLE
            dataMessageAdapter.updateMessages(dataMessages)
        }
    }

    private fun renderNotifications(notifications: List<NotificationMessage>) {
        if (notifications.isEmpty()) {
            binding.tvEmptyNotifications.visibility = View.VISIBLE
            binding.rvNotifications.visibility = View.GONE
        } else {
            binding.tvEmptyNotifications.visibility = View.GONE
            binding.rvNotifications.visibility = View.VISIBLE
            notificationAdapter.updateNotifications(notifications)
        }
    }

    private fun renderNotificationDetail(notification: NotificationMessage?) {
        if (notification == null) {
            binding.cardNotificationDetail.visibility = View.GONE
            return
        }

        binding.cardNotificationDetail.visibility = View.VISIBLE
        binding.tvSender.text = notification.sender
        binding.tvMessage.text = notification.message

        when (notification) {
            is NotificationMessage.Urgent -> {
                binding.tvNotificationType.text = "Urgent Notification Details"
                binding.layoutExpirationDate.visibility = View.VISIBLE
                binding.tvExpirationDate.text = notification.expirationDate
            }
            is NotificationMessage.Regular -> {
                binding.tvNotificationType.text = "Regular Notification Details"
                binding.layoutExpirationDate.visibility = View.GONE
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
    }
}
