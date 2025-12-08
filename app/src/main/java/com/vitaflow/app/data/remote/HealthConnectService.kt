package com.vitaflow.app.data.remote

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthConnectService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Use lazy initialization with null safety
    private val healthConnectClient: HealthConnectClient? by lazy {
        try {
            if (HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE) {
                HealthConnectClient.getOrCreate(context)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Required permissions
    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(DistanceRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class)
    )

    /**
     * Check if Health Connect SDK is available
     */
    suspend fun isAvailable(): Boolean {
        return try {
            HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get the SDK status for detailed error handling
     */
    fun getSdkStatus(): Int {
        return try {
            HealthConnectClient.getSdkStatus(context)
        } catch (e: Exception) {
            HealthConnectClient.SDK_UNAVAILABLE
        }
    }

    /**
     * Check if Health Connect is installed
     */
    fun isHealthConnectInstalled(): Boolean {
        return getSdkStatus() != HealthConnectClient.SDK_UNAVAILABLE
    }

    /**
     * Get intent to install Health Connect from Play Store
     */
    fun getHealthConnectInstallIntent(): Intent {
        return Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    /**
     * Check if all required permissions are granted
     */
    suspend fun hasAllPermissions(): Boolean {
        return try {
            healthConnectClient?.permissionController?.getGrantedPermissions()
                ?.containsAll(permissions) ?: false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get steps for a specific date
     */
    suspend fun getStepsForDate(date: LocalDate): Int {
        if (healthConnectClient == null) return 0

        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

        return try {
            val response = healthConnectClient!!.readRecords(
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

    /**
     * Get distance for a specific date
     */
    suspend fun getDistanceForDate(date: LocalDate): Float {
        if (healthConnectClient == null) return 0f

        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

        return try {
            val response = healthConnectClient!!.readRecords(
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

    /**
     * Get calories burned for a specific date
     */
    suspend fun getCaloriesForDate(date: LocalDate): Int {
        if (healthConnectClient == null) return 0

        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

        return try {
            val response = healthConnectClient!!.readRecords(
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

    /**
     * Get active minutes for a specific date
     */
    suspend fun getActiveMinutesForDate(date: LocalDate): Int {
        if (healthConnectClient == null) return 0

        val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

        return try {
            val response = healthConnectClient!!.readRecords(
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