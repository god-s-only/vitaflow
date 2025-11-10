package com.vitaflow.app.data.repository

import androidx.datastore.dataStore
import com.vitaflow.app.data.remote.SpoonacularAPI
import com.vitaflow.app.data.remote.dto.NutritionFoodDetailDTO
import com.vitaflow.app.domain.models.NutritionFood
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NutritionFoodRepositoryImpl @Inject constructor(private val spoonacularAPI: SpoonacularAPI): NutritionFoodRepository {
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

    private fun mapDetailDtoToNutritionFood(dto: NutritionFoodDetailDTO): NutritionFood{
        val nutrients = dto.nutrition?.nutrients ?: emptyList()
        return NutritionFood(
            id = dto.id,
            title = dto.title,
            calories = nutrients.find { it.name == "Calories" }?.amount?.toInt(),
            carbs = nutrients.find { it.name == "Carbs" }?.amount,
            protein = nutrients.find { it.name == "Protein" }?.amount,
            fat = nutrients.find { it.name == "Fat" }?.amount,
            servingSize = dto.servings?.size?.toString(),
            servingUnit = dto.servings?.unit
        )

    }
}