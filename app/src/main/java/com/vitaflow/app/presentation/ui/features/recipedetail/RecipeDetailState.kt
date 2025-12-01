package com.vitaflow.app.presentation.ui.features.recipedetail

import com.vitaflow.app.domain.models.RecipeDetail

data class RecipeDetailState(
    val isLoading: Boolean = false,
    val data: RecipeDetail? = null,
    val error: String? = null
)
