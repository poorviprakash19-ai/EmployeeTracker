package com.company.employeetracker.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.company.employeetracker.data.database.entities.User
import com.company.employeetracker.ui.components.*
import com.company.employeetracker.ui.theme.*
import com.company.employeetracker.viewmodel.*
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEmployeesScreen(
    employeeViewModel: EmployeeViewModel = viewModel(),
    taskViewModel: TaskViewModel = viewModel(),
    reviewViewModel: ReviewViewModel = viewModel()
) {
    val employees by employeeViewModel.employees.collectAsState()
    val allTasks by taskViewModel.allTasks.collectAsState()
    val allReviews by reviewViewModel.allReviews.collectAsState()

    var selectedEmployee by remember { mutableStateOf<User?>(null) }
    var showAddEmployeeDialog by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedDepartment by remember { mutableStateOf("All Departments") }

    val departments = listOf(
        "All Departments", "Engineering", "Design", "Analytics", "Product", "Management"
    )

    val filteredEmployees = employees.filter {
        (it.name.contains(searchQuery, true) ||
                it.designation.contains(searchQuery, true)) &&
                (selectedDepartment == "All Departments" || it.department == selectedDepartment)
    }

    val activeEmployees = employees.count { emp ->
        allTasks.any { it.employeeId == emp.id && it.status == "Active" }
    }

    val avgRating =
        if (allReviews.isNotEmpty()) allReviews.map { it.overallRating }.average().toFloat() else 0f

    if (selectedEmployee != null) {
        EmployeeDetailScreen(
            employee = selectedEmployee!!,
            onBackClick = { selectedEmployee = null }
        )
        return
    }

    Scaffold(
        containerColor = Color(0xFF0A0E27),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddEmployeeDialog = true },
                containerColor = Color.Transparent,
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF2ECC71), Color(0xFF27AE60))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Employee",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF0A0E27))
        ) {

            /** GRADIENT HEADER **/
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF2ECC71), Color(0xFF27AE60))
                        )
                    )
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Team Directory",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Light
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "👥 Employees",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.People,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            LazyColumn {

                /** MODERN SEARCH BAR **/
                item {
                    Spacer(Modifier.height(20.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "Search employees...",
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                null,
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF2ECC71),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedContainerColor = Color(0xFF1A1F3A),
                            unfocusedContainerColor = Color(0xFF1A1F3A)
                        )
                    )
                }

                /** DEPARTMENT FILTER **/
                item {
                    Spacer(Modifier.height(12.dp))
                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        OutlinedTextField(
                            value = selectedDepartment,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF2ECC71),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedContainerColor = Color(0xFF1A1F3A),
                                unfocusedContainerColor = Color(0xFF1A1F3A)
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color(0xFF1A1F3A))
                        ) {
                            departments.forEach {
                                DropdownMenuItem(
                                    text = { Text(it, color = Color.White) },
                                    onClick = {
                                        selectedDepartment = it
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                /** MINI STATS **/
                item {
                    Spacer(Modifier.height(20.dp))
                    Row(
                        Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MiniStatCard(
                            value = filteredEmployees.size.toString(),
                            label = "Total",
                            gradient = listOf(Color(0xFF667EEA), Color(0xFF764BA2)),
                            modifier = Modifier.weight(1f)
                        )
                        MiniStatCard(
                            value = activeEmployees.toString(),
                            label = "Active",
                            gradient = listOf(Color(0xFF2ECC71), Color(0xFF27AE60)),
                            modifier = Modifier.weight(1f)
                        )
                        MiniStatCard(
                            value = String.format("%.1f", avgRating),
                            label = "Rating",
                            gradient = listOf(Color(0xFFFFA500), Color(0xFFFF6347)),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                /** LIST HEADER **/
                item {
                    Spacer(Modifier.height(28.dp))
                    Text(
                        "All Team Members (${filteredEmployees.size})",
                        modifier = Modifier.padding(horizontal = 20.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                /** EMPTY STATE **/
                if (filteredEmployees.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.SearchOff,
                                    null,
                                    tint = Color.White.copy(alpha = 0.3f),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    "No employees found",
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                } else {
                    items(filteredEmployees, key = { it.id }) { employee ->
                        val empTasks = allTasks.filter { it.employeeId == employee.id }
                        val empReviews = allReviews.filter { it.employeeId == employee.id }
                        val rating =
                            if (empReviews.isNotEmpty())
                                empReviews.map { it.overallRating }.average().toFloat()
                            else 0f

                        Spacer(Modifier.height(12.dp))
                        ModernEmployeeListCard(
                            employee = employee,
                            taskCount = empTasks.size,
                            rating = rating,
                            modifier = Modifier.padding(horizontal = 20.dp),
                            onClick = { selectedEmployee = employee }
                        )
                    }
                }

                item { Spacer(Modifier.height(100.dp)) }
            }
        }
    }

    if (showAddEmployeeDialog) {
        AddEmployeeDialog(
            onDismiss = { showAddEmployeeDialog = false },
            onEmployeeAdded = { showAddEmployeeDialog = false }
        )
    }
}

/** MINI STAT CARD WITH GRADIENT **/
@Composable
private fun MiniStatCard(
    value: String,
    label: String,
    gradient: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(90.dp),
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
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ModernEmployeeListCard(
    employee: User,
    taskCount: Int,
    rating: Float,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        when (employee.department) {
                            "Design" -> Brush.linearGradient(
                                listOf(Color(0xFFE91E63), Color(0xFFC2185B))
                            )
                            "Engineering" -> Brush.linearGradient(
                                listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                            )
                            "Analytics" -> Brush.linearGradient(
                                listOf(Color(0xFFFF9800), Color(0xFFF57C00))
                            )
                            else -> Brush.linearGradient(
                                listOf(Color(0xFF2ECC71), Color(0xFF27AE60))
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = employee.name.split(" ").mapNotNull { it.firstOrNull() }
                        .take(2).joinToString(""),
                    color = Color.White,
                    fontSize = 20.sp,
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
                Text(
                    text = employee.designation,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF667EEA).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = employee.department,
                            fontSize = 10.sp,
                            color = Color(0xFF667EEA),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Assignment,
                            null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "$taskCount tasks",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFA500).copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            null,
                            tint = Color(0xFFFFA500),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            String.format("%.1f", rating),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFA500)
                        )
                    }
                }
            }
        }
    }
}