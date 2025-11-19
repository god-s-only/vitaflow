package com.vitaflow.app.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DailyNutrition(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val date: String,
    val calories: Double,
    val carbs: Double,
    val protein: Double,
    val fat: Double,
    val water: Double? = null
)
