package com.vitaflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DailyNutrition")
data class DailyNutritionEntity(
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