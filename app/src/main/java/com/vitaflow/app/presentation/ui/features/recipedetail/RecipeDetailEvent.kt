package com.vitaflow.app.presentation.ui.features.recipedetail

sealed class RecipeDetailEvent {
    data object OnStartCooking: RecipeDetailEvent()
}