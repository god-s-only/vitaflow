package com.vitaflow.app.presentation.ui.features.nutrition

data class NutritionState(
    val currentDate: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,

    // Calorie tracking
    val targetCalories: Int = 2000,
    val consumedCalories: Int = 0,
    val burnedCalories: Int = 0,
    val totalCalories: Int = targetCalories - consumedCalories + burnedCalories,

    // Macronutrients
    val carbs: MacroNutrient = MacroNutrient(0, 250),
    val protein: MacroNutrient = MacroNutrient(0, 150),
    val fat: MacroNutrient = MacroNutrient(0, 70),

    // Water intake
    val waterIntake: Int = 0,
    val targetWaterIntake: Int = 2000,

    // Meals
    val meals: List<MealWithEntries> = emptyList(),

    // Recent foods
    val recentFoods: List<FoodItem> = emptyList()
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
    val quantity: Double,
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