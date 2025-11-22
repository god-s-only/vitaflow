package com.vitaflow.app.presentation.ui.features.nutrition

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.repository.getTodayDate
import com.vitaflow.app.domain.usecase.nutrition.AddFoodEntryUseCase
import com.vitaflow.app.domain.usecase.nutrition.CalculateAndSaveDailyNutritionUseCase
import com.vitaflow.app.domain.usecase.nutrition.GetCalorieTargetUseCase
import com.vitaflow.app.domain.usecase.nutrition.GetDailyNutritionUseCase
import com.vitaflow.app.domain.usecase.nutrition.GetFoodByIdUseCase
import com.vitaflow.app.domain.usecase.nutrition.GetFoodEntriesForDateUseCase
import com.vitaflow.app.domain.usecase.nutrition.GetMacroTargetsUseCase
import com.vitaflow.app.domain.usecase.nutrition.GetRecentFoodsUseCase
import com.vitaflow.app.domain.usecase.nutrition.GetWaterTargetUseCase
import com.vitaflow.app.domain.usecase.nutrition.RemoveFoodEntryUseCase
import com.vitaflow.app.domain.usecase.nutrition.SetCalorieTargetUseCase
import com.vitaflow.app.domain.usecase.nutrition.SetMacroTargetsUseCase
import com.vitaflow.app.domain.usecase.nutrition.SetWaterTargetUseCase
import com.vitaflow.app.domain.usecase.nutrition.UpdateWaterIntakeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "NutritionViewModel"

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val addFoodEntryUseCase: AddFoodEntryUseCase,
    private val removeFoodEntryUseCase: RemoveFoodEntryUseCase,
    private val getFoodEntriesForDateUseCase: GetFoodEntriesForDateUseCase,
    private val updateWaterIntakeUseCase: UpdateWaterIntakeUseCase,
    private val getDailyNutritionUseCase: GetDailyNutritionUseCase,
    private val calculateAndSaveDailyNutritionUseCase: CalculateAndSaveDailyNutritionUseCase,
    private val getRecentFoodsUseCase: GetRecentFoodsUseCase,
    private val getFoodByIdUseCase: GetFoodByIdUseCase,
    private val getCalorieTargetUseCase: GetCalorieTargetUseCase,
    private val setCalorieTargetUseCase: SetCalorieTargetUseCase,
    private val getMacroTargetsUseCase: GetMacroTargetsUseCase,
    private val setMacroTargetsUseCase: SetMacroTargetsUseCase,
    private val getWaterTargetUseCase: GetWaterTargetUseCase,
    private val setWaterTargetUseCase: SetWaterTargetUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NutritionState())
    val state: StateFlow<NutritionState> = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent: SharedFlow<NavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        Log.d(TAG, "ViewModel initialized")
        loadTargets()
        observeFoodEntries()
        observeDailyNutrition()
        loadRecentFoods()
    }

    private fun loadTargets() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Loading targets...")

                // Load all targets
                val calorieTarget = getCalorieTargetUseCase()
                val (carbsTarget, proteinTarget, fatTarget) = getMacroTargetsUseCase()
                val waterTarget = getWaterTargetUseCase()

                Log.d(TAG, "Targets loaded - Calories: $calorieTarget, Carbs: $carbsTarget, Protein: $proteinTarget, Fat: $fatTarget, Water: $waterTarget")

                _state.update { currentState ->
                    currentState.copy(
                        currentDate = getTodayDate(),
                        targetCalories = calorieTarget,
                        targetWaterIntake = waterTarget,
                        carbs = currentState.carbs.copy(target = carbsTarget),
                        protein = currentState.protein.copy(target = proteinTarget),
                        fat = currentState.fat.copy(target = fatTarget)
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading targets", e)
                _state.update {
                    it.copy(error = "Failed to load targets: ${e.message}")
                }
            }
        }
    }

    private fun observeDailyNutrition() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting to observe daily nutrition...")
                getDailyNutritionUseCase().collect { dailyNutrition ->
                    Log.d(TAG, "Daily nutrition updated: $dailyNutrition")
                    _state.update { currentState ->
                        currentState.copy(
                            waterIntake = dailyNutrition?.water?.toInt() ?: 0,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error observing daily nutrition", e)
            }
        }
    }

    private fun loadRecentFoods() {
        viewModelScope.launch {
            try {
                getRecentFoodsUseCase(10).collect { foods ->
                    Log.d(TAG, "Recent foods loaded: ${foods.size} items")
                    _state.update { it.copy(recentFoods = foods.map { food -> food.toFoodItem() }) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading recent foods", e)
            }
        }
    }

    private fun observeFoodEntries() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting to observe food entries...")
                getFoodEntriesForDateUseCase().collect { entries ->
                    Log.d(TAG, "Food entries updated: ${entries.size} entries")

                    val groupedEntries = entries.groupBy { it.mealType }
                    Log.d(TAG, "Grouped by meal type: ${groupedEntries.keys}")

                    val mealsWithEntries = mutableListOf<MealWithEntries>()
                    var totalConsumed = 0
                    var totalCarbs = 0
                    var totalProtein = 0
                    var totalFat = 0

                    for ((mealType, mealEntries) in groupedEntries) {
                        val entriesWithDetails = mutableListOf<FoodEntryWithDetails>()
                        var mealCalories = 0
                        var mealCarbs = 0
                        var mealProtein = 0
                        var mealFat = 0

                        for (entry in mealEntries) {
                            val food = getFoodByIdUseCase(entry.foodId)
                            Log.d(TAG, "Processing entry: ${entry.id}, foodId: ${entry.foodId}, food found: ${food != null}")

                            if (food != null) {
                                val multiplier = entry.quantity / 100.0
                                val entryWithDetails = FoodEntryWithDetails(
                                    entryId = entry.id,
                                    food = food.toFoodItem(),
                                    quantity = entry.quantity,
                                    calculatedCalories = (food.caloriesPer100g * multiplier).toInt(),
                                    calculatedCarbs = (food.carbsPer100g?.times(multiplier))?.toInt() ?: 0,
                                    calculatedProtein = (food.proteinPer100g * multiplier).toInt(),
                                    calculatedFat = (food.fatPer100g * multiplier).toInt(),
                                    timestamp = entry.timestamp
                                )
                                entriesWithDetails.add(entryWithDetails)

                                mealCalories += entryWithDetails.calculatedCalories
                                mealCarbs += entryWithDetails.calculatedCarbs
                                mealProtein += entryWithDetails.calculatedProtein
                                mealFat += entryWithDetails.calculatedFat
                            }
                        }

                        Log.d(TAG, "$mealType totals - Cal: $mealCalories, Carbs: $mealCarbs, Protein: $mealProtein, Fat: $mealFat")

                        mealsWithEntries.add(
                            MealWithEntries(
                                type = mealType,
                                entries = entriesWithDetails,
                                totalCalories = mealCalories,
                                totalCarbs = mealCarbs,
                                totalProtein = mealProtein,
                                totalFat = mealFat
                            )
                        )

                        totalConsumed += mealCalories
                        totalCarbs += mealCarbs
                        totalProtein += mealProtein
                        totalFat += mealFat
                    }

                    Log.d(TAG, "Daily totals - Cal: $totalConsumed, Carbs: $totalCarbs, Protein: $totalProtein, Fat: $totalFat")

                    _state.update { currentState ->
                        currentState.copy(
                            meals = mealsWithEntries,
                            consumedCalories = totalConsumed,
                            carbs = currentState.carbs.copy(current = totalCarbs),
                            protein = currentState.protein.copy(current = totalProtein),
                            fat = currentState.fat.copy(current = totalFat),
                            isLoading = false
                        )
                    }

                    calculateAndSaveDailyNutritionUseCase()
                    Log.d(TAG, "Daily nutrition recalculated and saved")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error observing food entries", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load food entries: ${e.message}"
                    )
                }
            }
        }
    }

    fun addFoodToMeal(food: Food, mealType: String, quantity: Double) {
        viewModelScope.launch {
            Log.d(TAG, "Adding food to meal - Food: ${food.name}, MealType: $mealType, Quantity: $quantity")
            addFoodEntryUseCase(food, mealType, quantity).fold(
                onSuccess = {
                    Log.d(TAG, "Food added successfully: ${food.name}")
                    _navigationEvent.emit(
                        NavigationEvent.ShowMessage("${food.name} added to $mealType")
                    )
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to add food", error)
                    _state.update {
                        it.copy(error = "Failed to add food: ${error.message}")
                    }
                }
            )
        }
    }

    fun removeFoodEntry(entryId: Long) {
        viewModelScope.launch {
            Log.d(TAG, "Removing food entry: $entryId")
            removeFoodEntryUseCase(entryId).fold(
                onSuccess = {
                    Log.d(TAG, "Food entry removed successfully")
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to remove food", error)
                    _state.update {
                        it.copy(error = "Failed to remove food: ${error.message}")
                    }
                }
            )
        }
    }

    fun addWater(amount: Int) {
        viewModelScope.launch {
            val currentIntake = _state.value.waterIntake
            val newIntake = (currentIntake + amount).coerceAtMost(_state.value.targetWaterIntake + 1000)

            Log.d(TAG, "Adding water - Current: $currentIntake, Adding: $amount, New: $newIntake")

            updateWaterIntakeUseCase(newIntake.toDouble()).fold(
                onSuccess = {
                    _state.update { it.copy(waterIntake = newIntake) }
                    Log.d(TAG, "Water intake updated successfully")
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to update water", error)
                    _state.update {
                        it.copy(error = "Failed to update water intake: ${error.message}")
                    }
                }
            )
        }
    }

    fun onQuickAddClick(action: QuickAddAction) {
        viewModelScope.launch {
            when (action) {
                QuickAddAction.SCAN_BARCODE -> {
                    _navigationEvent.emit(NavigationEvent.NavigateToBarcodeScan)
                }
                QuickAddAction.TAKE_PHOTO -> {
                    _navigationEvent.emit(NavigationEvent.NavigateToPhotoCapture)
                }
                QuickAddAction.ADD_RECIPE -> {
                    _navigationEvent.emit(NavigationEvent.NavigateToRecipes)
                }
                QuickAddAction.QUICK_CALORIES -> {
                    _navigationEvent.emit(NavigationEvent.ShowQuickCaloriesDialog)
                }
            }
        }
    }

    fun onAddMealClick(mealType: String) {
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateToFoodSearch(mealType))
        }
    }

    fun onRecentFoodClick(food: FoodItem) {
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.ShowAddFoodDialog(food))
        }
    }

    fun updateCalorieTarget(newTarget: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Updating calorie target: $newTarget")
            setCalorieTargetUseCase(newTarget).fold(
                onSuccess = {
                    _state.update { it.copy(targetCalories = newTarget) }
                    Log.d(TAG, "Calorie target updated successfully")
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to update calorie target", error)
                    _state.update {
                        it.copy(error = "Failed to update calorie target: ${error.message}")
                    }
                }
            )
        }
    }

    fun updateMacroTargets(carbsTarget: Int, proteinTarget: Int, fatTarget: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Updating macro targets - Carbs: $carbsTarget, Protein: $proteinTarget, Fat: $fatTarget")
            setMacroTargetsUseCase(carbsTarget, proteinTarget, fatTarget).fold(
                onSuccess = {
                    _state.update { currentState ->
                        currentState.copy(
                            carbs = currentState.carbs.copy(target = carbsTarget),
                            protein = currentState.protein.copy(target = proteinTarget),
                            fat = currentState.fat.copy(target = fatTarget)
                        )
                    }
                    Log.d(TAG, "Macro targets updated successfully")
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to update macro targets", error)
                    _state.update {
                        it.copy(error = "Failed to update macro targets: ${error.message}")
                    }
                }
            )
        }
    }

    fun updateWaterTarget(newTarget: Int) {
        viewModelScope.launch {
            Log.d(TAG, "Updating water target: $newTarget")
            setWaterTargetUseCase(newTarget).fold(
                onSuccess = {
                    _state.update { it.copy(targetWaterIntake = newTarget) }
                    Log.d(TAG, "Water target updated successfully")
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to update water target", error)
                    _state.update {
                        it.copy(error = "Failed to update water target: ${error.message}")
                    }
                }
            )
        }
    }

    fun refreshData() {
        loadTargets()
    }

    private fun Food.toFoodItem() = FoodItem(
        id = this.id.toString(),
        name = this.name,
        caloriesPer100g = this.caloriesPer100g,
        carbsPer100g = this.carbsPer100g,
        proteinPer100g = this.proteinPer100g,
        fatPer100g = this.fatPer100g,
        imageUrl = this.imageUrl
    )
}

// Navigation Events
sealed class NavigationEvent {
    data class NavigateToFoodSearch(val mealType: String) : NavigationEvent()
    data class ShowAddFoodDialog(val food: FoodItem) : NavigationEvent()
    data class ShowMessage(val message: String) : NavigationEvent()
    object NavigateToBarcodeScan : NavigationEvent()
    object NavigateToPhotoCapture : NavigationEvent()
    object NavigateToRecipes : NavigationEvent()
    object ShowQuickCaloriesDialog : NavigationEvent()
}

// Quick Add Actions
enum class QuickAddAction {
    SCAN_BARCODE,
    TAKE_PHOTO,
    ADD_RECIPE,
    QUICK_CALORIES
}