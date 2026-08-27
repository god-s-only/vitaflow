package com.vitaflow.app.domain.models

data class DailyNutrition(
    val id: Int = 0,
    val name: String,
    val date: String,
    val calories: Double,
    val carbs: Double,
    val protein: Double,
    val fat: Double,
    val water: Double? = null
)
