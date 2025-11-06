package com.vitaflow.app.data.repository

import androidx.datastore.dataStore
import com.vitaflow.app.data.remote.SpoonacularAPI
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
                emit(Result.failure(Exception("Error: ${res.code()} ${res.errorBody()}")))
            }
        }catch (e: Exception){
            emit(Result.failure(Exception(e.message)))
        }
    }
}