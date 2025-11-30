package com.vitaflow.app.presentation.ui.features.recipes

sealed class RecipesEvent {
    data class OnQueryChange(val query: String): RecipesEvent()
    data class SearchRecipes(val query: String): RecipesEvent()
}