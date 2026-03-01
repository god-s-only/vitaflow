package com.vitaflow.app.domain.usecase.workout

import com.vitaflow.app.common.Resource
import com.vitaflow.app.domain.models.FeaturedWorkout
import com.vitaflow.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFeaturedWorkoutUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(): Flow<Resource<FeaturedWorkout>> {
        return repository.getFeaturedWorkout()
    }
}
