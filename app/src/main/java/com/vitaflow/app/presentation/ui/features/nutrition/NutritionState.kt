package com.vitaflow.app.presentation.ui.features.nutrition

data class NutritionState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalCalories: Int = 2000,
    val targetCalories: Int = 2000,
    val consumedCalories: Int = 1456,
    val burnedCalories: Int = 320,
    val carbs: MacroNutrient = MacroNutrient(current = 165, target = 250),
    val protein: MacroNutrient = MacroNutrient(current = 92, target = 150),
    val fat: MacroNutrient = MacroNutrient(current = 45, target = 65),
    val meals: List<Meal> = emptyList(),
    val waterIntake: Int = 1750,
    val targetWaterIntake: Int = 2500,
    val recentFoods: List<Food> = sampleRecentFoods
)

val sampleRecentFoods = listOf(
    Food(
        id = "1",
        name = "Greek Yogurt",
        calories = 130,
        carbs = 20,
        protein = 15,
        fat = 0,
        imageUrl = "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=400&h=300&fit=crop"
    ),
    Food(
        id = "2",
        name = "Banana",
        calories = 105,
        carbs = 27,
        protein = 1,
        fat = 0,
        imageUrl = "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=400&h=300&fit=crop"
    ),
    Food(
        id = "3",
        name = "Chicken Breast",
        calories = 185,
        carbs = 0,
        protein = 35,
        fat = 4,
        imageUrl = "https://images.unsplash.com/photo-1604503468506-a8da13d82791?w=400&h=300&fit=crop"
    ),
    Food(
        id = "4",
        name = "Oatmeal",
        calories = 150,
        carbs = 27,
        protein = 5,
        fat = 3,
        imageUrl = "https://images.unsplash.com/photo-1517082027879-e2e133fcb6a0?w=400&h=300&fit=crop"
    ),
    Food(
        id = "5",
        name = "Almonds",
        calories = 164,
        carbs = 6,
        protein = 6,
        fat = 14,
        imageUrl = "https://images.unsplash.com/photo-1508747703725-719777637510?w=400&h=300&fit=crop"
    )
)