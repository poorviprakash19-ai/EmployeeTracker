package com.company.employeetracker.ui.screens.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.company.employeetracker.data.database.entities.User
import com.company.employeetracker.ui.components.TaskCard
import com.company.employeetracker.ui.theme.*
import com.company.employeetracker.viewmodel.ReviewViewModel
import com.company.employeetracker.viewmodel.TaskViewModel
import com.company.employeetracker.ui.components.LoadingScreen
import com.company.employeetracker.ui.components.ErrorStateScreen
import com.company.employeetracker.viewmodel.MessageViewModel

@Composable
fun EmployeeHomeScreen(
    currentUser: User,
    onTaskClick: (Int) -> Unit = {},
    onNavigateToSelectEmployee: () -> Unit = {},
    taskViewModel: TaskViewModel = viewModel(),
    reviewViewModel: ReviewViewModel = viewModel(),
    messageViewModel: MessageViewModel = viewModel()
) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser.id) {
        try {
            isLoading = true
            hasError = false
            taskViewModel.loadTasksForEmployee(currentUser.id)
            reviewViewModel.loadReviewsForEmployee(currentUser.id)
            messageViewModel.loadUnreadCount(currentUser.id)
            kotlinx.coroutines.delay(1000)
            isLoading = false
        } catch (e: Exception) {
            hasError = true
            isLoading = false
        }
    }

    val tasks by taskViewModel.employeeTasks.collectAsState()
    val reviews by reviewViewModel.employeeReviews.collectAsState()
    val activeCount by taskViewModel.activeCount.collectAsState()
    val pendingCount by taskViewModel.pendingCount.collectAsState()
    val completedCount by taskViewModel.completedCount.collectAsState()
    val unreadCount by messageViewModel.unreadCount.collectAsState()

    val totalTasks = activeCount + pendingCount + completedCount
    val completionPercentage = if (totalTasks > 0) (completedCount * 100) / totalTasks else 0
    val latestReview = reviews.firstOrNull()

    if (isLoading) {
        LoadingScreen(message = "Loading your dashboard...")
        return
    }

    if (hasError) {
        ErrorStateScreen(
            title = "Failed to Load Data",
            message = "We couldn't load your dashboard.",
            onRetry = { isLoading = true; hasError = false }
        )
        return
    }

    Scaffold(
        containerColor = Color(0xFF0A0E27),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToSelectEmployee,
                containerColor = Color.Transparent,
                modifier = Modifier.size(64.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.linearGradient(listOf(Color(0xFF2ECC71), Color(0xFF27AE60)))
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Message, null, modifier = Modifier.size(28.dp), tint = Color.White)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color(0xFF0A0E27)).padding(paddingValues)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().background(
                        Brush.horizontalGradient(listOf(Color(0xFF2ECC71), Color(0xFF27AE60)))
                    ).padding(24.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentUser.name.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString(""),
                                    color = Color(0xFF2ECC71), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text("👋 Welcome back!", color = Color.White.copy(0.9f), fontSize = 13.sp, fontWeight = FontWeight.Light)
                                Text(currentUser.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                                Text(currentUser.designation, color = Color.White.copy(0.8f), fontSize = 12.sp)
                            }
                        }

                        Box {
                            Surface(shape = CircleShape, color = Color.White.copy(0.2f), modifier = Modifier.size(48.dp)) {
                                IconButton(onClick = {}) {
                                    Icon(Icons.Default.Notifications, null, tint = Color.White)
                                }
                            }
                            if (unreadCount > 0) {
                                Box(
                                    modifier = Modifier.size(20.dp).offset(32.dp, 0.dp).clip(CircleShape).background(Color(0xFFFF4757)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(if (unreadCount > 9) "9+" else "$unreadCount", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GradientStatMini(Icons.Default.TrendingUp, "$activeCount", "Active", listOf(Color(0xFF2ECC71), Color(0xFF27AE60)), Modifier.weight(1f))
                    GradientStatMini(Icons.Default.Pause, "$pendingCount", "Pending", listOf(Color(0xFFFF9800), Color(0xFFF57C00)), Modifier.weight(1f))
                    GradientStatMini(Icons.Default.CheckCircle, "$completedCount", "Done", listOf(Color(0xFF667EEA), Color(0xFF764BA2)), Modifier.weight(1f))
                }
            }

            item {
                Spacer(Modifier.height(20.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(Color(0xFF1A1F3A)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("📊 Task Progress", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Keep it up!", fontSize = 13.sp, color = Color.White.copy(0.6f))
                            }
                            Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFF2ECC71).copy(0.2f)) {
                                Text("$completedCount of $totalTasks", color = Color(0xFF2ECC71), fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { if (totalTasks > 0) completedCount.toFloat() / totalTasks else 0f },
                            modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                            color = Color(0xFF2ECC71),
                            trackColor = Color.White.copy(0.1f)
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("$completionPercentage% Complete", fontSize = 14.sp, color = Color(0xFF2ECC71), fontWeight = FontWeight.SemiBold)
                            Text("${totalTasks - completedCount} remaining", fontSize = 12.sp, color = Color.White.copy(0.5f))
                        }
                    }
                }
            }

            if (latestReview != null) {
                item {
                    Spacer(Modifier.height(20.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        colors = CardDefaults.cardColors(Color(0xFF1A1F3A)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = Color(0xFFFFA500).copy(0.2f), modifier = Modifier.size(56.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Star, null, tint = Color(0xFFFFA500), modifier = Modifier.size(28.dp))
                                }
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Latest Review", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text(latestReview.remarks, fontSize = 13.sp, color = Color.White.copy(0.7f), maxLines = 2)
                            }
                            Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFFFA500).copy(0.2f)) {
                                Text("${latestReview.overallRating}/5", color = Color(0xFFFFA500), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(28.dp))
                Text("📝 Upcoming Tasks", modifier = Modifier.padding(horizontal = 20.dp), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            if (tasks.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.TaskAlt, null, tint = Color.White.copy(0.3f), modifier = Modifier.size(64.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("No tasks yet", fontSize = 16.sp, color = Color.White.copy(0.6f))
                        }
                    }
                }
            } else {
                items(tasks.take(3)) { task ->
                    Spacer(Modifier.height(12.dp))
                    ModernTaskCardHome(task, onClick = { onTaskClick(task.id) }, modifier = Modifier.padding(horizontal = 20.dp))
                }
            }

            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun GradientStatMini(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    gradient: List<Color>,
    modifier: Modifier
) {
    Card(modifier = modifier.height(100.dp), colors = CardDefaults.cardColors(Color.Transparent), shape = RoundedCornerShape(16.dp)) {
        Box(Modifier.fillMaxSize().background(Brush.linearGradient(gradient)).padding(14.dp)) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(icon, null, tint = Color.White.copy(0.8f), modifier = Modifier.size(26.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(value, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text(label, fontSize = 11.sp, color = Color.White.copy(0.9f), fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun ModernTaskCardHome(
    task: com.company.employeetracker.data.database.entities.Task,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val statusColor = when (task.status) {
        "Done" -> Color(0xFF2ECC71)
        "Active" -> Color(0xFFFF9800)
        else -> Color(0xFFFF6B9D)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(Color(0xFF1A1F3A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(task.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    Spacer(Modifier.height(4.dp))
                    Text(task.description, fontSize = 13.sp, color = Color.White.copy(0.6f), maxLines = 2)
                }
                Surface(shape = RoundedCornerShape(8.dp), color = statusColor.copy(0.2f)) {
                    Text(task.status, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, null, tint = Color.White.copy(0.5f), modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text(task.deadline, fontSize = 12.sp, color = Color.White.copy(0.6f))
            }
        }
    }
}