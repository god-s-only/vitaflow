package com.vitaflow.app.domain.models

data class NutritionFood(
    val id: Int,
    val title: String,
    val calories: Double? = null,
    val carbs: Double? = null,
    val protein: Double? = null,
    val fat: Double? = null,
    val isLoadingDetails: Boolean = false
)
