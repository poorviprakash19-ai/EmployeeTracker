package com.company.employeetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.company.employeetracker.data.database.AppDatabase
import com.company.employeetracker.data.database.entities.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.company.employeetracker.data.repository.FirebaseRepository

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskDao = AppDatabase.getDatabase(application).taskDao()
    private val firebaseRepo = FirebaseRepository()

    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())
    val allTasks: StateFlow<List<Task>> = _allTasks

    private val _employeeTasks = MutableStateFlow<List<Task>>(emptyList())
    val employeeTasks: StateFlow<List<Task>> = _employeeTasks

    private val _pendingCount = MutableStateFlow(0)
    val pendingCount: StateFlow<Int> = _pendingCount

    private val _activeCount = MutableStateFlow(0)
    val activeCount: StateFlow<Int> = _activeCount

    private val _completedCount = MutableStateFlow(0)
    val completedCount: StateFlow<Int> = _completedCount

    init {
        loadAllTasks()
    }

    private fun loadAllTasks() {
        viewModelScope.launch {
            try {
                firebaseRepo.getAllTasksFlow().collect { tasks ->
                    _allTasks.value = tasks
                    updateTaskCounts(tasks)
                    // Sync to local DB
                    tasks.forEach { taskDao.insertTask(it) }
                }
            } catch (e: Exception) {
                // Fallback to local database
                taskDao.getAllTasks().collect { tasks ->
                    _allTasks.value = tasks
                    updateTaskCounts(tasks)
                }
            }
        }
    }

    fun loadTasksForEmployee(employeeId: Int) {
        viewModelScope.launch {
            try {
                firebaseRepo.getTasksByEmployeeFlow(employeeId).collect { tasks ->
                    _employeeTasks.value = tasks
                    updateTaskCounts(tasks)
                    // Sync to local DB
                    tasks.forEach { taskDao.insertTask(it) }
                }
            } catch (e: Exception) {
                // Fallback to local database
                taskDao.getTasksByEmployee(employeeId).collect { tasks ->
                    _employeeTasks.value = tasks
                    updateTaskCounts(tasks)
                }
            }
        }
    }

    private fun updateTaskCounts(tasks: List<Task>) {
        _pendingCount.value = tasks.count { it.status == "Pending" }
        _activeCount.value = tasks.count { it.status == "Active" }
        _completedCount.value = tasks.count { it.status == "Done" }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                firebaseRepo.addTask(task)
            } catch (e: Exception) {
                // Fallback to local database
                taskDao.insertTask(task)
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                firebaseRepo.updateTask(task)
            } catch (e: Exception) {
                // Fallback to local database
                taskDao.updateTask(task)
            }
        }
    }

    fun updateTaskStatus(taskId: Int, newStatus: String) {
        viewModelScope.launch {
            try {
                val task = taskDao.getTaskById(taskId)
                task?.let {
                    val updatedTask = it.copy(status = newStatus)
                    firebaseRepo.updateTask(updatedTask)
                }
            } catch (e: Exception) {
                // Fallback to local database
                val task = taskDao.getTaskById(taskId)
                task?.let {
                    val updatedTask = it.copy(status = newStatus)
                    taskDao.updateTask(updatedTask)
                }
            }
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            try {
                firebaseRepo.deleteTask(taskId)
            } catch (e: Exception) {
                // Fallback to local database
                val task = taskDao.getTaskById(taskId)
                task?.let { taskDao.deleteTask(it) }
            }
        }
    }
}