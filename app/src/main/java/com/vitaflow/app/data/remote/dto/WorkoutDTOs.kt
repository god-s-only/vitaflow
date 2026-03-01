package com.vitaflow.app.data.remote.dto

import com.google.gson.annotations.SerializedName

// Featured Workout DTOs

data class FeaturedWorkoutResponse(
    @SerializedName("workout")
    val workout: FeaturedWorkoutDTO?,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String?
)

data class FeaturedWorkoutDTO(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("difficulty")
    val difficulty: String,
    @SerializedName("duration")
    val duration: Int, // in minutes
    @SerializedName("calories")
    val calories: Int,
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("exercises")
    val exercises: List<WorkoutExerciseDTO>? = null
)

data class WorkoutExerciseDTO(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("gifUrl")
    val gifUrl: String?,
    @SerializedName("sets")
    val sets: Int?,
    @SerializedName("reps")
    val reps: Int?,
    @SerializedName("duration")
    val duration: Int?, // in seconds
    @SerializedName("restTime")
    val restTime: Int? // in seconds
)

// Quick Training / Additional Training DTOs

data class QuickTrainingResponse(
    @SerializedName("trainings")
    val trainings: List<QuickTrainingDTO>?,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String?
)

data class QuickTrainingDTO(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("duration")
    val duration: Int, // in minutes
    @SerializedName("calories")
    val calories: Int,
    @SerializedName("difficulty")
    val difficulty: String, // Beginner, Intermediate, Advanced
    @SerializedName("emoji")
    val emoji: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("exerciseCount")
    val exerciseCount: Int?
)
