package com.vitaflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_steps")
data class DailyStepsEntity(
    @PrimaryKey
    val date: String,
    val steps: Int,
    val targetSteps: Int = 10000,
    val caloriesBurned: Int,
    val distanceMeters: Float,
    val activeMinutes: Int,
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)