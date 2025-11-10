package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.repository.NutritionFoodRepository
import javax.inject.Inject

class GetNutritionFoodById @Inject constructor(private val respository: NutritionFoodRepository) {
    suspend operator fun invoke(id: Int, apiKey: String) = respository.getFoodDetail(id, apiKey)
}