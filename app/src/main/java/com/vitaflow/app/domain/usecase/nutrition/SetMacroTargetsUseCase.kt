package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.repository.NutritionFoodRepository
import javax.inject.Inject

class SetMacroTargetsUseCase @Inject constructor(
    private val repository: NutritionFoodRepository
) {
    suspend operator fun invoke(
        carbsTarget: Int,
        proteinTarget: Int,
        fatTarget: Int
    ): Result<Unit> {
        return try {
            if (carbsTarget < 0 || proteinTarget < 0 || fatTarget < 0) {
                return Result.failure(IllegalArgumentException("Macro targets cannot be negative"))
            }
            repository.setMacroTargets(carbsTarget, proteinTarget, fatTarget)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}