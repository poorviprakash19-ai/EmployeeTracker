package com.company.employeetracker.ui.screens.employee

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.company.employeetracker.data.database.entities.Message
import com.company.employeetracker.data.database.entities.User
import com.company.employeetracker.ui.theme.*
import com.company.employeetracker.viewmodel.EmployeeViewModel
import com.company.employeetracker.viewmodel.MessageViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(
    currentUser: User,
    onBackClick: () -> Unit = {},
    onMessageClick: (Int) -> Unit = {},
    messageViewModel: MessageViewModel = viewModel(),
    employeeViewModel: EmployeeViewModel = viewModel()
) {
    LaunchedEffect(currentUser.id) {
        messageViewModel.loadUnreadMessages(currentUser.id)
        messageViewModel.loadMessagesForUser(currentUser.id)
    }

    val unreadMessages by messageViewModel.unreadMessages.collectAsState()
    val allMessages by messageViewModel.userMessages.collectAsState()
    val employees by employeeViewModel.employees.collectAsState()

    var showUnreadOnly by remember { mutableStateOf(false) }

    val displayMessages = if (showUnreadOnly) unreadMessages else allMessages

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(GreenPrimary, GreenDark)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (unreadMessages.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = AccentRed
                                ) {
                                    Text(
                                        text = "${unreadMessages.size}",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Notifications",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${displayMessages.size} notification${if (displayMessages.size != 1) "s" else ""}",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // Filter Toggle
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showUnreadOnly) "Unread Only" else "All Notifications",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                FilterChip(
                    selected = showUnreadOnly,
                    onClick = { showUnreadOnly = !showUnreadOnly },
                    label = {
                        Text(if (showUnreadOnly) "Show All" else "Unread Only")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (showUnreadOnly) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }

        // Empty State
        if (displayMessages.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = Color(0xFFE0E0E0),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (showUnreadOnly) "No unread notifications" else "No notifications yet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF757575)
                        )
                        Text(
                            text = if (showUnreadOnly) "You're all caught up!" else "Notifications will appear here",
                            fontSize = 12.sp,
                            color = Color(0xFF9E9E9E)
                        )
                    }
                }
            }
        } else {
            // Notification List
            items(displayMessages) { message ->
                val sender = employees.find { it.id == message.senderId }

                Spacer(modifier = Modifier.height(12.dp))
                NotificationCard(
                    message = message,
                    sender = sender,
                    isUnread = !message.isRead,
                    onClick = {
                        messageViewModel.markAsRead(message.id)
                        onMessageClick(message.senderId)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun NotificationCard(
    message: Message,
    sender: User?,
    isUnread: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
    val timeString = dateFormat.format(Date(message.timestamp))

    val icon = when (message.messageType) {
        "REVIEW_REPLY" -> Icons.Default.Star
        "BROADCAST" -> Icons.Default.Campaign
        else -> Icons.AutoMirrored.Filled.Message
    }

    val backgroundColor = if (isUnread) GreenLight.copy(alpha = 0.08f) else Color.White

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(if (isUnread) 4.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Unread Indicator
            if (isUnread) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AccentRed)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Avatar/Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        when (message.messageType) {
                            "REVIEW_REPLY" -> AccentYellow.copy(alpha = 0.15f)
                            "BROADCAST" -> AccentBlue.copy(alpha = 0.15f)
                            else -> GreenPrimary.copy(alpha = 0.15f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (sender != null) {
                    Text(
                        text = sender.name.split(" ").mapNotNull { it.firstOrNull() }
                            .take(2).joinToString(""),
                        color = when (message.messageType) {
                            "REVIEW_REPLY" -> AccentYellow
                            "BROADCAST" -> AccentBlue
                            else -> GreenPrimary
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = when (message.messageType) {
                            "REVIEW_REPLY" -> AccentYellow
                            "BROADCAST" -> AccentBlue
                            else -> GreenPrimary
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Message Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = sender?.name ?: "System",
                            fontSize = 15.sp,
                            fontWeight = if (isUnread) FontWeight.Bold else FontWeight.SemiBold,
                            color = Color(0xFF212121)
                        )
                        if (sender != null) {
                            Text(
                                text = sender.designation,
                                fontSize = 12.sp,
                                color = Color(0xFF757575)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (message.messageType) {
                            "REVIEW_REPLY" -> AccentYellow.copy(alpha = 0.15f)
                            "BROADCAST" -> AccentBlue.copy(alpha = 0.15f)
                            else -> GreenPrimary.copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = when (message.messageType) {
                                "REVIEW_REPLY" -> "Review"
                                "BROADCAST" -> "Admin"
                                else -> "Message"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = when (message.messageType) {
                                "REVIEW_REPLY" -> AccentYellow
                                "BROADCAST" -> AccentBlue
                                else -> GreenPrimary
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message.message,
                    fontSize = 14.sp,
                    color = Color(0xFF424242),
                    lineHeight = 20.sp,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = timeString,
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }
            }
        }
    }
}