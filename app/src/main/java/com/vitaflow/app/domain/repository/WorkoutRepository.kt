package com.vitaflow.app.domain.repository

import com.vitaflow.app.common.Resource
import com.vitaflow.app.domain.models.FeaturedWorkout
import com.vitaflow.app.domain.models.QuickTraining
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    suspend fun getFeaturedWorkout(): Flow<Resource<FeaturedWorkout>>
    suspend fun getQuickTrainings(limit: Int = 10): Flow<Resource<List<QuickTraining>>>
    suspend fun getTrainingsByCategory(
        category: String? = null,
        difficulty: String? = null,
        limit: Int = 10
    ): Flow<Resource<List<QuickTraining>>>
}
