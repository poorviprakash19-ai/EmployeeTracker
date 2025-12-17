package com.company.employeetracker.ui.screens.employee

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.company.employeetracker.data.database.entities.User
import com.company.employeetracker.ui.theme.*
import com.company.employeetracker.viewmodel.ReviewViewModel
import kotlin.math.cos
import kotlin.math.sin

// --- Smooth color interpolation anchors ---
private val ColorRed = Color(0xFFFF5252)
private val ColorAmber = Color(0xFFFFC107)
private val ColorGreen = Color(0xFF4CAF50)

/**
 * Smooth color interpolation over 0..5 range (red -> amber -> green).
 */
fun getRatingColorSmooth(rating: Float): Color {
    val safe = rating.coerceIn(0f, 5f)
    val fraction = safe / 5f
    return if (fraction <= 0.5f) {
        val t = (fraction / 0.5f).coerceIn(0f, 1f)
        lerp(ColorRed, ColorAmber, t)
    } else {
        val t = ((fraction - 0.5f) / 0.5f).coerceIn(0f, 1f)
        lerp(ColorAmber, ColorGreen, t)
    }
}

@Composable
fun EmployeeReviewsScreen(
    currentUser: User,
    onBackClick: () -> Unit = {},
    reviewViewModel: ReviewViewModel = viewModel()
) {
    LaunchedEffect(currentUser.id) {
        reviewViewModel.loadReviewsForEmployee(currentUser.id)
    }

    val reviews by reviewViewModel.employeeReviews.collectAsState()
    val latestReview = reviews.firstOrNull()

    // Entrance animation for the whole screen
    var stageAnim by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { stageAnim = true }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(GreenLight.copy(alpha = 0.06f), Color(0xFFF5F5F5))
                )
            ),
        contentPadding = PaddingValues(bottom = 32.dp)
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
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        IconButton(onClick = { /* More options */ }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Reviews",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Performance Reviews",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${reviews.size} review${if (reviews.size != 1) "s" else ""} received",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }

        // Overall Rating Card
        if (latestReview != null) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Compute dynamic color based on latest overall rating
                val overallTargetColor = getRatingColorSmooth(latestReview.overallRating)
                val overallColor by animateColorAsState(
                    targetValue = overallTargetColor,
                    animationSpec = tween(durationMillis = 600),
                    label = "overallColor"
                )

                // Animated numeric display
                val animatedOverall by animateFloatAsState(
                    targetValue = latestReview.overallRating,
                    animationSpec = tween(700),
                    label = "animatedOverall"
                )

                AnimatedVisibility(
                    visible = stageAnim,
                    enter = fadeIn(tween(500)),
                    exit = fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .heightIn(min = 140.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Overall Rating",
                                    fontSize = 16.sp,
                                    color = Color(0xFF757575)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = String.format("%.1f", animatedOverall),
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF212121)
                                    )
                                    Text(
                                        text = " / 5.0",
                                        fontSize = 24.sp,
                                        color = Color(0xFF757575),
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = overallColor.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = when {
                                            latestReview.overallRating >= 4.5f -> "Excellent"
                                            latestReview.overallRating >= 3.5f -> "Good"
                                            else -> "Needs Improvement"
                                        },
                                        color = overallColor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }

                            // Animated star badge with pulsing scale or shake when low
                            val pulse = rememberInfiniteTransition(label = "pulse")
                            val starScale by pulse.animateFloat(
                                initialValue = 1.0f,
                                targetValue = 1.06f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(900, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "starScale"
                            )

                            // Shake animation for low rating
                            val shakeOffset by if (latestReview.overallRating < 3.5f) {
                                pulse.animateFloat(
                                    initialValue = -6f,
                                    targetValue = 6f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(120, easing = LinearEasing),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "shakeOffset"
                                )
                            } else {
                                remember { mutableStateOf(0f) }
                            }

                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(overallColor.copy(alpha = 0.12f))
                                    .offset(x = with(LocalDensity.current) { shakeOffset.toDp() }),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Star",
                                    tint = overallColor,
                                    modifier = Modifier.size(60.dp)
                                )

                                // If Excellent, show trophy + confetti overlay
                                if (latestReview.overallRating >= 4.5f) {
                                    TrophyWithConfetti()
                                }
                            }
                        }
                    }
                }
            }

            // Skills Overview
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Skills Overview",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "Chart",
                                tint = GreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            SkillBarAnimated("Quality", latestReview.quality, GreenPrimary)
                            SkillBarAnimated("Communication", latestReview.communication, AccentBlue)
                            SkillBarAnimated("Innovation", latestReview.innovation, PurplePrimary)
                            SkillBarAnimated("Timeliness", latestReview.timeliness, AccentOrange)
                            SkillBarAnimated("Attendance", latestReview.attendance, AccentGreen)
                        }
                    }
                }
            }

            // Performance Radar
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Performance Radar",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = "Radar",
                                    tint = GreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Determine radar color based on average of values
                        val avg = (latestReview.quality + latestReview.communication +
                                latestReview.innovation + latestReview.timeliness +
                                latestReview.attendance) / 5f
                        val radarTargetColor = getRatingColorSmooth(avg)
                        val radarColor by animateColorAsState(
                            targetValue = radarTargetColor,
                            animationSpec = tween(600),
                            label = "radarColor"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(270.dp)
                        ) {
                            RadarChart(
                                values = listOf(
                                    latestReview.quality,
                                    latestReview.communication,
                                    latestReview.innovation,
                                    latestReview.timeliness,
                                    latestReview.attendance
                                ),
                                labels = listOf("Quality", "Communication", "Innovation", "Timeliness", "Attendance"),
                                modifier = Modifier.fillMaxSize(),
                                fillColor = radarColor.copy(alpha = 0.28f),
                                strokeColor = radarColor
                            )
                        }
                    }
                }
            }

            // Skill Breakdown
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Skill Breakdown",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = "Breakdown",
                                tint = AccentOrange,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        SkillProgressAnimated("Quality", latestReview.quality, GreenPrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        SkillProgressAnimated("Communication", latestReview.communication, AccentBlue)
                        Spacer(modifier = Modifier.height(12.dp))
                        SkillProgressAnimated("Innovation", latestReview.innovation, PurplePrimary)
                        Spacer(modifier = Modifier.height(12.dp))
                        SkillProgressAnimated("Timeliness", latestReview.timeliness, AccentOrange)
                        Spacer(modifier = Modifier.height(12.dp))
                        SkillProgressAnimated("Attendance", latestReview.attendance, AccentGreen)
                    }
                }
            }
        }

        // Review History Header
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Review History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History",
                    tint = Color(0xFF757575),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Review History Items
        items(reviews) { review ->
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(GreenPrimary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Reviewer",
                                    tint = GreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = String.format("%.1f/5.0", review.overallRating),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = review.date,
                                    fontSize = 12.sp,
                                    color = Color(0xFF757575)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                review.overallRating >= 4.5f -> GreenLight.copy(alpha = 0.1f)
                                review.overallRating >= 3.5f -> AccentOrange.copy(alpha = 0.1f)
                                else -> AccentRed.copy(alpha = 0.1f)
                            }
                        ) {
                            Text(
                                text = when {
                                    review.overallRating >= 4.5f -> "Excellent"
                                    review.overallRating >= 3.5f -> "Good"
                                    else -> "Fair"
                                },
                                color = when {
                                    review.overallRating >= 4.5f -> GreenPrimary
                                    review.overallRating >= 3.5f -> AccentOrange
                                    else -> AccentRed
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = review.remarks,
                        fontSize = 14.sp,
                        color = Color(0xFF424242),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Skill Ratings",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SkillRatingItem("Quality", review.quality)
                        SkillRatingItem("Communication", review.communication)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SkillRatingItem("Innovation", review.innovation)
                        SkillRatingItem("Timeliness", review.timeliness)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    SkillRatingItem("Attendance", review.attendance, modifier = Modifier.fillMaxWidth(0.5f))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// --- Helper Composables with animations ---
@Composable
fun TrophyWithConfetti() {
    // Simple confetti: animated small circles moving down
    val confettiCount = 12
    val infinite = rememberInfiniteTransition(label = "confetti")
    val progress by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(1200)),
        label = "confettiProgress"
    )

    Box(contentAlignment = Alignment.TopCenter) {
        // Trophy icon
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = "Trophy",
            tint = Color(0xFFFFD700),
            modifier = Modifier
                .size(36.dp)
                .offset(y = (-12).dp)
        )

        // Confetti canvas overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in 0 until confettiCount) {
                val x = size.width * ((i + progress) % confettiCount) / confettiCount
                val y = size.height * ((progress + i * 0.07f) % 1f)
                val sizeDp = 6.dp.toPx() * (1f + (i % 3) * 0.2f)
                drawCircle(
                    color = listOf(ColorRed, ColorAmber, ColorGreen, AccentBlue, PurplePrimary)[i % 5],
                    radius = sizeDp,
                    center = Offset(x, y)
                )
            }
        }
    }
}

@Composable
fun SkillBarAnimated(label: String, value: Float, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = String.format("%.1f", value),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(6.dp)
                .height(100.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(3.dp))
        ) {
            val animatedFill by animateFloatAsState(
                targetValue = value / 5f,
                animationSpec = tween(600),
                label = "skillBar_$label"
            )
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight(animatedFill)
                    .background(color, RoundedCornerShape(3.dp))
                    .align(Alignment.BottomCenter)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color(0xFF757575),
            maxLines = 1
        )
    }
}

@Composable
fun SkillProgressAnimated(label: String, value: Float, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = String.format("%.1f/5", value),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        val animated by animateFloatAsState(
            targetValue = value / 5f,
            animationSpec = tween(700),
            label = "skillProgress_$label"
        )
        LinearProgressIndicator(
            progress = { animated },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = color,
            trackColor = Color(0xFFE0E0E0)
        )
    }
}

@Composable
fun SkillRatingItem(label: String, rating: Float, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = label,
                tint = AccentYellow,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF757575)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = String.format("%.1f", rating),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )
    }
}

@Composable
fun RadarChart(
    values: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    maxValue: Float = 5f,
    fillColor: Color = GreenPrimary.copy(alpha = 0.3f),
    strokeColor: Color = GreenPrimary
) {
    Canvas(modifier = modifier.padding(12.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f * 0.7f
        val angleStep = 360f / values.size

        // Draw background polygons
        for (level in 5 downTo 1) {
            val levelRadius = radius * (level / 5f)
            val path = Path()
            for (i in values.indices) {
                val angle = Math.toRadians((angleStep * i - 90).toDouble())
                val x = center.x + (levelRadius * cos(angle)).toFloat()
                val y = center.y + (levelRadius * sin(angle)).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(
                path = path,
                color = Color(0xFFECEFF1),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Draw axes
        for (i in values.indices) {
            val angle = Math.toRadians((angleStep * i - 90).toDouble())
            val endX = center.x + (radius * cos(angle)).toFloat()
            val endY = center.y + (radius * sin(angle)).toFloat()
            drawLine(
                color = Color(0xFFE0E0E0),
                start = center,
                end = Offset(endX, endY),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw data polygon
        val dataPath = Path()
        for (i in values.indices) {
            val normalizedValue = values[i] / maxValue
            val angle = Math.toRadians((angleStep * i - 90).toDouble())
            val x = center.x + (radius * normalizedValue * cos(angle)).toFloat()
            val y = center.y + (radius * normalizedValue * sin(angle)).toFloat()
            if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
        }
        dataPath.close()

        drawPath(
            path = dataPath,
            color = fillColor,
            style = androidx.compose.ui.graphics.drawscope.Fill
        )
        drawPath(
            path = dataPath,
            color = strokeColor,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}