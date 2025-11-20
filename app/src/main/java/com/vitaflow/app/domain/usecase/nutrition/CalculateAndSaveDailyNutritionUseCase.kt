package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.repository.NutritionFoodRepository
import com.vitaflow.app.domain.repository.getTodayDate
import javax.inject.Inject

class CalculateAndSaveDailyNutritionUseCase @Inject constructor(
    private val repository: NutritionFoodRepository
) {
    suspend operator fun invoke(date: String = getTodayDate()): Result<Unit> {
        return try {
            repository.calculateAndSaveDailyNutrition(date)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}