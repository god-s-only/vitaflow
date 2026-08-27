package com.vitaflow.app.domain.models

data class FoodAnalysisResult(
    val foodName: String,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int,
    val caloriesPer100g: Double,
    val carbsPer100g: Double?,
    val proteinPer100g: Double,
    val fatPer100g: Double,
    val confidence: Float = 0.85f
)