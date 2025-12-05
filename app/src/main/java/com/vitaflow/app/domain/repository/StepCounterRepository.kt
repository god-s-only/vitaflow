package com.vitaflow.app.domain.repository

interface StepCounterRepository {
    suspend fun storeSteps(stepsSinceLastReboot: Long)
    suspend fun loadTodaySteps(): Long
}