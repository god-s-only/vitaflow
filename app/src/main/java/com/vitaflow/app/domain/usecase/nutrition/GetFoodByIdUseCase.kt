package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import javax.inject.Inject

class GetFoodByIdUseCase @Inject constructor(
    private val repository: NutritionFoodRepository
) {
    suspend operator fun invoke(foodId: String): Food? {
        return repository.getFoodById(foodId)
    }
}