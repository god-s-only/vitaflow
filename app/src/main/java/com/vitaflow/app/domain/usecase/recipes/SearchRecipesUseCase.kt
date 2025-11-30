package com.vitaflow.app.domain.usecase.recipes

import com.vitaflow.app.domain.models.RecipeModel
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchRecipesUseCase @Inject constructor(private val repository: NutritionFoodRepository) {
    suspend operator fun invoke(query: String, apiKey: String): Flow<List<RecipeModel>>{
        return repository.searchRecipes(query, apiKey)
    }
}