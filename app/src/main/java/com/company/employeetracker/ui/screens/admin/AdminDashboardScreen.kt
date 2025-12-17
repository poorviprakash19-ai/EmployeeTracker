package com.company.employeetracker.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.company.employeetracker.ui.components.EmployeeCard
import com.company.employeetracker.ui.components.AddReviewDialog
import com.company.employeetracker.ui.theme.*
import com.company.employeetracker.viewmodel.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminDashboardScreen(
    employeeViewModel: EmployeeViewModel = viewModel(),
    taskViewModel: TaskViewModel = viewModel(),
    reviewViewModel: ReviewViewModel = viewModel(),
    messageViewModel: MessageViewModel = viewModel()
) {
    val employees by employeeViewModel.employees.collectAsState()
    val employeeCount by employeeViewModel.employeeCount.collectAsState()
    val allTasks by taskViewModel.allTasks.collectAsState()
    val allReviews by reviewViewModel.allReviews.collectAsState()
    val unreadCount by messageViewModel.unreadCount.collectAsState()

    LaunchedEffect(Unit) {
        messageViewModel.loadUnreadCount(1)
    }

    val completedTasks = allTasks.count { it.status == "Done" }
    val pendingTasks = allTasks.size - completedTasks
    val avgRating =
        if (allReviews.isNotEmpty()) allReviews.map { it.overallRating }.average().toFloat() else 0f

    val topPerformers = allReviews
        .groupBy { it.employeeId }
        .mapValues { it.value.map { r -> r.overallRating }.average().toFloat() }
        .toList()
        .sortedByDescending { it.second }
        .take(3)

    val recentActivities = remember(employees, allTasks, allReviews) {
        buildList {
            employees.sortedByDescending { it.joiningDate }.take(2).forEach {
                add(
                    ActivityItem(
                        Icons.Default.PersonAdd,
                        "New Employee",
                        "${it.name} joined ${it.department}",
                        getRelativeTime(it.joiningDate),
                        Color(0xFF2ECC71),
                        parseDate(it.joiningDate)
                    )
                )
            }

            allTasks.filter { it.status == "Done" }
                .sortedByDescending { it.deadline }
                .take(2)
                .forEach {
                    val emp = employees.find { e -> e.id == it.employeeId }
                    add(
                        ActivityItem(
                            Icons.Default.CheckCircle,
                            "Task Completed",
                            "${it.title} by ${emp?.name ?: "Unknown"}",
                            getRelativeTime(it.deadline),
                            Color(0xFF667EEA),
                            parseDate(it.deadline)
                        )
                    )
                }

            allReviews.sortedByDescending { it.date }.take(2).forEach {
                val emp = employees.find { e -> e.id == it.employeeId }
                add(
                    ActivityItem(
                        Icons.Default.Star,
                        "Performance Review",
                        "${emp?.name ?: "Unknown"} rated ${it.overallRating}",
                        getRelativeTime(it.date),
                        Color(0xFFFFA500),
                        parseDate(it.date)
                    )
                )
            }
        }.sortedByDescending { it.timestamp }
    }

    var showAllActivities by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E27))
    ) {

        /** MODERN HEADER WITH GRADIENT **/
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "👋 Welcome Back",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Light
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Dashboard",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Box {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            IconButton(onClick = { }) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .offset(x = 32.dp, y = (-2).dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF4757)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$unreadCount",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        /** GRADIENT STATS CARDS **/
        item {
            Spacer(Modifier.height(20.dp))
            Column(Modifier.padding(horizontal = 20.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GradientStatCard(
                        Icons.Default.People,
                        employeeCount.toString(),
                        "Team Members",
                        listOf(Color(0xFF56CCF2), Color(0xFF2F80ED)),
                        Modifier.weight(1f)
                    )
                    GradientStatCard(
                        Icons.Default.CheckCircle,
                        completedTasks.toString(),
                        "Completed",
                        listOf(Color(0xFF2ECC71), Color(0xFF27AE60)),
                        Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GradientStatCard(
                        Icons.Default.Schedule,
                        pendingTasks.toString(),
                        "Pending",
                        listOf(Color(0xFFFF6B9D), Color(0xFFC06C84)),
                        Modifier.weight(1f)
                    )
                    GradientStatCard(
                        Icons.Default.Star,
                        String.format("%.1f", avgRating),
                        "Avg Rating",
                        listOf(Color(0xFFFFA500), Color(0xFFFF6347)),
                        Modifier.weight(1f)
                    )
                }
            }
        }

        /** TOP PERFORMERS WITH NEW STYLE **/
        item {
            Spacer(Modifier.height(28.dp))
            Text(
                "⭐ Top Performers",
                modifier = Modifier.padding(horizontal = 20.dp),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        items(topPerformers) { (id, rating) ->
            val emp = employees.find { it.id == id }
            emp?.let {
                Spacer(Modifier.height(12.dp))
                ModernEmployeeCard(
                    employee = it,
                    rating = rating,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        /** ACTIVITY TIMELINE **/
        item {
            Spacer(Modifier.height(28.dp))
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "📋 Activity Feed",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (recentActivities.size > 3) {
                    TextButton(onClick = { showAllActivities = !showAllActivities }) {
                        Text(
                            if (showAllActivities) "Less" else "All",
                            color = Color(0xFF667EEA)
                        )
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F3A)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val list =
                        if (showAllActivities) recentActivities else recentActivities.take(3)

                    list.forEachIndexed { index, activity ->
                        ModernActivityItem(activity = activity)
                        if (index < list.lastIndex) {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }

        /** FLOATING ACTION BUTTON STYLE **/
        item {
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { showReviewDialog = true },
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFFA500), Color(0xFFFF6347))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = Color.White)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Add Performance Review",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(100.dp)) }
    }

    if (showReviewDialog) {
        AddReviewDialog(
            onDismiss = { showReviewDialog = false },
            onReviewAdded = {}
        )
    }
}

/** ---------------- MODERN COMPONENTS ---------------- **/

@Composable
fun GradientStatCard(
    icon: ImageVector,
    value: String,
    label: String,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradient))
                .padding(18.dp)
        ) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    icon,
                    null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        value,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        label,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ModernEmployeeCard(
    employee: com.company.employeetracker.data.database.entities.User,
    rating: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F3A))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = employee.name.split(" ").mapNotNull { it.firstOrNull() }
                        .take(2).joinToString(""),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = employee.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF667EEA).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = employee.department,
                        fontSize = 11.sp,
                        color = Color(0xFF667EEA),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFFA500).copy(alpha = 0.2f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        null,
                        tint = Color(0xFFFFA500),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        String.format("%.1f", rating),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFA500)
                    )
                }
            }
        }
    }
}

@Composable
fun ModernActivityItem(activity: ActivityItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = activity.iconColor.copy(alpha = 0.2f),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    activity.icon,
                    null,
                    tint = activity.iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(Modifier.width(14.dp))

        Column(Modifier.weight(1f)) {
            Text(
                activity.title,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                activity.subtitle,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        Text(
            activity.time,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.5f),
            fontWeight = FontWeight.Medium
        )
    }
}

/** ---------------- MODELS & UTILS ---------------- **/

data class ActivityItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val time: String,
    val iconColor: Color,
    val timestamp: Long
)

private fun getRelativeTime(date: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val d = sdf.parse(date) ?: return "Unknown"
        val days = (Date().time - d.time) / (1000 * 60 * 60 * 24)
        when {
            days < 1 -> "Today"
            days == 1L -> "Yesterday"
            days < 7 -> "${days}d ago"
            days < 30 -> "${days / 7}w ago"
            else -> "${days / 30}mo ago"
        }
    } catch (e: Exception) {
        "Unknown"
    }
}

private fun parseDate(date: String): Long {
    return try {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)?.time ?: 0L
    } catch (e: Exception) {
        0L
    }
}