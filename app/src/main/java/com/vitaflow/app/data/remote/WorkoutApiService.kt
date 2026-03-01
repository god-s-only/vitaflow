package com.vitaflow.app.data.remote

import com.vitaflow.app.data.remote.dto.FeaturedWorkoutResponse
import com.vitaflow.app.data.remote.dto.QuickTrainingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WorkoutApiService {
    
    @GET("/api/v1/workouts/featured")
    suspend fun getFeaturedWorkout(): Response<FeaturedWorkoutResponse>
    
    @GET("/api/v1/workouts/quick-trainings")
    suspend fun getQuickTrainings(
        @Query("limit") limit: Int = 10
    ): Response<QuickTrainingResponse>
    
    @GET("/api/v1/workouts/trainings")
    suspend fun getTrainingsByCategory(
        @Query("category") category: String? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("limit") limit: Int = 10
    ): Response<QuickTrainingResponse>
}
