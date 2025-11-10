package com.vitaflow.app.data.remote

import com.vitaflow.app.data.remote.dto.NutritionFoodDTO
import com.vitaflow.app.data.remote.dto.NutritionFoodDetailDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonacularAPI {
    @GET("food/products/search")
    suspend fun searchFoodProducts(
        @Query("query") query: String,
        @Query("number") number: Int = 10,
        @Query("apiKey") apiKey: String
    ): Response<NutritionFoodDTO>

    @GET("food/products/{id}")
    suspend fun getFoodProductById(
        @Path("id") productId: Int,
        @Query("apiKey") apiKey: String
    ): Response<NutritionFoodDetailDTO>
}