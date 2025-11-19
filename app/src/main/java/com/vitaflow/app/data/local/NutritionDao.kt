package com.vitaflow.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.vitaflow.app.domain.models.DailyNutrition
import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.models.FoodEntry

@Dao
interface NutritionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutrition(dailyNutrition: DailyNutrition)

    @Delete
    suspend fun deleteNutrition(dailyNutrition: DailyNutrition)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNutrition(dailyNutritionList: List<DailyNutrition>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodEntry(foodEntry: FoodEntry)

    @Delete
    suspend fun deleteFoodEntry(foodEntry: FoodEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: Food)

    @Delete
    suspend fun deleteFood(food: Food)

}