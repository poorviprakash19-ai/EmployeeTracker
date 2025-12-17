package com.company.employeetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.company.employeetracker.data.database.AppDatabase
import com.company.employeetracker.data.database.entities.Review
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(application: Application) : AndroidViewModel(application) {
    private val reviewDao = AppDatabase.getDatabase(application).reviewDao()

    private val _allReviews = MutableStateFlow<List<Review>>(emptyList())
    val allReviews: StateFlow<List<Review>> = _allReviews

    private val _employeeReviews = MutableStateFlow<List<Review>>(emptyList())
    val employeeReviews: StateFlow<List<Review>> = _employeeReviews

    private val _averageRating = MutableStateFlow(0f)
    val averageRating: StateFlow<Float> = _averageRating

    private val _reviewCount = MutableStateFlow(0)
    val reviewCount: StateFlow<Int> = _reviewCount

    init {
        loadAllReviews()
    }

    private fun loadAllReviews() {
        viewModelScope.launch {
            reviewDao.getAllReviews().collect { reviews ->
                _allReviews.value = reviews
                _reviewCount.value = reviews.size
            }
        }
    }

    fun loadReviewsForEmployee(employeeId: Int) {
        viewModelScope.launch {
            reviewDao.getReviewsByEmployee(employeeId).collect { reviews ->
                _employeeReviews.value = reviews
            }

            val avgRating = reviewDao.getAverageRating(employeeId)
            _averageRating.value = avgRating ?: 0f
        }
    }

    fun addReview(review: Review) {
        viewModelScope.launch {
            reviewDao.insertReview(review)
        }
    }

    fun updateReview(review: Review) {
        viewModelScope.launch {
            reviewDao.updateReview(review)
        }
    }

    fun deleteReview(review: Review) {
        viewModelScope.launch {
            reviewDao.deleteReview(review)
        }
    }
}