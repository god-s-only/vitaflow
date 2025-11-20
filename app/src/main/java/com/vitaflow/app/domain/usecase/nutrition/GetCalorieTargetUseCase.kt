package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.repository.NutritionFoodRepository
import javax.inject.Inject

class GetCalorieTargetUseCase @Inject constructor(
    private val repository: NutritionFoodRepository
) {
    suspend operator fun invoke(): Int {
        return repository.getCalorieTarget()
    }
}