package com.vitaflow.app.presentation.ui.features.search

import com.vitaflow.app.domain.models.NutritionFood

data class FoodSearchState(
    val food: List<NutritionFood> = emptyList(),
    val error: String? = null,
    val loading: Boolean = false
)
