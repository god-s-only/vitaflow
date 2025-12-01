package com.vitaflow.app.data.mappers

import com.vitaflow.app.data.remote.dto.recipes.RecipesDetailDTO
import com.vitaflow.app.domain.models.RecipeDetail

fun RecipesDetailDTO.toDomain(): RecipeDetail {

    return RecipeDetail(
        id = id,
        title = title,
        image = image,
        readyInMinutes = readyInMinutes,
        servings = servings,
        sourceUrl = sourceUrl,
        preparationMinutes = preparationMinutes,
        cookingMinutes = cookingMinutes,
        aggregateLikes = aggregateLikes,
        healthScore = healthScore,
        sourceName = sourceName,
        pricePerServing = pricePerServing,
        extendedIngredients = extendedIngredients,
        summary = summary,
        dishTypes = dishTypes,
        spoonacularScore = spoonacularScore,
        calories =
    )
}