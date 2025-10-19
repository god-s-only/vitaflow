package com.vitaflow.app.domain.models

data class Exercise(
    val bodyParts: List<String>,
    val equipments: List<String>,
    val exerciseId: String,
    val gifUrl: String,
    val instructions: List<String>,
    val name: String,
    val secondaryMuscles: List<String>,
    val targetMuscles: List<String>
)