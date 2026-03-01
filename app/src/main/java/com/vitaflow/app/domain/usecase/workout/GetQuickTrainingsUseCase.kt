package com.vitaflow.app.domain.usecase.workout

import com.vitaflow.app.common.Resource
import com.vitaflow.app.domain.models.QuickTraining
import com.vitaflow.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuickTrainingsUseCase @Inject constructor(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(limit: Int = 10): Flow<Resource<List<QuickTraining>>> {
        return repository.getQuickTrainings(limit)
    }
}
