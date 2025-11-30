package com.vitaflow.app.presentation.ui.features.recipes

import com.vitaflow.app.domain.models.RecipeModel

data class RecipesState(
    val isLoading: Boolean = false,
    val query: String = "",
    val recipes: List<RecipeModel> = emptyList(),
    val error: String? = null
)