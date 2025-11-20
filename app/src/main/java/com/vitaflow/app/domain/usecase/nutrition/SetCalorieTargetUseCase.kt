package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.repository.NutritionFoodRepository
import javax.inject.Inject

class SetCalorieTargetUseCase @Inject constructor(
    private val repository: NutritionFoodRepository
) {
    suspend operator fun invoke(target: Int): Result<Unit> {
        return try {
            if (target < 1000) {
                return Result.failure(IllegalArgumentException("Calorie target must be at least 1000"))
            }
            repository.setCalorieTarget(target)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}