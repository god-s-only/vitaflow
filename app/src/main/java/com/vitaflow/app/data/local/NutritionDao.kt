package com.vitaflow.app.data.local

import androidx.room.*
import com.vitaflow.app.domain.models.DailyNutrition
import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.models.FoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutrition(dailyNutrition: DailyNutrition)

    @Delete
    suspend fun deleteNutrition(dailyNutrition: DailyNutrition)

    @Query("SELECT * FROM DailyNutrition WHERE date = :date")
    fun getDailyNutrition(date: String): Flow<DailyNutrition?>

    @Query("UPDATE DailyNutrition SET water = :water WHERE date = :date")
    suspend fun updateWaterIntake(date: String, water: Double)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodEntry(entry: FoodEntry)

    @Delete
    suspend fun deleteFoodEntry(entry: FoodEntry)

    @Query("DELETE FROM FoodEntry WHERE id = :entryId")
    suspend fun deleteFoodEntryById(entryId: Long)

    @Query("SELECT * FROM FoodEntry WHERE date = :date ORDER BY timestamp DESC")
    fun getFoodEntriesForDate(date: String): Flow<List<FoodEntry>>

    @Query("SELECT * FROM FoodEntry WHERE date = :date AND mealType = :mealType ORDER BY timestamp DESC")
    fun getFoodEntriesForMealType(date: String, mealType: String): Flow<List<FoodEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: Food)

    @Delete
    suspend fun deleteFood(food: Food)

    @Query("SELECT * FROM Food WHERE CAST(id AS TEXT) = :foodId")
    suspend fun getFoodById(foodId: String): Food?

    @Query("""
        SELECT DISTINCT f.* FROM Food f 
        INNER JOIN FoodEntry fe ON CAST(f.id AS TEXT) = fe.foodId 
        ORDER BY fe.timestamp DESC 
        LIMIT :limit
    """)
    fun getRecentFoods(limit: Int): Flow<List<Food>>

    @Query("SELECT * FROM Food WHERE name LIKE '%' || :query || '%'")
    fun searchFoods(query: String): Flow<List<Food>>

    @Query("""
        SELECT 
            COALESCE(SUM(f.caloriesPer100g * fe.quantity / 100), 0) as totalCalories,
            COALESCE(SUM(IFNULL(f.carbsPer100g, 0) * fe.quantity / 100), 0) as totalCarbs,
            COALESCE(SUM(f.proteinPer100g * fe.quantity / 100), 0) as totalProtein,
            COALESCE(SUM(f.fatPer100g * fe.quantity / 100), 0) as totalFat
        FROM FoodEntry fe
        INNER JOIN Food f ON CAST(f.id AS TEXT) = fe.foodId
        WHERE fe.date = :date
    """)
    suspend fun calculateDailyTotals(date: String): DailyTotals?
}

data class DailyTotals(
    val totalCalories: Double,
    val totalCarbs: Double,
    val totalProtein: Double,
    val totalFat: Double
)