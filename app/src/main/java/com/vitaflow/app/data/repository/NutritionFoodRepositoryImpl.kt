package com.vitaflow.app.data.repository

import com.vitaflow.app.data.local.NutritionDao
import com.vitaflow.app.data.local.NutritionPreferences
import com.vitaflow.app.data.remote.SpoonacularAPI
import com.vitaflow.app.data.remote.dto.NutritionFoodDetailDTO
import com.vitaflow.app.domain.models.DailyNutrition
import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.models.FoodEntry
import com.vitaflow.app.domain.models.NutritionFood
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
    }

    override suspend fun getFoodDetail(foodId: Int, apiKey: String): Flow<Result<NutritionFood>> = flow {
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
    }

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
            var existingWater = 0.0
            dao.getDailyNutrition(date).collect { existing ->
                existingWater = existing?.water ?: 0.0
            }

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
        }
    }

    override suspend fun updateWaterIntake(date: String, amount: Double) {
        var exists = false
        dao.getDailyNutrition(date).collect { daily ->
            exists = daily != null
        }

        if (exists) {
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