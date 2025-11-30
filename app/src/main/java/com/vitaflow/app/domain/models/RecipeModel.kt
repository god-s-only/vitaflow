package com.vitaflow.app.domain.models

data class RecipeModel(
    val id: Int,
    val title: String,
    val image: String,
    val fat: Double,
    val calories: Double = 420.0,
    val protein: Double = 18.5,
    val carbs: Double = 45.2
)
