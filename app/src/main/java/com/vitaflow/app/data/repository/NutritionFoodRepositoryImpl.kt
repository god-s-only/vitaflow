package com.vitaflow.app.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.vitaflow.app.data.local.NutritionDao
import com.vitaflow.app.data.local.NutritionPreferences
import com.vitaflow.app.data.mappers.recipe.toDomain
import com.vitaflow.app.data.remote.SpoonacularAPI
import com.vitaflow.app.data.remote.dto.NutritionFoodDetailDTO
import com.vitaflow.app.domain.models.DailyNutrition
import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.models.FoodEntry
import com.vitaflow.app.domain.models.NutritionFood
import com.vitaflow.app.domain.models.RecipeModel
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import com.vitaflow.app.data.mappers.recipe.toDomainList
import com.vitaflow.app.domain.models.RecipeDetail
import com.vitaflow.app.presentation.ui.features.capture.FoodAnalysisResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

class NutritionFoodRepositoryImpl @Inject constructor(
    private val spoonacularAPI: SpoonacularAPI,
    private val dao: NutritionDao,
    private val nutritionPreferences: NutritionPreferences
) : NutritionFoodRepository {

    override suspend fun searchFoodProducts(
        query: String,
        apiKey: String
    ): Flow<Result<List<NutritionFood>>> = flow {
        try {
            val res = spoonacularAPI.searchFoodProducts(query = query, apiKey = apiKey)
            if (res.isSuccessful) {
                res.body()?.let { data ->
                    val nutritionFood = data.nutritionFoodResponses.map {
                        NutritionFood(
                            id = it.id,
                            title = it.title
                        )
                    }
                    emit(Result.success(nutritionFood))
                } ?: emit(Result.failure(Exception("Error: Body is null")))
            } else {
                emit(Result.failure(Exception("API Error ${res.code()}: ${res.errorBody()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getFoodDetail(foodId: Int, apiKey: String): Flow<Result<NutritionFood>> =
        flow {
            try {
                val res = spoonacularAPI.getFoodProductById(foodId, apiKey)
                if (res.isSuccessful) {
                    res.body()?.let { data ->
                        val nutritionFood = mapDetailDtoToNutritionFood(data)
                        emit(Result.success(nutritionFood))
                    } ?: emit(Result.failure(Exception("Error: Body is null")))
                } else {
                    emit(Result.failure(Exception("API Error ${res.code()} ${res.errorBody()}")))
                }
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)


    override suspend fun insertNutrition(dailyNutrition: DailyNutrition) {
        dao.insertNutrition(dailyNutrition)
    }

    override suspend fun deleteNutrition(dailyNutrition: DailyNutrition) {
        dao.deleteNutrition(dailyNutrition)
    }

    override suspend fun insertFoodEntry(foodEntry: FoodEntry) {
        dao.insertFoodEntry(foodEntry)
    }

    override suspend fun insertFood(food: Food): Long {
        return dao.insertFood(food)
    }

    override suspend fun deleteFood(food: Food) {
        dao.deleteFood(food)
    }

    override suspend fun removeFoodEntry(entryId: Long) {
        dao.deleteFoodEntryById(entryId)
    }

    override suspend fun getFoodEntriesForDate(date: String): Flow<List<FoodEntry>> {
        return dao.getFoodEntriesForDate(date)
    }

    override suspend fun getFoodEntriesForMealType(
        date: String,
        mealType: String
    ): Flow<List<FoodEntry>> {
        return dao.getFoodEntriesForMealType(date, mealType)
    }

    override suspend fun getFoodById(foodId: String): Food? {
        return dao.getFoodById(foodId)
    }

    override suspend fun getRecentFoods(limit: Int): Flow<List<Food>> {
        return dao.getRecentFoods(limit)
    }

    override suspend fun searchFoods(query: String): Flow<List<Food>> {
        return dao.searchFoods(query)
    }

    override suspend fun getDailyNutrition(date: String): Flow<DailyNutrition?> {
        return dao.getDailyNutrition(date)
    }

    override suspend fun updateDailyNutrition(dailyNutrition: DailyNutrition) {
        dao.insertNutrition(dailyNutrition)
    }

    override suspend fun calculateAndSaveDailyNutrition(date: String) {
        val totals = dao.calculateDailyTotals(date)
        if (totals != null) {
            val existingNutrition = dao.getDailyNutritionSync(date)
            val existingWater = existingNutrition?.water ?: 0.0

            val dailyNutrition = DailyNutrition(
                name = "Daily Summary",
                date = date,
                calories = totals.totalCalories,
                carbs = totals.totalCarbs,
                protein = totals.totalProtein,
                fat = totals.totalFat,
                water = existingWater
            )
            dao.insertNutrition(dailyNutrition)

            android.util.Log.d(
                "Repository",
                "Saved DailyNutrition for $date: calories=${dailyNutrition.calories}"
            )
        }
    }

    override suspend fun updateWaterIntake(date: String, amount: Double) {
        // Use first() to get single value from Flow
        val existing = dao.getDailyNutrition(date).first()

        if (existing != null) {
            dao.updateWaterIntake(date, amount)
        } else {
            val dailyNutrition = DailyNutrition(
                name = "Daily Summary",
                date = date,
                calories = 0.0,
                carbs = 0.0,
                protein = 0.0,
                fat = 0.0,
                water = amount
            )
            dao.insertNutrition(dailyNutrition)
        }
    }

    override suspend fun getCalorieTarget(): Int {
        return nutritionPreferences.getCalorieTarget()
    }

    override suspend fun setCalorieTarget(target: Int) {
        nutritionPreferences.setCalorieTarget(target)
    }

    override suspend fun getMacroTargets(): Triple<Int, Int, Int> {
        return nutritionPreferences.getMacroTargets()
    }

    override suspend fun setMacroTargets(carbs: Int, protein: Int, fat: Int) {
        nutritionPreferences.setMacroTargets(carbs, protein, fat)
    }

    override suspend fun getWaterTarget(): Int {
        return nutritionPreferences.getWaterTarget()
    }

    override suspend fun setWaterTarget(target: Int) {
        nutritionPreferences.setWaterTarget(target)
    }

    override suspend fun getFoodProductByUPC(
        upc: String,
        apiKey: String
    ): Flow<Result<NutritionFood>> = flow {
        try {
            val res = spoonacularAPI.getFoodProductByUPC(upc, apiKey)
            if (res.isSuccessful) {
                res.body()?.let { data ->
                    emit(Result.success(mapDetailDtoToNutritionFood(data)))
                } ?: emit(Result.failure(Exception("Error body is null")))
            } else {
                emit(Result.failure(Exception("API Error ${res.code()}: ${res.errorBody()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)


    override suspend fun searchRecipes(query: String, apiKey: String): Flow<List<RecipeModel>> =
        flow {
            val response = spoonacularAPI.searchRecipes(query = query, apiKey = apiKey)
            if (!response.isSuccessful) throw Exception(
                "API Error ${response.code()}: ${
                    response.errorBody()?.string()
                }"
            )
            val body = response.body() ?: throw Exception("Response body is null")
            emit(body.toDomainList())
        }.flowOn(Dispatchers.IO)


    override suspend fun getRecipesDetail(
        recipeId: Int,
        apiKey: String
    ): Flow<RecipeDetail> = flow {
        val res = spoonacularAPI.getRecipeById(recipeId = recipeId, apiKey = apiKey)
        if (!res.isSuccessful) throw Exception(
            "API Error ${res.code()}: ${
                res.errorBody()?.string()
            }"
        )
        val body = res.body() ?: throw Exception("Response body is null")
        emit(body.toDomain())
    }.flowOn(Dispatchers.IO)

    override suspend fun getDailyNutritionSync(date: String): DailyNutrition? {
        return dao.getDailyNutritionSync(date)
    }

    override suspend fun analyzeFoodImage(bitmap: Bitmap): Result<FoodAnalysisResult> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("AnalyzeFoodImage", "Starting food image analysis with ML Kit")
                val analysisResult = analyzeFoodWithMLKit(bitmap)
                Log.d("AnalyzeFoodImage", "Analysis completed: ${analysisResult.foodName}")
                Result.success(analysisResult)
            } catch (e: Exception) {
                Log.e("AnalyzeFoodImage", "Error analyzing food image", e)
                Result.failure(e)
            }
        }
    }

    private suspend fun analyzeFoodWithMLKit(bitmap: Bitmap): FoodAnalysisResult =
        suspendCancellableCoroutine { continuation ->
            val image = InputImage.fromBitmap(bitmap, 0)
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

            labeler.process(image)
                .addOnSuccessListener { labels ->
                    // Get the top label with highest confidence
                    val topLabel = labels.maxByOrNull { it.confidence }

                    if (topLabel != null && topLabel.confidence > 0.5) {
                        val foodName = topLabel.text
                        val confidence = topLabel.confidence

                        // Map common food labels to estimated nutrition data
                        val nutritionData = getEstimatedNutrition(foodName)

                        val result = FoodAnalysisResult(
                            foodName = foodName,
                            calories = nutritionData.calories,
                            carbs = nutritionData.carbs,
                            protein = nutritionData.protein,
                            fat = nutritionData.fat,
                            caloriesPer100g = nutritionData.caloriesPer100g,
                            carbsPer100g = nutritionData.carbsPer100g,
                            proteinPer100g = nutritionData.proteinPer100g,
                            fatPer100g = nutritionData.fatPer100g,
                            confidence = confidence
                        )
                        continuation.resume(result)
                    } else {
                        // Low confidence - return unknown with default values
                        continuation.resume(
                            FoodAnalysisResult(
                                foodName = "Unknown Food",
                                calories = 200,
                                carbs = 30,
                                protein = 10,
                                fat = 8,
                                caloriesPer100g = 200.0,
                                carbsPer100g = 30.0,
                                proteinPer100g = 10.0,
                                fatPer100g = 8.0,
                                confidence = topLabel?.confidence ?: 0f
                            )
                        )
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("AnalyzeFoodImage", "ML Kit labeling failed", e)
                    continuation.resume(
                        FoodAnalysisResult(
                            foodName = "Unknown Food",
                            calories = 200,
                            carbs = 30,
                            protein = 10,
                            fat = 8,
                            caloriesPer100g = 200.0,
                            carbsPer100g = 30.0,
                            proteinPer100g = 10.0,
                            fatPer100g = 8.0,
                            confidence = 0.5f
                        )
                    )
                }
        }

    private data class EstimatedNutrition(
        val calories: Int,
        val carbs: Int,
        val protein: Int,
        val fat: Int,
        val caloriesPer100g: Double,
        val carbsPer100g: Double,
        val proteinPer100g: Double,
        val fatPer100g: Double
    )

    private fun getEstimatedNutrition(foodName: String): EstimatedNutrition {
        val lowerFoodName = foodName.lowercase()

        return when {
            // Fruits
            lowerFoodName.contains("apple") -> EstimatedNutrition(52, 14, 0, 0, 52.0, 14.0, 0.3, 0.2)
            lowerFoodName.contains("banana") -> EstimatedNutrition(89, 23, 1, 0, 89.0, 22.8, 1.1, 0.3)
            lowerFoodName.contains("orange") -> EstimatedNutrition(47, 12, 1, 0, 47.0, 11.8, 0.9, 0.1)
            lowerFoodName.contains("grape") -> EstimatedNutrition(69, 18, 1, 0, 69.0, 18.1, 0.7, 0.2)
            lowerFoodName.contains("strawberry") -> EstimatedNutrition(32, 8, 1, 0, 32.0, 7.7, 0.7, 0.3)
            lowerFoodName.contains("blueberry") -> EstimatedNutrition(57, 14, 1, 0, 57.0, 14.5, 0.7, 0.3)
            lowerFoodName.contains("watermelon") -> EstimatedNutrition(30, 8, 1, 0, 30.0, 7.6, 0.6, 0.2)
            lowerFoodName.contains("pineapple") -> EstimatedNutrition(50, 13, 1, 0, 50.0, 13.1, 0.5, 0.1)
            lowerFoodName.contains("mango") -> EstimatedNutrition(60, 15, 1, 0, 60.0, 14.8, 0.8, 0.4)
            lowerFoodName.contains("peach") -> EstimatedNutrition(39, 10, 1, 0, 39.0, 9.5, 0.9, 0.3)

            // Vegetables
            lowerFoodName.contains("broccoli") -> EstimatedNutrition(34, 7, 3, 0, 34.0, 6.6, 2.8, 0.4)
            lowerFoodName.contains("carrot") -> EstimatedNutrition(41, 10, 1, 0, 41.0, 9.6, 0.9, 0.2)
            lowerFoodName.contains("spinach") -> EstimatedNutrition(23, 4, 3, 0, 23.0, 3.6, 2.9, 0.4)
            lowerFoodName.contains("lettuce") -> EstimatedNutrition(15, 3, 1, 0, 15.0, 2.9, 1.4, 0.2)
            lowerFoodName.contains("tomato") -> EstimatedNutrition(18, 4, 1, 0, 18.0, 3.9, 0.9, 0.2)
            lowerFoodName.contains("cucumber") -> EstimatedNutrition(16, 4, 1, 0, 16.0, 3.6, 0.7, 0.1)
            lowerFoodName.contains("pepper") -> EstimatedNutrition(31, 6, 1, 0, 31.0, 6.0, 1.0, 0.3)
            lowerFoodName.contains("onion") -> EstimatedNutrition(40, 9, 1, 0, 40.0, 9.3, 1.1, 0.1)
            lowerFoodName.contains("potato") -> EstimatedNutrition(77, 17, 2, 0, 77.0, 17.5, 2.0, 0.1)
            lowerFoodName.contains("sweet potato") -> EstimatedNutrition(86, 20, 2, 0, 86.0, 20.1, 1.6, 0.1)

            // Proteins
            lowerFoodName.contains("chicken") -> EstimatedNutrition(165, 0, 31, 3, 165.0, 0.0, 31.0, 3.6)
            lowerFoodName.contains("beef") -> EstimatedNutrition(250, 0, 26, 15, 250.0, 0.0, 26.0, 15.0)
            lowerFoodName.contains("pork") -> EstimatedNutrition(242, 0, 27, 14, 242.0, 0.0, 27.0, 14.0)
            lowerFoodName.contains("fish") || lowerFoodName.contains("salmon") -> EstimatedNutrition(208, 0, 20, 13, 208.0, 0.0, 20.0, 13.0)
            lowerFoodName.contains("tuna") -> EstimatedNutrition(132, 0, 28, 1, 132.0, 0.0, 28.0, 1.0)
            lowerFoodName.contains("shrimp") -> EstimatedNutrition(99, 0, 24, 0, 99.0, 0.2, 24.0, 0.3)
            lowerFoodName.contains("egg") -> EstimatedNutrition(155, 1, 13, 11, 155.0, 1.1, 13.0, 11.0)
            lowerFoodName.contains("tofu") -> EstimatedNutrition(76, 2, 8, 5, 76.0, 1.9, 8.1, 4.8)

            // Grains & Starches
            lowerFoodName.contains("rice") -> EstimatedNutrition(130, 28, 3, 0, 130.0, 28.2, 2.7, 0.3)
            lowerFoodName.contains("pasta") || lowerFoodName.contains("noodle") -> EstimatedNutrition(131, 25, 5, 1, 131.0, 25.0, 5.0, 1.1)
            lowerFoodName.contains("bread") -> EstimatedNutrition(265, 49, 9, 3, 265.0, 49.0, 9.0, 3.2)
            lowerFoodName.contains("oats") || lowerFoodName.contains("oatmeal") -> EstimatedNutrition(389, 66, 17, 7, 389.0, 66.3, 16.9, 6.9)
            lowerFoodName.contains("cereal") -> EstimatedNutrition(379, 83, 8, 3, 379.0, 83.0, 8.0, 3.0)
            lowerFoodName.contains("quinoa") -> EstimatedNutrition(120, 21, 4, 2, 120.0, 21.3, 4.4, 1.9)

            // Dairy
            lowerFoodName.contains("milk") -> EstimatedNutrition(65, 5, 3, 4, 65.0, 4.8, 3.4, 3.6)
            lowerFoodName.contains("cheese") -> EstimatedNutrition(402, 1, 25, 33, 402.0, 1.3, 25.0, 33.0)
            lowerFoodName.contains("yogurt") -> EstimatedNutrition(59, 4, 10, 0, 59.0, 3.6, 10.0, 0.4)
            lowerFoodName.contains("butter") -> EstimatedNutrition(717, 1, 1, 81, 717.0, 0.1, 0.9, 81.1)
            lowerFoodName.contains("ice cream") -> EstimatedNutrition(207, 24, 4, 11, 207.0, 23.6, 3.5, 11.0)

            // Fast Food & Prepared Foods
            lowerFoodName.contains("pizza") -> EstimatedNutrition(266, 33, 11, 10, 266.0, 33.0, 11.0, 10.0)
            lowerFoodName.contains("burger") || lowerFoodName.contains("hamburger") -> EstimatedNutrition(295, 30, 17, 14, 295.0, 30.0, 17.0, 14.0)
            lowerFoodName.contains("sandwich") -> EstimatedNutrition(250, 28, 12, 10, 250.0, 28.0, 12.0, 10.0)
            lowerFoodName.contains("fries") || lowerFoodName.contains("french fry") -> EstimatedNutrition(312, 41, 3, 15, 312.0, 41.0, 3.4, 15.0)
            lowerFoodName.contains("hot dog") -> EstimatedNutrition(290, 2, 10, 26, 290.0, 2.2, 10.0, 26.0)
            lowerFoodName.contains("taco") -> EstimatedNutrition(226, 20, 9, 13, 226.0, 19.9, 8.9, 13.0)
            lowerFoodName.contains("burrito") -> EstimatedNutrition(290, 32, 12, 13, 290.0, 32.0, 12.0, 13.0)

            // Snacks & Sweets
            lowerFoodName.contains("chocolate") -> EstimatedNutrition(546, 61, 5, 31, 546.0, 61.0, 4.9, 31.0)
            lowerFoodName.contains("cookie") -> EstimatedNutrition(502, 64, 7, 25, 502.0, 64.0, 7.0, 25.0)
            lowerFoodName.contains("cake") -> EstimatedNutrition(371, 53, 3, 15, 371.0, 53.0, 3.5, 15.0)
            lowerFoodName.contains("donut") || lowerFoodName.contains("doughnut") -> EstimatedNutrition(452, 51, 4, 25, 452.0, 51.0, 4.0, 25.0)
            lowerFoodName.contains("candy") -> EstimatedNutrition(400, 90, 0, 5, 400.0, 90.0, 0.0, 5.0)
            lowerFoodName.contains("chip") || lowerFoodName.contains("crisp") -> EstimatedNutrition(536, 53, 7, 35, 536.0, 53.0, 7.0, 35.0)
            lowerFoodName.contains("popcorn") -> EstimatedNutrition(387, 78, 13, 4, 387.0, 78.0, 12.9, 4.5)

            // Beverages
            lowerFoodName.contains("coffee") -> EstimatedNutrition(2, 0, 0, 0, 2.0, 0.0, 0.3, 0.0)
            lowerFoodName.contains("tea") -> EstimatedNutrition(1, 0, 0, 0, 1.0, 0.0, 0.1, 0.0)
            lowerFoodName.contains("juice") -> EstimatedNutrition(45, 11, 1, 0, 45.0, 10.4, 0.7, 0.2)
            lowerFoodName.contains("soda") || lowerFoodName.contains("soft drink") -> EstimatedNutrition(42, 11, 0, 0, 42.0, 10.6, 0.0, 0.0)
            lowerFoodName.contains("beer") -> EstimatedNutrition(43, 3, 0, 0, 43.0, 3.6, 0.5, 0.0)
            lowerFoodName.contains("wine") -> EstimatedNutrition(82, 3, 0, 0, 82.0, 2.6, 0.1, 0.0)

            // Soups & Salads
            lowerFoodName.contains("soup") -> EstimatedNutrition(75, 10, 3, 3, 75.0, 9.9, 3.0, 2.9)
            lowerFoodName.contains("salad") -> EstimatedNutrition(33, 5, 2, 1, 33.0, 5.0, 2.1, 0.5)

            // Nuts & Seeds
            lowerFoodName.contains("almond") -> EstimatedNutrition(579, 22, 21, 50, 579.0, 21.6, 21.2, 49.9)
            lowerFoodName.contains("peanut") -> EstimatedNutrition(567, 16, 26, 49, 567.0, 16.1, 25.8, 49.2)
            lowerFoodName.contains("walnut") -> EstimatedNutrition(654, 14, 15, 65, 654.0, 13.7, 15.2, 65.2)
            lowerFoodName.contains("cashew") -> EstimatedNutrition(553, 30, 18, 44, 553.0, 30.2, 18.2, 43.9)

            // Default fallback
            else -> EstimatedNutrition(200, 30, 10, 8, 200.0, 30.0, 10.0, 8.0)
        }
    }

    private fun mapDetailDtoToNutritionFood(dto: NutritionFoodDetailDTO): NutritionFood {
        val nutrients = dto.nutrition?.nutrients ?: emptyList()
        return NutritionFood(
            id = dto.id,
            title = dto.title,
            calories = nutrients.find { it.name == "Calories" }?.amount,
            carbs = nutrients.find { it.name == "Carbs" }?.amount,
            protein = nutrients.find { it.name == "Protein" }?.amount,
            fat = nutrients.find { it.name == "Fat" }?.amount,
        )
    }
}

fun getTodayDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())