package com.company.employeetracker.ui.screens.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.company.employeetracker.data.database.entities.User
import com.company.employeetracker.ui.theme.*
import com.company.employeetracker.viewmodel.AuthViewModel
import com.company.employeetracker.viewmodel.ReviewViewModel
import com.company.employeetracker.viewmodel.TaskViewModel

@Composable
fun EmployeeProfileScreen(
    currentUser: User,
    onLogout: () -> Unit = {},
    taskViewModel: TaskViewModel = viewModel(),
    reviewViewModel: ReviewViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    LaunchedEffect(currentUser.id) {
        taskViewModel.loadTasksForEmployee(currentUser.id)
        reviewViewModel.loadReviewsForEmployee(currentUser.id)
    }

    val tasks by taskViewModel.employeeTasks.collectAsState()
    val reviews by reviewViewModel.employeeReviews.collectAsState()
    val averageRating by reviewViewModel.averageRating.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E27))
    ) {
        // Modern Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(Color(0xFF2ECC71), Color(0xFF27AE60))))
                    .padding(32.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier.size(120.dp).clip(CircleShape).background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser.name.split(" ").mapNotNull { it.firstOrNull() }.take(2).joinToString(""),
                            color = Color(0xFF2ECC71), fontSize = 42.sp, fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(currentUser.name, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(6.dp))
                    Text(currentUser.designation, fontSize = 15.sp, color = Color.White.copy(0.8f), fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    Text(currentUser.email, fontSize = 13.sp, color = Color.White.copy(0.7f))
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF2ECC71)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Edit Profile", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }

        // Stats
        item {
            Spacer(Modifier.height(24.dp))
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileStat(Icons.Default.Assignment, tasks.size.toString(), "Tasks", listOf(Color(0xFF667EEA), Color(0xFF764BA2)), Modifier.weight(1f))
                ProfileStat(Icons.Default.Star, reviews.size.toString(), "Reviews", listOf(Color(0xFFFFA500), Color(0xFFFF6347)), Modifier.weight(1f))
                ProfileStat(Icons.Default.TrendingUp, String.format("%.1f", averageRating), "Rating", listOf(Color(0xFF2ECC71), Color(0xFF27AE60)), Modifier.weight(1f))
            }
        }

        item {
            Spacer(Modifier.height(32.dp))
            Text("⚙️ Settings", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 20.dp))
        }

        item {
            Spacer(Modifier.height(12.dp))
            Card(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(Color(0xFF1A1F3A)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(Modifier.padding(4.dp)) {
                    ModernSetting(Icons.Default.Notifications, "Push Notifications", "Get notified", Color(0xFFFF6B9D), true)
                    Divider(color = Color.White.copy(0.1f))
                    ModernSetting(Icons.Default.CloudUpload, "Auto Backup", "Backup data", Color(0xFF56CCF2), true)
                }
            }
        }

        item {
            Spacer(Modifier.height(28.dp))
            Text("🎨 Appearance", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 20.dp))
        }

        item {
            Spacer(Modifier.height(12.dp))
            Card(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(Color(0xFF1A1F3A)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(Modifier.padding(4.dp)) {
                    ModernSetting(Icons.Default.DarkMode, "Dark Mode", "Currently enabled", Color(0xFF764BA2), true)
                    Divider(color = Color.White.copy(0.1f))
                    ModernSetting(Icons.Default.GridView, "Compact View", "More items", Color(0xFFFF9800), true)
                }
            }
        }

        item {
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = { authViewModel.logout(); onLogout() },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color(0xFFFF6B9D), Color(0xFFC06C84)))),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Logout, null, tint = Color.White)
                        Spacer(Modifier.width(12.dp))
                        Text("Logout", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        item { Spacer(Modifier.height(100.dp)) }
    }
}

@Composable
fun ProfileStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    gradient: List<Color>,
    modifier: Modifier
) {
    Card(modifier = modifier.height(110.dp), colors = CardDefaults.cardColors(Color.Transparent), shape = RoundedCornerShape(16.dp)) {
        Box(Modifier.fillMaxSize().background(Brush.linearGradient(gradient)).padding(14.dp)) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(icon, null, tint = Color.White.copy(0.8f), modifier = Modifier.size(32.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(value, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text(label, fontSize = 11.sp, color = Color.White.copy(0.9f), fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun ModernSetting(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    hasSwitch: Boolean
) {
    var checked by remember { mutableStateOf(false) }

    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = RoundedCornerShape(12.dp), color = iconColor.copy(0.2f), modifier = Modifier.size(48.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, fontSize = 12.sp, color = Color.White.copy(0.6f))
        }
        if (hasSwitch) {
            Switch(
                checked = checked,
                onCheckedChange = { checked = it },
                colors = SwitchDefaults.colors(checkedThumbColor = iconColor, checkedTrackColor = iconColor.copy(0.3f))
            )
        }
    }
}