package com.vitaflow.app.domain.models

data class RecipeModel(
    val id: Int,
    val title: String,
    val image: String,
    val fat: Int,
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0
)
