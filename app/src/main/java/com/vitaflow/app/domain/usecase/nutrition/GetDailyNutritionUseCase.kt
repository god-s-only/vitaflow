package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.models.DailyNutrition
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import com.vitaflow.app.domain.repository.getTodayDate
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDailyNutritionUseCase @Inject constructor(
    private val repository: NutritionFoodRepository
) {
    suspend operator fun invoke(date: String = getTodayDate()): Flow<DailyNutrition?> {
        return repository.getDailyNutrition(date)
    }
}
