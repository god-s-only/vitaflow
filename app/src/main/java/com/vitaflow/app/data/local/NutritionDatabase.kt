package com.vitaflow.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vitaflow.app.data.local.entity.DailyNutritionEntity
import com.vitaflow.app.data.local.entity.DailyStepsEntity
import com.vitaflow.app.data.local.entity.FoodEntity
import com.vitaflow.app.data.local.entity.FoodEntryEntity

@Database(entities = [FoodEntryEntity::class, DailyNutritionEntity::class, FoodEntity::class, DailyStepsEntity::class], version = 2)
abstract class NutritionDatabase: RoomDatabase() {
    abstract fun nutritionDao(): NutritionDao
    abstract fun stepsDao(): StepsDAO
}