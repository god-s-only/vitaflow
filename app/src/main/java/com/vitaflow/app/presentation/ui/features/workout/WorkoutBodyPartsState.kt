package com.vitaflow.app.presentation.ui.features.workout

data class WorkoutBodyPartsState(
    val isLoading: Boolean = false,
    val bodyParts: List<String> = emptyList(),
    val error: String? = null
)
