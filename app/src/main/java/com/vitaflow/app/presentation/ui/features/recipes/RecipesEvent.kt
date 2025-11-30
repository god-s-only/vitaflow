package com.vitaflow.app.presentation.ui.features.recipes

sealed class RecipesEvent {
    data class SearchRecipe(val query: String): RecipesEvent()
}