package com.vitaflow.app.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.vitaflow.app.data.local.StepsDAO
import com.vitaflow.app.domain.models.StepsCount
import com.vitaflow.app.domain.repository.StepCounterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

private const val TAG = "STEP_COUNT_LISTENER"
class StepCounterRepositoryImpl @Inject constructor(private val dao: StepsDAO): StepCounterRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun storeSteps(stepsSinceLastReboot: Long) = withContext(Dispatchers.IO) {
        val stepsCount = StepsCount(
            steps = stepsSinceLastReboot,
            createdAt = Instant.now().toString()
        )
        Log.d(TAG, "Storing steps: $stepsCount")
        dao.insertAll(stepsCount)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun loadTodaySteps(): Long = withContext(Dispatchers.IO){
        val todayAtMidnight = (LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).toString())
        val todayDataPoints = dao.loadAllStepsFromToday(startDateTime = todayAtMidnight)
        when {
            todayDataPoints.isEmpty() -> 0
            else -> {
                val firstDataPointOfTheDay = todayDataPoints.first()
                val latestDataPointSoFar = todayDataPoints.last()

                val todaySteps = latestDataPointSoFar.steps - firstDataPointOfTheDay.steps
                Log.d(TAG, "Today Steps: $todaySteps")
                todaySteps
            }
        }
    }
}
