package com.vitaflow.app.data.repository

import androidx.datastore.dataStore
import com.vitaflow.app.data.local.NutritionDao
import com.vitaflow.app.data.remote.SpoonacularAPI
import com.vitaflow.app.data.remote.dto.NutritionFoodDetailDTO
import com.vitaflow.app.domain.models.DailyNutrition
import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.models.FoodEntry
import com.vitaflow.app.domain.models.NutritionFood
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class NutritionFoodRepositoryImpl @Inject constructor(private val spoonacularAPI: SpoonacularAPI, private val dao: NutritionDao): NutritionFoodRepository {
    override suspend fun searchFoodProducts(
        query: String,
        apiKey: String
    ): Flow<Result<List<NutritionFood>>> = flow {
        try {
            val res = spoonacularAPI.searchFoodProducts(query = query, apiKey = apiKey)
            if(res.isSuccessful){
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
        }catch (e: Exception){
            emit(Result.failure(e))
        }
    }


    override suspend fun getFoodDetail(foodId: Int, apiKey: String): Flow<Result<NutritionFood>> = flow{
        try{
            val res = spoonacularAPI.getFoodProductById(foodId, apiKey)
            if(res.isSuccessful){
                res.body()?.let { data ->
                    val nutritionFood = mapDetailDtoToNutritionFood(data)
                    emit(Result.success(nutritionFood))
                } ?: emit(Result.failure(Exception("Error: Body is null")))
            }else{
                emit(Result.failure(Exception("API Error ${res.code()} ${res.errorBody()}")))
            }
        }catch (e: Exception){
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
        dao.calculateDailyTotals(date)
    }

    override suspend fun updateWaterIntake(date: String, amount: Double) {
        dao.updateWaterIntake(date, amount)
    }

    override suspend fun getCalorieTarget(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun setCalorieTarget(target: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getMacroTargets(): Triple<Int, Int, Int> {
        TODO("Not yet implemented")
    }

    override suspend fun setMacroTargets(carbs: Int, protein: Int, fat: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getWaterTarget(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun setWaterTarget(target: Int) {
        TODO("Not yet implemented")
    }

    private fun mapDetailDtoToNutritionFood(dto: NutritionFoodDetailDTO): NutritionFood{
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
fun getTodayDate(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}