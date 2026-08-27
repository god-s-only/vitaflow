package com.vitaflow.app.data.mappers.local

import android.os.Build
import androidx.annotation.RequiresApi
import com.vitaflow.app.data.local.entity.DailyStepsEntity
import com.vitaflow.app.domain.models.DailySteps
import java.time.LocalDate

// DailySteps: Entity -> Domain
@RequiresApi(Build.VERSION_CODES.O)
fun DailyStepsEntity.toDomain(): DailySteps = DailySteps(
    date = LocalDate.parse(date),
    steps = steps,
    targetSteps = targetSteps
)