package com.vitaflow.app.domain.models

// Domain models for Featured Workout

data class FeaturedWorkout(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val category: String,
    val difficulty: String,
    val duration: Int,
    val calories: Int,
    val rating: Double,
    val exercises: List<WorkoutExercise> = emptyList()
)

data class WorkoutExercise(
    val id: String,
    val name: String,
    val gifUrl: String?,
    val sets: Int?,
    val reps: Int?,
    val duration: Int?, // in seconds
    val restTime: Int?  // in seconds
)

// Domain model for Quick Training

data class QuickTraining(
    val id: String,
    val name: String,
    val description: String,
    val duration: Int, // in minutes
    val calories: Int,
    val difficulty: String,
    val emoji: String,
    val category: String,
    val imageUrl: String?,
    val exerciseCount: Int?
)

// Difficulty levels
enum class WorkoutDifficulty {
    BEGINNER, INTERMEDIATE, ADVANCED
}

// Workout categories
enum class WorkoutCategory {
    CARDIO, STRENGTH, HIIT, YOGA, FLEXIBILITY, FULL_BODY
}
