package com.vitaflow.app.domain.usecase.steps

import com.vitaflow.app.domain.repository.StepCounterRepository
import javax.inject.Inject

class CheckHealthConnectPermissionsUseCase @Inject constructor(
    private val repository: StepCounterRepository
) {
    operator fun invoke() = repository.hasHealthConnectPermissions()
}