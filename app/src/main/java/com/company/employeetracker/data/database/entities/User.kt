package com.company.employeetracker.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val password: String,
    val name: String,
    val role: String, // "admin" or "employee"
    val designation: String,
    val department: String = "",
    val joiningDate: String = "",
    val contact: String = "",
    val profileImage: String = ""
)