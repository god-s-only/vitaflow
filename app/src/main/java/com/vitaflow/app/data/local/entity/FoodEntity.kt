package com.vitaflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Food")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val caloriesPer100g: Double,
    val carbsPer100g: Double? = null,
    val proteinPer100g: Double,
    val fatPer100g: Double,
    val barcode: String? = null,
    val imageUrl: String? = null
)