package com.vitaflow.app.domain.models

import java.time.LocalDate

data class DailySteps(
    val date: LocalDate,
    val steps: Int,
    val targetSteps: Int = 10000
)