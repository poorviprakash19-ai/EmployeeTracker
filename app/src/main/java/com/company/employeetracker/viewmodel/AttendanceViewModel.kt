package com.company.employeetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.company.employeetracker.data.database.AppDatabase
import com.company.employeetracker.data.database.entities.Attendance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AttendanceViewModel(application: Application) : AndroidViewModel(application) {
    private val attendanceDao = AppDatabase.getDatabase(application).attendanceDao()

    private val _allAttendance = MutableStateFlow<List<Attendance>>(emptyList())
    val allAttendance: StateFlow<List<Attendance>> = _allAttendance

    private val _employeeAttendance = MutableStateFlow<List<Attendance>>(emptyList())
    val employeeAttendance: StateFlow<List<Attendance>> = _employeeAttendance

    private val _todayAttendance = MutableStateFlow<List<Attendance>>(emptyList())
    val todayAttendance: StateFlow<List<Attendance>> = _todayAttendance

    init {
        loadAllAttendance()
    }

    private fun loadAllAttendance() {
        viewModelScope.launch {
            attendanceDao.getAllAttendance().collect { attendance ->
                _allAttendance.value = attendance
            }
        }
    }

    fun loadAttendanceForEmployee(employeeId: Int) {
        viewModelScope.launch {
            attendanceDao.getAttendanceByEmployee(employeeId).collect { attendance ->
                _employeeAttendance.value = attendance
            }
        }
    }

    fun loadTodayAttendance(date: String) {
        viewModelScope.launch {
            attendanceDao.getAttendanceByDate(date).collect { attendance ->
                _todayAttendance.value = attendance
            }
        }
    }

    fun markAttendance(attendance: Attendance) {
        viewModelScope.launch {
            attendanceDao.insertAttendance(attendance)
        }
    }

    fun updateAttendance(attendance: Attendance) {
        viewModelScope.launch {
            attendanceDao.updateAttendance(attendance)
        }
    }

    suspend fun getAttendanceForDate(employeeId: Int, date: String): Attendance? {
        return attendanceDao.getAttendanceByEmployeeAndDate(employeeId, date)
    }

    suspend fun getPresentDays(employeeId: Int): Int {
        return attendanceDao.getPresentDays(employeeId)
    }

    suspend fun getAbsentDays(employeeId: Int): Int {
        return attendanceDao.getAbsentDays(employeeId)
    }
}