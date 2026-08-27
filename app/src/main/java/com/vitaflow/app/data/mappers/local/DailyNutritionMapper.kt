package com.vitaflow.app.data.mappers.local

import com.vitaflow.app.data.local.entity.DailyNutritionEntity
import com.vitaflow.app.domain.models.DailyNutrition

// DailyNutrition: Entity -> Domain
fun DailyNutritionEntity.toDomain(): DailyNutrition = DailyNutrition(
    id = id,
    name = name,
    date = date,
    calories = calories,
    carbs = carbs,
    protein = protein,
    fat = fat,
    water = water
)

// DailyNutrition: Domain -> Entity
fun DailyNutrition.toEntity(): DailyNutritionEntity = DailyNutritionEntity(
    id = id,
    name = name,
    date = date,
    calories = calories,
    carbs = carbs,
    protein = protein,
    fat = fat,
    water = water
)