package com.vitaflow.app.data.worker

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.vitaflow.app.data.local.StepsDAO
import com.vitaflow.app.data.remote.HealthConnectService
import com.vitaflow.app.domain.models.DailyStepsEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@HiltWorker
class StepsSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val healthConnectService: HealthConnectService,
    private val stepsDao: StepsDAO
) : CoroutineWorker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            if (!healthConnectService.isAvailable() || !healthConnectService.hasAllPermissions()) {
                return@withContext Result.failure(
                    workDataOf(ERROR_KEY to "Health Connect not available or permissions not granted")
                )
            }

            val today = LocalDate.now()
            val syncResults = mutableListOf<Boolean>()

            for (daysAgo in 0..6) {
                val date = today.minusDays(daysAgo.toLong())
                val syncSuccess = syncDataForDate(date)
                syncResults.add(syncSuccess)
            }

            val cleanupDate = today.minusDays(90).toString()
            stepsDao.deleteOldSteps(cleanupDate)

            if (syncResults.all { it }) {
                Result.success(workDataOf(SYNC_SUCCESS_KEY to true))
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure(workDataOf(ERROR_KEY to e.message))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun syncDataForDate(date: LocalDate): Boolean {
        return try {
            val healthData = healthConnectService.getDailyHealthData(date)

            val entity = DailyStepsEntity(
                date = date.toString(),
                steps = healthData.steps,
                targetSteps = 10000, // You can make this configurable
                caloriesBurned = healthData.caloriesBurned,
                distanceMeters = healthData.distanceMeters,
                activeMinutes = healthData.activeMinutes,
                lastSyncTimestamp = System.currentTimeMillis()
            )

            stepsDao.insertSteps(entity)
            true
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        const val WORK_NAME = "steps_sync_work"
        const val ERROR_KEY = "error"
        const val SYNC_SUCCESS_KEY = "sync_success"

        fun schedulePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(true)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<StepsSyncWorker>(
                repeatInterval = 1,
                repeatIntervalTimeUnit = TimeUnit.HOURS,
                flexTimeInterval = 15,
                flexTimeIntervalUnit = TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        }

        fun scheduleImmediateSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val syncRequest = OneTimeWorkRequestBuilder<StepsSyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "${WORK_NAME}_immediate",
                ExistingWorkPolicy.REPLACE,
                syncRequest
            )
        }

        fun cancelSync(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}