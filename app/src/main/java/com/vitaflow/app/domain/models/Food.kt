package com.vitaflow.app.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Food(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val caloriesPer100g: Double,
    val carbsPer100g: Double,
    val proteinPer100g: Double,
    val fatPer100g: Double,
    val barcode: String? = null,
    val imageUrl: String? = null
)
