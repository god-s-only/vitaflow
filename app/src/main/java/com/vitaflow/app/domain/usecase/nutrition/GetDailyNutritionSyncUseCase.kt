package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.models.DailyNutrition
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import com.vitaflow.app.domain.repository.getTodayDate
import javax.inject.Inject

class GetDailyNutritionSyncUseCase @Inject constructor(
    private val repository: NutritionFoodRepository
) {
    suspend operator fun invoke(date: String = getTodayDate()): DailyNutrition? {
        return repository.getDailyNutritionSync(date)
    }
}