package com.vitaflow.app.domain.repository

import android.graphics.Bitmap
import com.vitaflow.app.data.remote.dto.NutritionFoodDetailDTO
import com.vitaflow.app.domain.models.DailyNutrition
import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.models.FoodEntry
import com.vitaflow.app.domain.models.NutritionFood
import com.vitaflow.app.domain.models.RecipeDetail
import com.vitaflow.app.domain.models.RecipeModel
import com.vitaflow.app.presentation.ui.features.capture.FoodAnalysisResult
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface NutritionFoodRepository {
    suspend fun searchFoodProducts(query: String, apiKey: String): Flow<Result<List<NutritionFood>>>
    suspend fun getFoodDetail(foodId: Int, apiKey: String): Flow<Result<NutritionFood>>
    suspend fun insertNutrition(dailyNutrition: DailyNutrition)
    suspend fun deleteNutrition(dailyNutrition: DailyNutrition)
    suspend fun insertFoodEntry(foodEntry: FoodEntry)
    suspend fun insertFood(food: Food)
    suspend fun deleteFood(food: Food)


    suspend fun removeFoodEntry(entryId: Long)
    suspend fun getFoodEntriesForDate(date: String): Flow<List<FoodEntry>>
    suspend fun getFoodEntriesForMealType(date: String, mealType: String): Flow<List<FoodEntry>>

    suspend fun getFoodById(foodId: String): Food?
    suspend fun getRecentFoods(limit: Int = 10): Flow<List<Food>>
    suspend fun searchFoods(query: String): Flow<List<Food>>

    suspend fun getDailyNutrition(date: String): Flow<DailyNutrition?>
    suspend fun updateDailyNutrition(dailyNutrition: DailyNutrition)
    suspend fun calculateAndSaveDailyNutrition(date: String)

    suspend fun updateWaterIntake(date: String, amount: Double)

    suspend fun getCalorieTarget(): Int
    suspend fun setCalorieTarget(target: Int)
    suspend fun getMacroTargets(): Triple<Int, Int, Int>
    suspend fun setMacroTargets(carbs: Int, protein: Int, fat: Int)
    suspend fun getWaterTarget(): Int
    suspend fun setWaterTarget(target: Int)

    suspend fun getFoodProductByUPC(upc: String, apiKey: String): Flow<Result<NutritionFood>>
    suspend fun searchRecipes(query: String, apiKey: String): Flow<List<RecipeModel>>
    suspend fun getRecipesDetail(recipeId: Int, apiKey: String): Flow<RecipeDetail>
    suspend fun getDailyNutritionSync(date: String): DailyNutrition?
    suspend fun analyzeFoodImage(bitmap: Bitmap): Result<FoodAnalysisResult>
}

fun getTodayDate(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}