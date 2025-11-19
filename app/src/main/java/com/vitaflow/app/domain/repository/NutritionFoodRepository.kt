package com.vitaflow.app.domain.repository

import com.vitaflow.app.data.remote.dto.NutritionFoodDetailDTO
import com.vitaflow.app.domain.models.DailyNutrition
import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.models.FoodEntry
import com.vitaflow.app.domain.models.NutritionFood
import kotlinx.coroutines.flow.Flow

interface NutritionFoodRepository {
    suspend fun searchFoodProducts(query: String, apiKey: String): Flow<Result<List<NutritionFood>>>
    suspend fun getFoodDetail(foodId: Int, apiKey: String): Flow<Result<NutritionFood>>
    suspend fun insertNutrition(dailyNutrition: DailyNutrition)
    suspend fun deleteNutrition(dailyNutrition: DailyNutrition)
    suspend fun insertFoodEntry(foodEntry: FoodEntry)
    suspend fun deleteFoodEntry(foodEntry: FoodEntry)
    suspend fun insertFood(food: Food)
    suspend fun deleteFood(food: Food)
}