package com.vitaflow.app.domain.models

data class StepsData(
    val currentSteps: Int = 0,
    val targetSteps: Int = 10000,
    val caloriesBurned: Int = 0,
    val distanceKm: Float = 0f,
    val activeMinutes: Int = 0,
    val weeklyData: List<DailySteps> = emptyList()
)