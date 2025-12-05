package com.vitaflow.app.domain.usecase.steps

import com.vitaflow.app.common.Resource
import com.vitaflow.app.domain.models.StepsData
import com.vitaflow.app.domain.repository.StepCounterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayStepsUseCase @Inject constructor(
    private val repository: StepCounterRepository
) {
    operator fun invoke(): Flow<Resource<StepsData>> {
        return repository.getTodaySteps()
    }
}