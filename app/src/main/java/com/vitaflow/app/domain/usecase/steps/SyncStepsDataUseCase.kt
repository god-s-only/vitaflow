package com.vitaflow.app.domain.usecase.steps

import com.vitaflow.app.common.Resource
import com.vitaflow.app.domain.repository.StepCounterRepository
import javax.inject.Inject

class SyncStepsDataUseCase @Inject constructor(
    private val repository: StepCounterRepository
) {
    suspend operator fun invoke(): Resource<Boolean> {
        return repository.syncStepsData()
    }
}