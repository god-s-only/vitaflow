package com.vitaflow.app.presentation.ui.features.search

import com.vitaflow.app.domain.models.DailyNutrition

sealed class FoodSearchEvent{
    data class OnSearchChange(val query: String): FoodSearchEvent()
    data class LoadNutritionDetails(val productId: Int): FoodSearchEvent()
    data class OnAddNutrition(val name: String, val calories: Double, val carbs: Double, val protein: Double, val fat: Double): FoodSearchEvent()
}