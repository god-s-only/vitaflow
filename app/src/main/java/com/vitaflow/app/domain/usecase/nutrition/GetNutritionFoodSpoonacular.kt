package com.vitaflow.app.domain.usecase.nutrition

import com.vitaflow.app.domain.models.NutritionFood
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNutritionFoodSpoonacular @Inject constructor(private val repository: NutritionFoodRepository) {

    suspend operator fun invoke(query: String, apiKey: String): Flow<Result<List<NutritionFood>>> = repository.searchFoodProducts(query, apiKey)
}