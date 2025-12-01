package com.vitaflow.app.domain.models

import com.vitaflow.app.presentation.ui.features.recipedetail.Ingredient

data class RecipeDetail(
    val id: Int,
    val title: String,
    val image: String,
    val readyInMinutes: Int,
    val servings: Int,
    val sourceUrl: String,
    val preparationMinutes: Int,
    val cookingMinutes: Int,
    val aggregateLikes: Int,
    val healthScore: Int,
    val sourceName: String,
    val pricePerServing: Double,
    val extendedIngredients: List<Ingredient>,
    val summary: String,
    val dishTypes: List<String>,
    val spoonacularScore: Double,
    val calories: Int = 543,
    val protein: Int = 17,
    val fat: Int = 16,
    val carbs: Int = 65
)
