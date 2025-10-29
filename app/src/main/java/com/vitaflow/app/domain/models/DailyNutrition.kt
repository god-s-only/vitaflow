package com.vitaflow.app.domain.models

import androidx.room.Entity

@Entity
data class DailyNutrition(
    val date: String, // "2024-01-15" format
    val calories: Int,
    val carbs: Double,
    val protein: Double,
    val fat: Double,
    val water: Int // milliliters
)
