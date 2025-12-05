package com.vitaflow.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.vitaflow.app.domain.models.StepsCount

@Dao
interface StepsDAO {
    @Query("SELECT * FROM steps")
    suspend fun getAll(): List<StepsCount>

    @Query("SELECT * FROM steps WHERE createdAt >= date(:startDateTime) " +
            "AND createdAt < date(:startDateTime, '+1 day')")
    suspend fun loadAllStepsFromToday(startDateTime: String): Array<StepsCount>

    @Insert
    suspend fun insertAll(vararg steps: StepsCount)

    @Delete
    suspend fun delete(steps: StepsCount)
}