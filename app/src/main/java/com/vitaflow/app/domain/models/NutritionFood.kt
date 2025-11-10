package com.vitaflow.app.domain.models

data class NutritionFood(
    val id: Int,
    val title: String,
    val calories: Int? = null,
    val carbs: Double? = null,
    val protein: Double? = null,
    val fat: Double? = null,
    val servingSize: String? = null,
    val servingUnit: String? = null,
    val isLoadingDetails: Boolean = false
)
