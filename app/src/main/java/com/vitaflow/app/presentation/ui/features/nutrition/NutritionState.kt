package com.vitaflow.app.presentation.ui.features.nutrition

data class NutritionState(
    val isLoading: Boolean = false,
    val error: String? = null,

    // Calorie Data
    val targetCalories: Int = 2000,
    val consumedCalories: Int = 0,
    val burnedCalories: Int = 0,
    val totalCalories: Int = targetCalories - consumedCalories + burnedCalories,

    // Macronutrients
    val carbs: MacroNutrient = MacroNutrient(current = 0, target = 250),
    val protein: MacroNutrient = MacroNutrient(current = 0, target = 150),
    val fat: MacroNutrient = MacroNutrient(current = 0, target = 65),

    // Meals with actual food entries
    val meals: List<MealWithEntries> = emptyList(),

    // Water Intake
    val waterIntake: Int = 0,
    val targetWaterIntake: Int = 2000,

    // Recent Foods
    val recentFoods: List<FoodItem> = emptyList(),

    // Current Date
    val currentDate: String = ""
)

data class MealWithEntries(
    val type: String,
    val entries: List<FoodEntryWithDetails>,
    val totalCalories: Int,
    val totalCarbs: Int,
    val totalProtein: Int,
    val totalFat: Int
)

data class FoodEntryWithDetails(
    val entryId: Long,
    val food: FoodItem,
    val quantity: Double, // in grams
    val calculatedCalories: Int,
    val calculatedCarbs: Int,
    val calculatedProtein: Int,
    val calculatedFat: Int,
    val timestamp: Long
)

data class FoodItem(
    val id: String,
    val name: String,
    val caloriesPer100g: Double,
    val carbsPer100g: Double?,
    val proteinPer100g: Double,
    val fatPer100g: Double,
    val imageUrl: String?
)

fun FoodItem.calculateNutrients(quantity: Double): FoodEntryWithDetails {
    val multiplier = quantity / 100.0
    return FoodEntryWithDetails(
        entryId = 0L,
        food = this,
        quantity = quantity,
        calculatedCalories = (caloriesPer100g * multiplier).toInt(),
        calculatedCarbs = (carbsPer100g?.times(multiplier))?.toInt() ?: 0,
        calculatedProtein = (proteinPer100g * multiplier).toInt(),
        calculatedFat = (fatPer100g * multiplier).toInt(),
        timestamp = System.currentTimeMillis()
    )
}