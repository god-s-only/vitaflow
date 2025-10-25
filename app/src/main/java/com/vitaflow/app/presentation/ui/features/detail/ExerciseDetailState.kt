package com.vitaflow.app.presentation.ui.features.detail

import com.vitaflow.app.domain.models.Exercise

data class ExerciseDetailState(
    val isLoading: Boolean = false,
    val exercise: Exercise? = null,
    val error: String = ""
)