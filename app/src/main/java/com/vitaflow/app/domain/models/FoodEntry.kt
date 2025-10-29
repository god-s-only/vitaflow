package com.vitaflow.app.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val foodId: String,
    val mealType: String, // "breakfast", "lunch", "dinner", "snacks"
    val quantity: Double, // Amount in grams
    val timestamp: Long,
    val date: String // "2024-01-15" format
)
