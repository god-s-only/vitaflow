package com.vitaflow.app.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "steps")
data class StepsCount(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val steps: Long,
    val createdAt: String
)
