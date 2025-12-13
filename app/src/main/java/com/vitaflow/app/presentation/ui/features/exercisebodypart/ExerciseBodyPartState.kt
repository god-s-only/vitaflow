package com.vitaflow.app.presentation.ui.features.exercisebodypart

import com.vitaflow.app.domain.models.Exercise

data class ExerciseBodyPartState(
    val isLoading: Boolean = false,
    val data: List<Exercise> = emptyList(),
    val error: String? = null
)