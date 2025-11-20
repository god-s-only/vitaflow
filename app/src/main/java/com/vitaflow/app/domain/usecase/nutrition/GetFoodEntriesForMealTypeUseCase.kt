package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.models.FoodEntry
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import com.vitaflow.app.domain.repository.getTodayDate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFoodEntriesForMealTypeUseCase @Inject constructor(
    private val repository: NutritionFoodRepository
) {
    suspend operator fun invoke(
        mealType: String,
        date: String = getTodayDate()
    ): Flow<List<FoodEntry>> {
        return repository.getFoodEntriesForMealType(date, mealType)
    }
}