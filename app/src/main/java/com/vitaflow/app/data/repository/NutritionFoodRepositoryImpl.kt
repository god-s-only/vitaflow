package com.vitaflow.app.data.repository

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
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
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

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

    override suspend fun insertFood(food: Food) {
        dao.insertFood(food)
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
                Log.d("AnalyzeFoodImage", "Starting food image analysis")
                val base64Image = convertBitmapToBase64(bitmap)
                val analysisResult = analyzeFoodWithClaude(base64Image)
                Log.d("AnalyzeFoodImage", "Analysis completed: ${analysisResult.foodName}")
                Result.success(analysisResult)
            } catch (e: Exception) {
                Log.e("AnalyzeFoodImage", "Error analyzing food image", e)
                Result.failure(e)
            }
        }
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val maxSize = 800
        val ratio = maxSize.toFloat() / maxOf(bitmap.width, bitmap.height)
        val resizedBitmap = Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * ratio).toInt(),
            (bitmap.height * ratio).toInt(),
            true
        )

        val outputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val byteArray = outputStream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private fun analyzeFoodWithClaude(base64Image: String): FoodAnalysisResult {
        val url = URL("https://api.anthropic.com/v1/messages")
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true
            val requestBody = JSONObject().apply {
                put("model", "claude-sonnet-4-20250514")
                put("max_tokens", 1000)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", JSONArray().apply {
                            put(JSONObject().apply {
                                put("type", "image")
                                put("source", JSONObject().apply {
                                    put("type", "base64")
                                    put("media_type", "image/jpeg")
                                    put("data", base64Image)
                                })
                            })
                            put(JSONObject().apply {
                                put("type", "text")
                                put(
                                    "text", """
                                    Analyze this food image and provide nutritional information.
                                    
                                    Respond ONLY with a JSON object in this exact format (no markdown, no extra text):
                                    {
                                      "foodName": "name of the food",
                                      "estimatedPortionGrams": 100,
                                      "caloriesPer100g": 250.0,
                                      "carbsPer100g": 45.0,
                                      "proteinPer100g": 8.0,
                                      "fatPer100g": 6.0
                                    }
                                    
                                    Important:
                                    - Identify the main food item in the image
                                    - Provide realistic nutritional values per 100g
                                    - If multiple foods, focus on the primary/main item
                                    - Estimate portion size in grams (default 100g if uncertain)
                                    - Use standard USDA nutritional data as reference
                                """.trimIndent()
                                )
                            })
                        })
                    })
                })
            }

            connection.outputStream.use { os ->
                os.write(requestBody.toString().toByteArray())
            }

            val responseCode = connection.responseCode
            Log.d("AnalyzeFoodImage", "API Response code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                Log.d("AnalyzeFoodImage", "API Response: $response")

                return parseClaudeResponse(response)
            } else {
                val errorResponse = connection.errorStream?.bufferedReader()?.use { it.readText() }
                Log.e("AnalyzeFoodImage", "API Error: $errorResponse")
                throw Exception("API request failed: $responseCode - $errorResponse")
            }
        } finally {
            connection.disconnect()
        }
    }

    private fun parseClaudeResponse(response: String): FoodAnalysisResult {
        try {
            val jsonResponse = JSONObject(response)
            val content = jsonResponse.getJSONArray("content")
            val textContent = content.getJSONObject(0).getString("text")
            val cleanJson = textContent
                .replace("```json", "")
                .replace("```", "")
                .trim()

            Log.d("AnalyzeFoodImage", "Parsed JSON: $cleanJson")

            val nutritionData = JSONObject(cleanJson)

            val portionGrams = nutritionData.optDouble("estimatedPortionGrams", 100.0)
            val caloriesPer100g = nutritionData.getDouble("caloriesPer100g")
            val carbsPer100g = nutritionData.optDouble("carbsPer100g", 0.0)
            val proteinPer100g = nutritionData.getDouble("proteinPer100g")
            val fatPer100g = nutritionData.getDouble("fatPer100g")
            val multiplier = portionGrams / 100.0

            return FoodAnalysisResult(
                foodName = nutritionData.getString("foodName"),
                calories = (caloriesPer100g * multiplier).toInt(),
                carbs = (carbsPer100g * multiplier).toInt(),
                protein = (proteinPer100g * multiplier).toInt(),
                fat = (fatPer100g * multiplier).toInt(),
                caloriesPer100g = caloriesPer100g,
                carbsPer100g = carbsPer100g,
                proteinPer100g = proteinPer100g,
                fatPer100g = fatPer100g
            )
        } catch (e: Exception) {
            Log.e("AnalyzeFoodImage", "Error parsing Claude response", e)
            return FoodAnalysisResult(
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
        }
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

fun getTodayDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())