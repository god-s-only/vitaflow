package com.vitaflow.app.domain.usecase.recipes

import com.vitaflow.app.domain.models.RecipeDetail
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecipeDetailUseCase @Inject constructor(private val repository: NutritionFoodRepository) {
    suspend operator fun invoke(recipeId: Int, apiKey: String): Flow<RecipeDetail>{
        return repository.getRecipesDetail(recipeId, apiKey)
    }
}