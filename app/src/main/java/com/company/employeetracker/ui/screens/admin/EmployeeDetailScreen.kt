package com.company.employeetracker.ui.screens.admin

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.company.employeetracker.data.database.entities.Attendance
import com.company.employeetracker.data.database.entities.User
import com.company.employeetracker.ui.theme.*
import com.company.employeetracker.viewmodel.AttendanceViewModel
import com.company.employeetracker.viewmodel.ReviewViewModel
import com.company.employeetracker.viewmodel.TaskViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(
    employee: User,
    employees: List<User> = listOf(employee),
    defaultCheckIn: LocalTime = LocalTime.of(9, 0),
    defaultCheckOut: LocalTime = LocalTime.of(18, 0),
    onBackClick: () -> Unit = {},
    taskViewModel: TaskViewModel = viewModel(),
    reviewViewModel: ReviewViewModel = viewModel(),
    attendanceViewModel: AttendanceViewModel = viewModel()
) {
    LaunchedEffect(employee.id) {
        taskViewModel.loadTasksForEmployee(employee.id)
        reviewViewModel.loadReviewsForEmployee(employee.id)
        attendanceViewModel.loadAttendanceForEmployee(employee.id)
    }

    val tasks by taskViewModel.employeeTasks.collectAsState()
    val reviews by reviewViewModel.employeeReviews.collectAsState()
    val attendance by attendanceViewModel.employeeAttendance.collectAsState()
    val averageRating by reviewViewModel.averageRating.collectAsState()

    var showMarkAttendanceDialog by remember { mutableStateOf(false) }

    val avatarGradient = when (employee.department) {
        "Design" -> listOf(Color(0xFFE91E63), Color(0xFFC2185B))
        "Engineering" -> listOf(Color(0xFF667EEA), Color(0xFF764BA2))
        "Analytics" -> listOf(Color(0xFFFF9800), Color(0xFFF57C00))
        "Product" -> listOf(Color(0xFF00BCD4), Color(0xFF0097A7))
        else -> listOf(Color(0xFF2ECC71), Color(0xFF27AE60))
    }

    var expandedEmployeeMenu by remember { mutableStateOf(false) }
    var selectedEmployeeIndex by remember { mutableStateOf(1) }
    val employeeMenuList = listOf("All Employees") + employees.map { it.name }

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
                    .background(Brush.horizontalGradient(avatarGradient))
                    .padding(24.dp)
            ) {
                Column {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = employee.name.split(" ").mapNotNull { it.firstOrNull() }
                                .take(2).joinToString(""),
                            color = avatarGradient[0],
                            fontSize = 40.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = employee.name,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = employee.designation,
                            fontSize = 15.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = employee.department,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }

        // Gradient Stats
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailStatCard(
                    icon = Icons.Default.Assignment,
                    value = tasks.size.toString(),
                    label = "Tasks",
                    gradient = listOf(Color(0xFF667EEA), Color(0xFF764BA2)),
                    modifier = Modifier.weight(1f)
                )
                DetailStatCard(
                    icon = Icons.Default.Star,
                    value = String.format("%.1f", averageRating),
                    label = "Rating",
                    gradient = listOf(Color(0xFFFFA500), Color(0xFFFF6347)),
                    modifier = Modifier.weight(1f)
                )
                DetailStatCard(
                    icon = Icons.Default.CheckCircle,
                    value = attendance.count { it.status == "Present" }.toString(),
                    label = "Present",
                    gradient = listOf(Color(0xFF2ECC71), Color(0xFF27AE60)),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Employee Info Card
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
                        text = "📋 Employee Information",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ModernInfoRow(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = employee.email,
                        iconColor = Color(0xFF56CCF2)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    ModernInfoRow(
                        icon = Icons.Default.Business,
                        label = "Department",
                        value = employee.department,
                        iconColor = Color(0xFF764BA2)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    ModernInfoRow(
                        icon = Icons.Default.Work,
                        label = "Designation",
                        value = employee.designation,
                        iconColor = Color(0xFFFF9800)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    ModernInfoRow(
                        icon = Icons.Default.CalendarToday,
                        label = "Joining Date",
                        value = employee.joiningDate,
                        iconColor = Color(0xFF2ECC71)
                    )
                    if (employee.contact.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        ModernInfoRow(
                            icon = Icons.Default.Phone,
                            label = "Contact",
                            value = employee.contact,
                            iconColor = Color(0xFFE91E63)
                        )
                    }
                }
            }
        }

        // Mark Attendance Button
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { showMarkAttendanceDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF2ECC71), Color(0xFF27AE60))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Mark Attendance",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Mark Attendance",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Attendance History
        item {
            Spacer(modifier = Modifier.height(28.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📅 Attendance History",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF667EEA).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "${attendance.size} records",
                        fontSize = 12.sp,
                        color = Color(0xFF667EEA),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        if (attendance.isEmpty()) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.EventBusy,
                            null,
                            tint = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "No attendance records",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 15.sp
                        )
                    }
                }
            }
        } else {
            items(attendance.take(10)) { record ->
                Spacer(modifier = Modifier.height(12.dp))
                ModernAttendanceCard(
                    record = record,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (showMarkAttendanceDialog) {
        val selectedEmployee: User? =
            if (selectedEmployeeIndex == 0) null else employees.getOrNull(selectedEmployeeIndex - 1)
        MarkAttendanceDialog(
            selectedEmployee = selectedEmployee ?: employee,
            isAllSelected = (selectedEmployee == null),
            employees = employees,
            defaultCheckIn = defaultCheckIn,
            defaultCheckOut = defaultCheckOut,
            onDismiss = { showMarkAttendanceDialog = false },
            onAttendanceMarked = { attendanceItem ->
                if (selectedEmployee == null) {
                    employees.forEach { emp ->
                        val copy = attendanceItem.copy(employeeId = emp.id)
                        attendanceViewModel.markAttendance(copy)
                    }
                } else {
                    attendanceViewModel.markAttendance(attendanceItem)
                }
                showMarkAttendanceDialog = false
            }
        )
    }
}

@Composable
fun DetailStatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
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
                    modifier = Modifier.size(28.dp)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        value,
                        fontSize = 24.sp,
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
fun ModernInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    iconColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = iconColor.copy(alpha = 0.2f),
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun ModernAttendanceCard(record: Attendance, modifier: Modifier = Modifier) {
    val statusColor = when (record.status) {
        "Present" -> Color(0xFF2ECC71)
        "Absent" -> Color(0xFFFF6B9D)
        "Half Day" -> Color(0xFFFF9800)
        "Leave" -> Color(0xFF56CCF2)
        else -> Color.Gray
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F3A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = statusColor.copy(alpha = 0.2f),
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (record.status) {
                            "Present" -> Icons.Default.CheckCircle
                            "Absent" -> Icons.Default.Cancel
                            "Half Day" -> Icons.Default.Schedule
                            "Leave" -> Icons.Default.EventBusy
                            else -> Icons.Default.HelpOutline
                        },
                        contentDescription = record.status,
                        tint = statusColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.date,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "In: ${record.checkInTime}${if (record.checkOutTime != null) " • Out: ${record.checkOutTime}" else ""}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
                if (record.remarks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = record.remarks,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        maxLines = 1
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = statusColor.copy(alpha = 0.2f)
            ) {
                Text(
                    text = record.status,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkAttendanceDialog(
    selectedEmployee: User,
    isAllSelected: Boolean,
    employees: List<User>,
    defaultCheckIn: LocalTime,
    defaultCheckOut: LocalTime,
    onDismiss: () -> Unit,
    onAttendanceMarked: (Attendance) -> Unit
) {
    var selectedStatus by remember { mutableStateOf("Present") }
    var checkInLocal by remember { mutableStateOf(defaultCheckIn) }
    var checkOutLocal by remember { mutableStateOf(defaultCheckOut) }
    var remarks by remember { mutableStateOf("") }

    val statuses = listOf("Present", "Absent", "Half Day", "Leave")
    val context = LocalContext.current
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())

    fun openTimePicker(initial: LocalTime, onTimeSelected: (LocalTime) -> Unit) {
        val hour = initial.hour
        val minute = initial.minute
        TimePickerDialog(context, { _, selectedHour, selectedMinute ->
            onTimeSelected(LocalTime.of(selectedHour, selectedMinute))
        }, hour, minute, false).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F3A))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isAllSelected) "Mark All" else "Mark Attendance",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isAllSelected) "${employees.size} employees" else selectedEmployee.name,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Status",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column {
                    statuses.chunked(2).forEach { rowStatuses ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowStatuses.forEach { status ->
                                FilterChip(
                                    selected = selectedStatus == status,
                                    onClick = { selectedStatus = status },
                                    label = { Text(status, fontSize = 13.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF2ECC71),
                                        selectedLabelColor = Color.White,
                                        containerColor = Color(0xFF0A0E27),
                                        labelColor = Color.White.copy(alpha = 0.7f)
                                    )
                                )
                            }
                            if (rowStatuses.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = checkInLocal.format(timeFormatter),
                    onValueChange = {},
                    label = { Text("Check In", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openTimePicker(checkInLocal) { checkInLocal = it } },
                    trailingIcon = {
                        IconButton(onClick = { openTimePicker(checkInLocal) { checkInLocal = it } }) {
                            Icon(Icons.Default.AccessTime, null, tint = Color(0xFF2ECC71))
                        }
                    },
                    readOnly = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF2ECC71),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = checkOutLocal.format(timeFormatter),
                    onValueChange = {},
                    label = { Text("Check Out", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openTimePicker(checkOutLocal) { checkOutLocal = it } },
                    trailingIcon = {
                        IconButton(onClick = { openTimePicker(checkOutLocal) { checkOutLocal = it } }) {
                            Icon(Icons.Default.AccessTime, null, tint = Color(0xFF2ECC71))
                        }
                    },
                    readOnly = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF2ECC71),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Remarks", color = Color.White.copy(alpha = 0.7f)) },
                    placeholder = { Text("Optional notes...", color = Color.White.copy(alpha = 0.5f)) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF2ECC71),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("Cancel", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            val formattedCheckIn = checkInLocal.format(timeFormatter)
                            val formattedCheckOut = checkOutLocal.format(timeFormatter)

                            val attendance = Attendance(
                                employeeId = selectedEmployee.id,
                                date = LocalDate.now().toString(),
                                checkInTime = formattedCheckIn,
                                checkOutTime = formattedCheckOut,
                                status = selectedStatus,
                                remarks = remarks
                            )
                            onAttendanceMarked(attendance)
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF2ECC71), Color(0xFF27AE60))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Mark", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}