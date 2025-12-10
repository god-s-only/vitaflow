package com.vitaflow.app.domain.usecase.steps

import com.vitaflow.app.domain.repository.StepCounterRepository
import javax.inject.Inject

class GetStepsTarget @Inject constructor(private val stepsRepository: StepCounterRepository) {
    suspend operator fun invoke() = stepsRepository.getStepsTarget()
}