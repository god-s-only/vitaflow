package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.models.FoodEntry
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import com.vitaflow.app.domain.repository.getTodayDate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddFoodEntryUseCase @Inject constructor(
    private val repository: NutritionFoodRepository
) {
    suspend operator fun invoke(
        food: Food,
        mealType: String,
        quantity: Double,
        date: String = getTodayDate()
    ): Result<Unit> {
        return try {
            val foodEntry = FoodEntry(
                foodId = food.id.toString(),
                mealType = mealType,
                quantity = quantity,
                timestamp = System.currentTimeMillis(),
                date = date
            )

            repository.insertFoodEntry(foodEntry)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}