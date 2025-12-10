package com.vitaflow.app.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.vitaflow.app.common.Resource
import com.vitaflow.app.data.local.StepsDAO
import com.vitaflow.app.data.local.StepsPreferences
import com.vitaflow.app.data.remote.HealthConnectService
import com.vitaflow.app.domain.models.DailySteps
import com.vitaflow.app.domain.models.DailyStepsEntity
import com.vitaflow.app.domain.models.StepsData
import com.vitaflow.app.domain.repository.StepCounterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject

class StepCounterRepositoryImpl @Inject constructor(
    private val dao: StepsDAO,
    private val healthConnectService: HealthConnectService,
    private val stepsPreferences: StepsPreferences
) : StepCounterRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getTodaySteps(): Flow<Resource<StepsData>> = flow {
        emit(Resource.Loading())

        try {
            val today = LocalDate.now()
            val startDate = today.minusDays(6)

            // Use suspend function instead of Flow to get immediate result
            val entities = dao.getStepsInRange(startDate.toString(), today.toString())

            if (entities.isEmpty()) {
                emit(Resource.Error("No data available. Please sync your data."))
            } else {
                val todayEntity = entities.lastOrNull { it.date == today.toString() }
                val domainModels = entities.map { it.toDomainModel() }

                emit(
                    Resource.Success(
                        StepsData(
                            currentSteps = todayEntity?.steps ?: 0,
                            targetSteps = todayEntity?.targetSteps ?: 10000,
                            caloriesBurned = todayEntity?.caloriesBurned ?: 0,
                            distanceKm = (todayEntity?.distanceMeters ?: 0f) / 1000f,
                            activeMinutes = todayEntity?.activeMinutes ?: 0,
                            weeklyData = domainModels
                        )
                    )
                )
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getWeeklySteps(): Flow<Resource<List<DailySteps>>> = flow {
        emit(Resource.Loading())

        try {
            val today = LocalDate.now()
            val startDate = today.minusDays(6)

            val entities = dao.getStepsInRange(startDate.toString(), today.toString())

            if (entities.isEmpty()) {
                emit(Resource.Error("No data available"))
            } else {
                emit(Resource.Success(entities.map { it.toDomainModel() }))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun syncStepsData(): Resource<Boolean> {
        return try {
            if (!healthConnectService.isAvailable()) {
                return Resource.Error("Health Connect not available")
            }

            if (!healthConnectService.hasAllPermissions()) {
                return Resource.Error("Missing Health Connect permissions")
            }

            val today = LocalDate.now()
            for (daysAgo in 0..6) {
                val date = today.minusDays(daysAgo.toLong())
                val healthData = healthConnectService.getDailyHealthData(date)

                val entity = DailyStepsEntity(
                    date = date.toString(),
                    steps = healthData.steps,
                    targetSteps = 10000,
                    caloriesBurned = healthData.caloriesBurned,
                    distanceMeters = healthData.distanceMeters,
                    activeMinutes = healthData.activeMinutes,
                    lastSyncTimestamp = System.currentTimeMillis()
                )

                dao.insertSteps(entity)
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Sync failed")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getStepsByDate(date: LocalDate): Resource<DailySteps> {
        return try {
            val entity = dao.getStepsByDate(date.toString())
            if (entity != null) {
                Resource.Success(entity.toDomainModel())
            } else {
                Resource.Error("No data for this date")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error fetching data")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateTargetSteps(targetSteps: Int): Resource<Boolean> {
        return try {
            val today = LocalDate.now()
            val entity = dao.getStepsByDate(today.toString())

            if (entity != null) {
                dao.insertSteps(entity.copy(targetSteps = targetSteps))
                Resource.Success(true)
            } else {
                Resource.Error("No data for today")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error updating target")
        }
    }

    override fun hasHealthConnectPermissions(): Flow<Boolean> = flow {
        emit(healthConnectService.hasAllPermissions())
    }

    override suspend fun isHealthConnectAvailable(): Boolean {
        return healthConnectService.isAvailable()
    }

    override suspend fun getStepsTarget(): Int? {
        return stepsPreferences.getStepsTarget()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun DailyStepsEntity.toDomainModel(): DailySteps {
    return DailySteps(
        date = LocalDate.parse(this.date),
        steps = this.steps,
        targetSteps = this.targetSteps
    )
}