package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.repository.NutritionFoodRepository
import com.vitaflow.app.domain.repository.getTodayDate
import javax.inject.Inject

class UpdateWaterIntakeUseCase @Inject constructor(
    private val repository: NutritionFoodRepository
) {
    suspend operator fun invoke(
        amount: Double,
        date: String = getTodayDate()
    ): Result<Unit> {
        return try {
            repository.updateWaterIntake(date, amount)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}