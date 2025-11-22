package com.vitaflow.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vitaflow.app.domain.models.DailyNutrition
import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.models.FoodEntry

@Database(entities = [FoodEntry::class, DailyNutrition::class, Food::class], version = 2, exportSchema = false)
abstract class NutritionDatabase: RoomDatabase() {
    abstract fun nutritionDao(): NutritionDao
}