package com.company.employeetracker.data.database.dao

import androidx.room.*
import com.company.employeetracker.data.database.entities.Attendance
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance ORDER BY date DESC, checkInTime DESC")
    fun getAllAttendance(): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE employeeId = :employeeId ORDER BY date DESC")
    fun getAttendanceByEmployee(employeeId: Int): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE date = :date")
    fun getAttendanceByDate(date: String): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance WHERE employeeId = :employeeId AND date = :date")
    suspend fun getAttendanceByEmployeeAndDate(employeeId: Int, date: String): Attendance?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance): Long

    @Update
    suspend fun updateAttendance(attendance: Attendance)

    @Delete
    suspend fun deleteAttendance(attendance: Attendance)

    @Query("SELECT COUNT(*) FROM attendance WHERE employeeId = :employeeId AND status = 'Present'")
    suspend fun getPresentDays(employeeId: Int): Int

    @Query("SELECT COUNT(*) FROM attendance WHERE employeeId = :employeeId AND status = 'Absent'")
    suspend fun getAbsentDays(employeeId: Int): Int
}