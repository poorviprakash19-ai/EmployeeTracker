package com.company.employeetracker.ui.screens.admin

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.company.employeetracker.ui.theme.*
import com.company.employeetracker.viewmodel.EmployeeViewModel
import com.company.employeetracker.viewmodel.ReviewViewModel
import com.company.employeetracker.viewmodel.TaskViewModel

@Composable
fun AdminAnalyticsScreen(
    employeeViewModel: EmployeeViewModel = viewModel(),
    taskViewModel: TaskViewModel = viewModel(),
    reviewViewModel: ReviewViewModel = viewModel()
) {
    val employees by employeeViewModel.employees.collectAsState()
    val allTasks by taskViewModel.allTasks.collectAsState()
    val allReviews by reviewViewModel.allReviews.collectAsState()
    val reviewCount by reviewViewModel.reviewCount.collectAsState()

    // Calculate analytics
    val topPerformers = allReviews
        .groupBy { it.employeeId }
        .mapValues { entry -> entry.value.map { it.overallRating }.average().toFloat() }
        .toList()
        .sortedByDescending { it.second }
        .take(3)

    val departmentEmployeeCounts = employees.groupBy { it.department }.mapValues { it.value.size }
    val totalEmployees = employees.size

    val completionRate = if (allTasks.isNotEmpty()) {
        (allTasks.count { it.status == "Done" } * 100) / allTasks.size
    } else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E27))
    ) {
        // Modern Header with Glassmorphism
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF667EEA),
                                Color(0xFF764BA2)
                            )
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "📊 Analytics",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Real-time performance insights",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Light
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            IconButton(onClick = { /* Export */ }) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Export",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Compact Stats Row
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CompactStatCard(
                    value = "$reviewCount",
                    label = "Reviews",
                    icon = Icons.Default.Assessment,
                    gradient = listOf(Color(0xFFFF6B9D), Color(0xFFC06C84)),
                    modifier = Modifier.weight(1f)
                )
                CompactStatCard(
                    value = "$completionRate%",
                    label = "Complete",
                    icon = Icons.Default.TrendingUp,
                    gradient = listOf(Color(0xFF56CCF2), Color(0xFF2F80ED)),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Top Performers - Leaderboard Style
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F3A)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "🏆 Leaderboard",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    topPerformers.forEachIndexed { index, (employeeId, rating) ->
                        val employee = employees.find { it.id == employeeId }
                        employee?.let {
                            LeaderboardItem(
                                rank = index + 1,
                                name = it.name,
                                department = it.department,
                                rating = rating,
                                isFirst = index == 0
                            )
                            if (index < topPerformers.size - 1) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }

        // Department Distribution - Donut Chart
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F3A)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Team Distribution",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Box(
                            modifier = Modifier.size(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ModernDonutChart(
                                data = departmentEmployeeCounts,
                                total = totalEmployees
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            departmentEmployeeCounts.toList().take(4).forEach { (dept, count) ->
                                DepartmentLegendItem(
                                    department = dept,
                                    count = count,
                                    total = totalEmployees
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }

        // Performance Trend - Gradient Line Chart
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1F3A)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Growth Trend",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GradientLineChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF2ECC71).copy(alpha = 0.1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFF2ECC71),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "+23% from last quarter",
                                color = Color(0xFF2ECC71),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Employee Ratings - Horizontal Bars
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F3A)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Performance Ratings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    employees.take(5).forEach { employee ->
                        val employeeReviews = allReviews.filter { it.employeeId == employee.id }
                        val avgRating = if (employeeReviews.isNotEmpty()) {
                            employeeReviews.map { it.overallRating }.average().toFloat()
                        } else 0f

                        ModernRatingBar(
                            name = employee.name,
                            rating = avgRating
                        )
                        Spacer(modifier = Modifier.height(12.dp))
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
fun CompactStatCard(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(gradient)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(28.dp)
                )

                Column {
                    Text(
                        text = value,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun LeaderboardItem(
    rank: Int,
    name: String,
    department: String,
    rating: Float,
    isFirst: Boolean
) {
    val rankColor = when (rank) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> Color(0xFF9E9E9E)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isFirst) rankColor.copy(alpha = 0.1f) else Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = rankColor,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "#$rank",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Text(
                    text = department,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFFA500).copy(alpha = 0.2f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFA500),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", rating),
                        color = Color(0xFFFFA500),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ModernDonutChart(data: Map<String, Int>, total: Int) {
    val colors = mapOf(
        "Engineering" to Color(0xFF667EEA),
        "Design" to Color(0xFFE91E63),
        "Product" to Color(0xFF00BCD4),
        "Marketing" to Color(0xFFFF9800),
        "Analytics" to Color(0xFF9C27B0),
        "Others" to Color(0xFF607D8B)
    )

    Canvas(modifier = Modifier.size(160.dp)) {
        val radius = size.minDimension / 2
        val center = Offset(size.width / 2, size.height / 2)
        val strokeWidth = 28.dp.toPx()
        var startAngle = -90f

        data.forEach { (dept, count) ->
            val sweepAngle = (count.toFloat() / total) * 360f
            val color = colors[dept] ?: Color.Gray

            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius + strokeWidth/2, center.y - radius + strokeWidth/2),
                size = Size((radius - strokeWidth/2) * 2, (radius - strokeWidth/2) * 2),
                style = Stroke(width = strokeWidth)
            )
            startAngle += sweepAngle
        }

        drawCircle(
            color = Color(0xFF0A0E27),
            radius = radius - strokeWidth,
            center = center
        )
    }
}

@Composable
fun DepartmentLegendItem(department: String, count: Int, total: Int) {
    val colors = mapOf(
        "Engineering" to Color(0xFF667EEA),
        "Design" to Color(0xFFE91E63),
        "Product" to Color(0xFF00BCD4),
        "Marketing" to Color(0xFFFF9800),
        "Analytics" to Color(0xFF9C27B0)
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(colors[department] ?: Color.Gray)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = department,
                fontSize = 13.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${(count * 100 / total)}%",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun GradientLineChart(modifier: Modifier = Modifier) {
    val data = listOf(2.5f, 3.2f, 2.9f, 3.8f, 4.2f, 3.9f, 4.6f)

    Canvas(modifier = modifier.padding(16.dp)) {
        val spacing = size.width / (data.size - 1)
        val maxValue = 5f
        val points = data.mapIndexed { index, value ->
            Offset(
                x = index * spacing,
                y = size.height - (value / maxValue) * size.height
            )
        }

        val path = androidx.compose.ui.graphics.Path()
        path.moveTo(0f, size.height)
        points.forEach { point ->
            path.lineTo(point.x, point.y)
        }
        path.lineTo(size.width, size.height)
        path.close()

        drawPath(
            path = path,
            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF667EEA).copy(alpha = 0.4f),
                    Color(0xFF667EEA).copy(alpha = 0.0f)
                )
            )
        )

        for (i in 0 until points.size - 1) {
            drawLine(
                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                ),
                start = points[i],
                end = points[i + 1],
                strokeWidth = 4.dp.toPx()
            )
        }

        points.forEach { point ->
            drawCircle(
                color = Color(0xFF667EEA),
                radius = 8.dp.toPx(),
                center = point
            )
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx(),
                center = point
            )
        }
    }
}

@Composable
fun ModernRatingBar(name: String, rating: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            fontSize = 14.sp,
            color = Color.White,
            modifier = Modifier.width(100.dp),
            maxLines = 1
        )

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(rating / 5f)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = String.format("%.1f", rating),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF667EEA)
        )
    }
}