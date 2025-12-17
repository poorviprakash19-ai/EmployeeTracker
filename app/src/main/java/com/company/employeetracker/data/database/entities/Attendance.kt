package com.company.employeetracker.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val employeeId: Int,
    val date: String, // Format: YYYY-MM-DD
    val checkInTime: String, // Format: HH:mm
    val checkOutTime: String? = null, // Format: HH:mm
    val status: String, // "Present", "Absent", "Half Day", "Leave"
    val remarks: String = "",
    val markedBy: String = "Admin"
)