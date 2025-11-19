package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.models.DailyNutrition
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import javax.inject.Inject

class AddDailyNutritionUseCase @Inject constructor(private val nutritionFoodRepository: NutritionFoodRepository) {
    suspend operator fun invoke(dailyNutrition: DailyNutrition){
        nutritionFoodRepository.insertNutrition(dailyNutrition)
    }
}