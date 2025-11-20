package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.repository.NutritionFoodRepository
import javax.inject.Inject

class SetWaterTargetUseCase @Inject constructor(
    private val repository: NutritionFoodRepository
) {
    suspend operator fun invoke(target: Int): Result<Unit> {
        return try {
            if (target < 500) {
                return Result.failure(IllegalArgumentException("Water target must be at least 500ml"))
            }

            repository.setWaterTarget(target)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}