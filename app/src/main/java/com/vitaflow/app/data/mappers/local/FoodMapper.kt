package com.vitaflow.app.data.mappers.local

import com.vitaflow.app.data.local.entity.FoodEntity
import com.vitaflow.app.data.local.entity.FoodEntryEntity
import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.models.FoodEntry

// Food: Entity -> Domain
fun FoodEntity.toDomain(): Food = Food(
    id = id,
    name = name,
    caloriesPer100g = caloriesPer100g,
    carbsPer100g = carbsPer100g,
    proteinPer100g = proteinPer100g,
    fatPer100g = fatPer100g,
    barcode = barcode,
    imageUrl = imageUrl
)

// Food: Domain -> Entity
fun Food.toEntity(): FoodEntity = FoodEntity(
    id = id,
    name = name,
    caloriesPer100g = caloriesPer100g,
    carbsPer100g = carbsPer100g,
    proteinPer100g = proteinPer100g,
    fatPer100g = fatPer100g,
    barcode = barcode,
    imageUrl = imageUrl
)

// FoodEntry: Entity -> Domain
fun FoodEntryEntity.toDomain(): FoodEntry = FoodEntry(
    id = id,
    foodId = foodId,
    mealType = mealType,
    quantity = quantity,
    timestamp = timestamp,
    date = date
)

// FoodEntry: Domain -> Entity
fun FoodEntry.toEntity(): FoodEntryEntity = FoodEntryEntity(
    id = id,
    foodId = foodId,
    mealType = mealType,
    quantity = quantity,
    timestamp = timestamp,
    date = date
)