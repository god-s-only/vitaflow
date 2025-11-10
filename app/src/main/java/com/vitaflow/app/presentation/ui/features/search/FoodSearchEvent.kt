package com.vitaflow.app.presentation.ui.features.search

sealed class FoodSearchEvent{
    data class OnSearchChange(val query: String): FoodSearchEvent()
    data class LoadNutritionDetails(val productId: Int): FoodSearchEvent()
}