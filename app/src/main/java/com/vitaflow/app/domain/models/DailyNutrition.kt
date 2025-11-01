package com.vitaflow.app.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DailyNutrition(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: String, // "2024-01-15" format
    val calories: Int,
    val carbs: Double,
    val protein: Double,
    val fat: Double,
    val water: Int // milliliters
)
