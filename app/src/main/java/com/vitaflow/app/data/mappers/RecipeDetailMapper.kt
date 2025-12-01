package com.vitaflow.app.data.mappers

import com.vitaflow.app.data.remote.dto.recipes.recipesdetail.RecipesDetailDTO
import com.vitaflow.app.domain.models.Ingredient
import com.vitaflow.app.domain.models.RecipeDetail

fun RecipesDetailDTO.toDomain(): RecipeDetail {

    fun nutrient(name: String): Int =
        nutrition.nutrients.find{ it.name == name}?.amount?.toInt() ?: 0

    fun convertToIngredients(): List<Ingredient>{
        return extendedIngredients.map { Ingredient(
            id = it.id,
            name = it.name,
            amount = it.amount,
            unit = it.unit,
            original = it.original,
            image = it.image
        ) }
    }

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
        extendedIngredients = convertToIngredients(),
        summary = summary,
        dishTypes = dishTypes,
        spoonacularScore = spoonacularScore,
        calories = nutrient("Calories"),
        protein = nutrient("Protein"),
        fat = nutrient("Fat"),
        carbs = nutrient("Carbs")
    )
}
