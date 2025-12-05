package com.vitaflow.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vitaflow.app.domain.models.DailyNutrition
import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.models.FoodEntry
import com.vitaflow.app.domain.models.StepsCount

@Database(entities = [FoodEntry::class, DailyNutrition::class, Food::class, StepsCount::class], version = 1)
abstract class NutritionDatabase: RoomDatabase() {
    abstract fun nutritionDao(): NutritionDao
    abstract fun stepsDao(): StepsDAO
}