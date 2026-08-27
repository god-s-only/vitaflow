package com.vitaflow.app.domain.usecase.steps

import com.vitaflow.app.domain.repository.StepCounterRepository
import javax.inject.Inject

class CheckHealthConnectInstalledUseCase @Inject constructor(
    private val repository: StepCounterRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.isHealthConnectInstalled()
    }
}