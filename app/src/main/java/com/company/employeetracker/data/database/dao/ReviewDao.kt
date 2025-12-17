package com.company.employeetracker.data.database.dao

import androidx.room.*
import com.company.employeetracker.data.database.entities.Review
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews")
    fun getAllReviews(): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE employeeId = :employeeId ORDER BY date DESC")
    fun getReviewsByEmployee(employeeId: Int): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE id = :reviewId")
    suspend fun getReviewById(reviewId: Int): Review?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review): Long

    @Update
    suspend fun updateReview(review: Review)

    @Delete
    suspend fun deleteReview(review: Review)

    @Query("SELECT AVG(overallRating) FROM reviews WHERE employeeId = :employeeId")
    suspend fun getAverageRating(employeeId: Int): Float?

    @Query("SELECT COUNT(*) FROM reviews")
    suspend fun getReviewCount(): Int
}