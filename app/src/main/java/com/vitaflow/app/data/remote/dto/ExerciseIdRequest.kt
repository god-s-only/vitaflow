package com.vitaflow.app.data.remote.dto


import com.google.gson.annotations.SerializedName
import com.vitaflow.app.domain.models.Exercise

data class ExerciseIdRequest(
    @SerializedName("bodyParts")
    val bodyParts: List<String>,
    @SerializedName("equipments")
    val equipments: List<String>,
    @SerializedName("exerciseId")
    val exerciseId: String,
    @SerializedName("gifUrl")
    val gifUrl: String,
    @SerializedName("instructions")
    val instructions: List<String>,
    @SerializedName("name")
    val name: String,
    @SerializedName("secondaryMuscles")
    val secondaryMuscles: List<String>,
    @SerializedName("targetMuscles")
    val targetMuscles: List<String>
)
fun ExerciseIdRequest.toExercise(): Exercise {
    return Exercise(
        bodyParts = bodyParts,
        equipments = equipments,
        exerciseId = exerciseId,
        gifUrl = gifUrl,
        instructions = instructions,
        name = name,
        secondaryMuscles = secondaryMuscles,
        targetMuscles = targetMuscles
    )
}