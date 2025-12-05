package com.vitaflow.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vitaflow.app.domain.models.DailyStepsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StepsDAO {
    @Query("SELECT * FROM daily_steps WHERE date = :date")
    suspend fun getStepsByDate(date: String): DailyStepsEntity?

    @Query("SELECT * FROM daily_steps WHERE date = :date")
    fun getStepsByDateFlow(date: String): Flow<DailyStepsEntity?>

    @Query("SELECT * FROM daily_steps WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC")
    suspend fun getStepsInRange(startDate: String, endDate: String): List<DailyStepsEntity>

    @Query("SELECT * FROM daily_steps WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC")
    fun getStepsInRangeFlow(startDate: String, endDate: String): Flow<List<DailyStepsEntity>>

    @Query("SELECT * FROM daily_steps ORDER BY date DESC LIMIT :limit")
    fun getRecentSteps(limit: Int): Flow<List<DailyStepsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSteps(steps: DailyStepsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSteps(steps: List<DailyStepsEntity>)

    @Query("DELETE FROM daily_steps WHERE date < :date")
    suspend fun deleteOldSteps(date: String)

    @Query("SELECT SUM(steps) FROM daily_steps WHERE date >= :startDate AND date <= :endDate")
    suspend fun getTotalStepsInRange(startDate: String, endDate: String): Int?

    @Query("SELECT AVG(steps) FROM daily_steps WHERE date >= :startDate AND date <= :endDate")
    suspend fun getAverageStepsInRange(startDate: String, endDate: String): Float?
}