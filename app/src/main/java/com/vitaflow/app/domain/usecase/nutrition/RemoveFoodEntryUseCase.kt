package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.repository.NutritionFoodRepository
import javax.inject.Inject

class RemoveFoodEntryUseCase @Inject constructor(
    private val repository: NutritionFoodRepository
) {
    suspend operator fun invoke(entryId: Long): Result<Unit> {
        return try {
            repository.removeFoodEntry(entryId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}