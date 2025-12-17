package com.company.employeetracker.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val employeeId: Int,
    val title: String,
    val description: String,
    val priority: String, // "High", "Medium", "Low"
    val status: String, // "Pending", "Active", "Done"
    val deadline: String,
    val assignedDate: String,
    val assignedBy: String = ""
)