package com.company.employeetracker.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "reviews",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Review(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val employeeId: Int,
    val date: String,
    val quality: Float,
    val communication: Float,
    val innovation: Float,
    val timeliness: Float,
    val attendance: Float,
    val overallRating: Float,
    val remarks: String,
    val reviewedBy: String = ""
)