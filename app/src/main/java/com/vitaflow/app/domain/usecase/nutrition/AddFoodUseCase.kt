package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import javax.inject.Inject

class AddFoodUseCase @Inject constructor(private val nutritionFoodRepository: NutritionFoodRepository) {
    suspend operator fun invoke(food: Food){
        try {
            nutritionFoodRepository.insertFood(food)
            Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e)
        }
    }
}