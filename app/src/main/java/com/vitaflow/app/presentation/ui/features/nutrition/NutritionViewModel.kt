package com.vitaflow.app.presentation.ui.features.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    // TODO: Inject nutrition repository when implemented
) : ViewModel() {

    private val _state = MutableStateFlow(NutritionState())
    val state: StateFlow<NutritionState> = _state.asStateFlow()

    init {
        loadNutritionData()
    }

    private fun loadNutritionData() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, error = null)

                // TODO: Load actual data from repository
                // For now, we'll use the default state values

                _state.value = _state.value.copy(
                    isLoading = false,
                    // Sample data is already set in NutritionState defaults
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }

    fun addWater(amount: Int) {
        viewModelScope.launch {
            val currentIntake = _state.value.waterIntake
            val newIntake =
                (currentIntake + amount).coerceAtMost(_state.value.targetWaterIntake + 1000) // Allow slight overflow

            _state.value = _state.value.copy(
                waterIntake = newIntake
            )

            // TODO: Save water intake to repository
        }
    }

    fun addFood(food: Food, mealType: String) {
        viewModelScope.launch {
            val currentMeals = _state.value.meals.toMutableList()
            val existingMealIndex = currentMeals.indexOfFirst { it.type == mealType }

            if (existingMealIndex != -1) {
                // Update existing meal
                val existingMeal = currentMeals[existingMealIndex]
                val updatedFoods = existingMeal.foods + food
                val updatedCalories = existingMeal.calories + food.calories

                currentMeals[existingMealIndex] = existingMeal.copy(
                    foods = updatedFoods,
                    calories = updatedCalories
                )
            } else {
                // Create new meal
                currentMeals.add(
                    Meal(
                        type = mealType,
                        foods = listOf(food),
                        calories = food.calories
                    )
                )
            }

            // Update consumed calories
            val newConsumedCalories = _state.value.consumedCalories + food.calories

            // Update macronutrients
            val updatedCarbs = _state.value.carbs.copy(
                current = _state.value.carbs.current + food.carbs
            )
            val updatedProtein = _state.value.protein.copy(
                current = _state.value.protein.current + food.protein
            )
            val updatedFat = _state.value.fat.copy(
                current = _state.value.fat.current + food.fat
            )

            _state.value = _state.value.copy(
                meals = currentMeals,
                consumedCalories = newConsumedCalories,
                carbs = updatedCarbs,
                protein = updatedProtein,
                fat = updatedFat
            )

            // TODO: Save meal data to repository
        }
    }

    fun removeFood(food: Food, mealType: String) {
        viewModelScope.launch {
            val currentMeals = _state.value.meals.toMutableList()
            val existingMealIndex = currentMeals.indexOfFirst { it.type == mealType }

            if (existingMealIndex != -1) {
                val existingMeal = currentMeals[existingMealIndex]
                val updatedFoods = existingMeal.foods.filter { it.id != food.id }

                if (updatedFoods.isEmpty()) {
                    // Remove the meal entirely
                    currentMeals.removeAt(existingMealIndex)
                } else {
                    // Update the meal
                    val updatedCalories = existingMeal.calories - food.calories
                    currentMeals[existingMealIndex] = existingMeal.copy(
                        foods = updatedFoods,
                        calories = updatedCalories
                    )
                }

                // Update consumed calories
                val newConsumedCalories =
                    (_state.value.consumedCalories - food.calories).coerceAtLeast(0)

                // Update macronutrients
                val updatedCarbs = _state.value.carbs.copy(
                    current = (_state.value.carbs.current - food.carbs).coerceAtLeast(0)
                )
                val updatedProtein = _state.value.protein.copy(
                    current = (_state.value.protein.current - food.protein).coerceAtLeast(0)
                )
                val updatedFat = _state.value.fat.copy(
                    current = (_state.value.fat.current - food.fat).coerceAtLeast(0)
                )

                _state.value = _state.value.copy(
                    meals = currentMeals,
                    consumedCalories = newConsumedCalories,
                    carbs = updatedCarbs,
                    protein = updatedProtein,
                    fat = updatedFat
                )

                // TODO: Save updated meal data to repository
            }
        }
    }

    fun updateCalorieTarget(newTarget: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                targetCalories = newTarget.coerceAtLeast(1000) // Minimum 1000 calories
            )

            // TODO: Save calorie target to repository
        }
    }

    fun updateMacroTargets(carbsTarget: Int, proteinTarget: Int, fatTarget: Int) {
        viewModelScope.launch {
            val updatedCarbs = _state.value.carbs.copy(target = carbsTarget.coerceAtLeast(0))
            val updatedProtein = _state.value.protein.copy(target = proteinTarget.coerceAtLeast(0))
            val updatedFat = _state.value.fat.copy(target = fatTarget.coerceAtLeast(0))

            _state.value = _state.value.copy(
                carbs = updatedCarbs,
                protein = updatedProtein,
                fat = updatedFat
            )

            // TODO: Save macro targets to repository
        }
    }

    fun refreshData() {
        loadNutritionData()
    }
}