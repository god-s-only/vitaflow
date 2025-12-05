package com.vitaflow.app.domain.usecase.steps

import com.vitaflow.app.data.repository.StepCounterRepositoryImpl
import javax.inject.Inject

class CheckHealthConnectAvailabilityUseCase @Inject constructor(
    private val repository: StepCounterRepositoryImpl
) {
    suspend operator fun invoke(): Boolean {
        return repository.isHealthConnectAvailable()
    }
}