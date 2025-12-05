package com.vitaflow.app.domain.usecase.steps

import com.vitaflow.app.common.Resource
import com.vitaflow.app.domain.repository.StepCounterRepository
import javax.inject.Inject

class UpdateTargetStepsUseCase @Inject constructor(
    private val repository: StepCounterRepository
) {
    suspend operator fun invoke(targetSteps: Int): Resource<Boolean> {
        if (targetSteps <= 0) {
            return Resource.Error("Target steps must be greater than 0")
        }
        return repository.updateTargetSteps(targetSteps)
    }
}