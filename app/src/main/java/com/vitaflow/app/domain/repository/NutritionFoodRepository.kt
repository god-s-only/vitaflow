package com.vitaflow.app.domain.repository

import com.vitaflow.app.domain.models.NutritionFood
import kotlinx.coroutines.flow.Flow

interface NutritionFoodRepository {
    suspend fun searchFoodProducts(query: String, apiKey: String): Flow<Result<List<NutritionFood>>>
}