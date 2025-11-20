package com.vitaflow.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

    @Query("DELETE FROM FoodEntry WHERE id = :entryId")
    suspend fun deleteFoodEntryById(entryId: Long)

    @Query("SELECT * FROM FoodEntry WHERE date = :date ORDER BY timestamp DESC")
    fun getFoodEntriesForDate(date: String): Flow<List<FoodEntry>>

    @Query("SELECT * FROM FoodEntry WHERE date = :date AND mealType = :mealType ORDER BY timestamp DESC")
    fun getFoodEntriesForMealType(date: String, mealType: String): Flow<List<FoodEntry>>

    @Query("SELECT * FROM Food WHERE id = :foodId")
    suspend fun getFoodById(foodId: String): Food?

    @Query("SELECT DISTINCT f.* FROM Food f INNER JOIN FoodEntry fe ON f.id = fe.foodId ORDER BY fe.timestamp DESC LIMIT :limit")
    fun getRecentFoods(limit: Int): Flow<List<Food>>

    @Query("SELECT * FROM Food WHERE name LIKE '%' || :query || '%'")
    fun searchFoods(query: String): Flow<List<Food>>

    @Query("SELECT * FROM DailyNutrition WHERE date = :date")
    fun getDailyNutrition(date: String): Flow<DailyNutrition?>

    @Query("UPDATE DailyNutrition SET water = :water WHERE date = :date")
    suspend fun updateWaterIntake(date: String, water: Double)

    @Query("SELECT * FROM DailyNutrition ORDER BY date DESC LIMIT 7")
    fun getLastSevenDays(): Flow<List<DailyNutrition>>

    @Query("""
        SELECT 
            SUM(f.caloriesPer100g * fe.quantity / 100) as totalCalories,
            SUM(f.carbsPer100g * fe.quantity / 100) as totalCarbs,
            SUM(f.proteinPer100g * fe.quantity / 100) as totalProtein,
            SUM(f.fatPer100g * fe.quantity / 100) as totalFat
        FROM FoodEntry fe
        INNER JOIN Food f ON fe.foodId = f.id
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