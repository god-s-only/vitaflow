package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.models.FoodEntry
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import com.vitaflow.app.domain.repository.getTodayDate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFoodEntriesForDateUseCase @Inject constructor(
    private val repository: NutritionFoodRepository
) {
    suspend operator fun invoke(date: String = getTodayDate()): Flow<List<FoodEntry>> {
        return repository.getFoodEntriesForDate(date)
    }
}