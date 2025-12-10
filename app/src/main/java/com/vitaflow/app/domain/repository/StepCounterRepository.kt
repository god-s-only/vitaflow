package com.vitaflow.app.domain.repository

import com.vitaflow.app.common.Resource
import com.vitaflow.app.domain.models.DailySteps
import com.vitaflow.app.domain.models.StepsData
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface StepCounterRepository {
    fun getTodaySteps(): Flow<Resource<StepsData>>
    fun getWeeklySteps(): Flow<Resource<List<DailySteps>>>
    suspend fun syncStepsData(): Resource<Boolean>
    suspend fun getStepsByDate(date: LocalDate): Resource<DailySteps>
    suspend fun updateTargetSteps(targetSteps: Int): Resource<Boolean>
    fun hasHealthConnectPermissions(): Flow<Boolean>
    suspend fun isHealthConnectAvailable(): Boolean

    suspend fun getStepsTarget(): Int?
}