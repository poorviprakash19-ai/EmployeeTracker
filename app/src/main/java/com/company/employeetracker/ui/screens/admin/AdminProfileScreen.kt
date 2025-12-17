package com.company.employeetracker.ui.screens.admin

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
import com.company.employeetracker.viewmodel.EmployeeViewModel
import com.company.employeetracker.viewmodel.ReviewViewModel
import com.company.employeetracker.viewmodel.TaskViewModel

@Composable
fun AdminProfileScreen(
    currentUser: User,
    onLogout: () -> Unit = {},
    employeeViewModel: EmployeeViewModel = viewModel(),
    taskViewModel: TaskViewModel = viewModel(),
    reviewViewModel: ReviewViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val employeeCount by employeeViewModel.employeeCount.collectAsState()
    val allTasks by taskViewModel.allTasks.collectAsState()
    val reviewCount by reviewViewModel.reviewCount.collectAsState()

    val avgRating = 4.7f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E27))
    ) {
        // Modern Gradient Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                        )
                    )
                    .padding(32.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser.name.split(" ").mapNotNull { it.firstOrNull() }
                                .take(2).joinToString(""),
                            color = Color(0xFF667EEA),
                            fontSize = 42.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = currentUser.name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = currentUser.designation,
                        fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = currentUser.email,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF667EEA)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Edit Profile",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // Gradient Stats Cards
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileStatCard(
                    icon = Icons.Default.People,
                    value = employeeCount.toString(),
                    label = "Employees",
                    gradient = listOf(Color(0xFF56CCF2), Color(0xFF2F80ED)),
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    icon = Icons.Default.Assignment,
                    value = allTasks.size.toString(),
                    label = "Tasks",
                    gradient = listOf(Color(0xFFFF9800), Color(0xFFF57C00)),
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    icon = Icons.Default.Star,
                    value = String.format("%.1f", avgRating),
                    label = "Rating",
                    gradient = listOf(Color(0xFFFFA500), Color(0xFFFF6347)),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Settings Section
        item {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "⚙️ Preferences",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F3A)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    ModernSettingItem(
                        icon = Icons.Default.Notifications,
                        title = "Push Notifications",
                        subtitle = "Get notified about updates",
                        iconColor = Color(0xFFFF6B9D),
                        hasSwitch = true
                    )
                    Divider(color = Color.White.copy(alpha = 0.1f))
                    ModernSettingItem(
                        icon = Icons.Default.CloudUpload,
                        title = "Auto Backup",
                        subtitle = "Automatically backup data",
                        iconColor = Color(0xFF56CCF2),
                        hasSwitch = true
                    )
                }
            }
        }

        // Appearance
        item {
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "🎨 Appearance",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F3A)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    ModernSettingItem(
                        icon = Icons.Default.DarkMode,
                        title = "Dark Mode",
                        subtitle = "Currently enabled",
                        iconColor = Color(0xFF764BA2),
                        hasSwitch = true
                    )
                    Divider(color = Color.White.copy(alpha = 0.1f))
                    ModernSettingItem(
                        icon = Icons.Default.GridView,
                        title = "Compact View",
                        subtitle = "Show more items on screen",
                        iconColor = Color(0xFFFF9800),
                        hasSwitch = true
                    )
                }
            }
        }

        // Reports
        item {
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "📊 Reports & Export",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F3A)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    ModernSettingItem(
                        icon = Icons.Default.FileDownload,
                        title = "Export CSV Report",
                        subtitle = "Download comprehensive report",
                        iconColor = Color(0xFF2ECC71),
                        hasArrow = true
                    )
                    Divider(color = Color.White.copy(alpha = 0.1f))
                    ModernSettingItem(
                        icon = Icons.Default.PictureAsPdf,
                        title = "Generate PDF",
                        subtitle = "Create PDF with analytics",
                        iconColor = Color(0xFFFF6B9D),
                        hasArrow = true
                    )
                    Divider(color = Color.White.copy(alpha = 0.1f))
                    ModernSettingItem(
                        icon = Icons.Default.Share,
                        title = "Share Analytics",
                        subtitle = "Share insights with team",
                        iconColor = Color(0xFF56CCF2),
                        hasArrow = true
                    )
                }
            }
        }

        // Support
        item {
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "💬 Support",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F3A)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    ModernSettingItem(
                        icon = Icons.Default.Email,
                        title = "Contact Support",
                        subtitle = "Get help from our team",
                        iconColor = Color(0xFF56CCF2),
                        hasArrow = true
                    )
                    Divider(color = Color.White.copy(alpha = 0.1f))
                    ModernSettingItem(
                        icon = Icons.Default.Feedback,
                        title = "Send Feedback",
                        subtitle = "Help us improve",
                        iconColor = Color(0xFFFFA500),
                        hasArrow = true
                    )
                    Divider(color = Color.White.copy(alpha = 0.1f))
                    ModernSettingItem(
                        icon = Icons.Default.Info,
                        title = "About",
                        subtitle = "Version 1.0.0",
                        iconColor = Color(0xFF9E9E9E),
                        hasArrow = true
                    )
                }
            }
        }

        // Danger Zone
        item {
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "⚠️ Danger Zone",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B9D),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F3A)),
                shape = RoundedCornerShape(20.dp)
            ) {
                ModernSettingItem(
                    icon = Icons.Default.Delete,
                    title = "Reset All Data",
                    subtitle = "Permanently delete all data",
                    iconColor = Color(0xFFFF6B9D),
                    hasArrow = true
                )
            }
        }

        // Logout Button
        item {
            Spacer(modifier = Modifier.height(28.dp))
            Button(
                onClick = {
                    authViewModel.logout()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
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
                                listOf(Color(0xFFFF6B9D), Color(0xFFC06C84))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Logout",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun ProfileStatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradient))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    icon,
                    null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(32.dp)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        value,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        label,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ModernSettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    hasSwitch: Boolean = false,
    hasArrow: Boolean = false
) {
    var switchState by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = iconColor.copy(alpha = 0.2f),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        if (hasSwitch) {
            Switch(
                checked = switchState,
                onCheckedChange = { switchState = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = iconColor,
                    checkedTrackColor = iconColor.copy(alpha = 0.3f)
                )
            )
        }

        if (hasArrow) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Arrow",
                tint = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}