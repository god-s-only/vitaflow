package com.vitaflow.app.data.mappers

import com.vitaflow.app.data.remote.dto.recipes.recipeslist.RecipeDTO
import com.vitaflow.app.data.remote.dto.recipes.recipeslist.Result
import com.vitaflow.app.domain.models.RecipeModel

fun Result.toDomain(): RecipeModel {

    fun nutrient(name: String): Int =
        nutrition.nutrients.find { it.name == name }?.amount?.toInt() ?: 0

    return RecipeModel(
        id = id,
        title = title,
        image = image,
        fat = nutrient("Fat"),
        calories = nutrient("Calories"),
        carbs = nutrient("Carbs"),
        protein = nutrient("Protein")
    )
}
fun RecipeDTO.toDomainList(): List<RecipeModel> =
    results.map { it.toDomain() }
