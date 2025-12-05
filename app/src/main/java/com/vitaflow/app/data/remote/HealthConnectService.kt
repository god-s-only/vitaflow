package com.vitaflow.app.data.remote

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthConnectService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val healthConnectClient by lazy {
        HealthConnectClient.getOrCreate(context)
    }

    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class)
    )

    suspend fun isAvailable(): Boolean {
        return HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
    }

    suspend fun hasAllPermissions(): Boolean {
        return healthConnectClient.permissionController.getGrantedPermissions()
            .containsAll(permissions)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getStepsForDate(date: LocalDate): Int {
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

        return try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
                )
            )
            response.records.sumOf { it.count.toInt() }
        } catch (e: Exception) {
            0
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getDistanceForDate(date: LocalDate): Float {
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

        return try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = DistanceRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
                )
            )
            response.records.sumOf { it.distance.inMeters }.toFloat()
        } catch (e: Exception) {
            0f
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getCaloriesForDate(date: LocalDate): Int {
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

        return try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = ActiveCaloriesBurnedRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
                )
            )
            response.records.sumOf { it.energy.inKilocalories }.toInt()
        } catch (e: Exception) {
            0
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getActiveMinutesForDate(date: LocalDate): Int {
        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

        return try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = ExerciseSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
                )
            )
            response.records.sumOf {
                (it.endTime.toEpochMilli() - it.startTime.toEpochMilli()) / 60000
            }.toInt()
        } catch (e: Exception) {
            0
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getDailyHealthData(date: LocalDate): DailyHealthData {
        return DailyHealthData(
            date = date,
            steps = getStepsForDate(date),
            distanceMeters = getDistanceForDate(date),
            caloriesBurned = getCaloriesForDate(date),
            activeMinutes = getActiveMinutesForDate(date)
        )
    }
}

data class DailyHealthData(
    val date: LocalDate,
    val steps: Int,
    val distanceMeters: Float,
    val caloriesBurned: Int,
    val activeMinutes: Int
)