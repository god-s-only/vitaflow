package com.vitaflow.app.data.remote

import com.vitaflow.app.data.remote.dto.NutritionFoodDTO
import com.vitaflow.app.data.remote.dto.NutritionFoodDetailDTO
import com.vitaflow.app.data.remote.dto.recipes.recipeslist.RecipeDTO
import com.vitaflow.app.data.remote.dto.recipes.recipesdetail.RecipesDetailDTO
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

    @GET("food/products/upc/{upc}")
    suspend fun getFoodProductByUPC(
        @Path("upc") upc: String,
        @Query("apiKey") apiKey: String
    ): Response<NutritionFoodDetailDTO>

    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("query") query: String,
        @Query("number") number: Int = 10,
        @Query("maxFat") maxFat: Int = 25,
        @Query("maxCarbs") maxCarbs: Int = 100,
        @Query("maxProtein") maxProtein: Int = 100,
        @Query("minCalories") minCalories: Int = 50,
        @Query("apiKey") apiKey: String
    ): Response<RecipeDTO>

    @GET("recipes/{id}/information")
    suspend fun getRecipeById(
        @Path("id") recipeId: Int,
        @Query("includeNutrition") includeNutrition: Boolean = true,
        @Query("apiKey") apiKey: String
    ): Response<RecipesDetailDTO>


}