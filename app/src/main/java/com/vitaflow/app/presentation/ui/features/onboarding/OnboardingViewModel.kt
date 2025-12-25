package com.vitaflow.app.presentation.ui.features.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaflow.app.domain.usecase.nutrition.SetCalorieTargetUseCase
import com.vitaflow.app.domain.usecase.nutrition.SetMacroTargetsUseCase
import com.vitaflow.app.domain.usecase.nutrition.SetWaterTargetUseCase
import com.vitaflow.app.domain.usecase.steps.UpdateTargetStepsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "OnboardingViewModel"

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val setCalorieTargetUseCase: SetCalorieTargetUseCase,
    private val setMacroTargetsUseCase: SetMacroTargetsUseCase,
    private val setWaterTargetUseCase: SetWaterTargetUseCase,
    private val updateTargetStepsUseCase: UpdateTargetStepsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<OnboardingNavigationEvent>()
    val navigationEvent: SharedFlow<OnboardingNavigationEvent> = _navigationEvent.asSharedFlow()

    fun updateCalorieTarget(calories: Int) {
        _state.update { it.copy(calorieTarget = calories) }
    }

    fun updateProteinTarget(protein: Int) {
        _state.update { it.copy(proteinTarget = protein) }
    }

    fun updateCarbsTarget(carbs: Int) {
        _state.update { it.copy(carbsTarget = carbs) }
    }

    fun updateFatTarget(fat: Int) {
        _state.update { it.copy(fatTarget = fat) }
    }

    fun updateWaterTarget(water: Int) {
        _state.update { it.copy(waterTarget = water) }
    }

    fun updateStepsTarget(steps: Int) {
        _state.update { it.copy(stepsTarget = steps) }
    }

    fun updateActivityLevel(level: ActivityLevel) {
        _state.update { it.copy(activityLevel = level) }
        calculateRecommendedTargets()
    }

    fun updateGoal(goal: FitnessGoal) {
        _state.update { it.copy(fitnessGoal = goal) }
        calculateRecommendedTargets()
    }

    fun updateAge(age: Int) {
        _state.update { it.copy(age = age) }
        calculateRecommendedTargets()
    }

    fun updateWeight(weight: Float) {
        _state.update { it.copy(weight = weight) }
        calculateRecommendedTargets()
    }

    fun updateHeight(height: Float) {
        _state.update { it.copy(height = height) }
        calculateRecommendedTargets()
    }

    fun updateGender(gender: Gender) {
        _state.update { it.copy(gender = gender) }
        calculateRecommendedTargets()
    }

    private fun calculateRecommendedTargets() {
        val currentState = _state.value

        // Calculate BMR (Basal Metabolic Rate) using Mifflin-St Jeor Equation
        val bmr = when (currentState.gender) {
            Gender.MALE -> (10 * currentState.weight) + (6.25 * currentState.height) - (5 * currentState.age) + 5
            Gender.FEMALE -> (10 * currentState.weight) + (6.25 * currentState.height) - (5 * currentState.age) - 161
            Gender.OTHER -> (10 * currentState.weight) + (6.25 * currentState.height) - (5 * currentState.age) - 78
        }

        // Apply activity multiplier
        val activityMultiplier = when (currentState.activityLevel) {
            ActivityLevel.SEDENTARY -> 1.2
            ActivityLevel.LIGHTLY_ACTIVE -> 1.375
            ActivityLevel.MODERATELY_ACTIVE -> 1.55
            ActivityLevel.VERY_ACTIVE -> 1.725
            ActivityLevel.EXTREMELY_ACTIVE -> 1.9
        }

        val tdee = bmr * activityMultiplier

        // Adjust based on fitness goal
        val calorieTarget = when (currentState.fitnessGoal) {
            FitnessGoal.LOSE_WEIGHT -> (tdee - 500).toInt()
            FitnessGoal.MAINTAIN_WEIGHT -> tdee.toInt()
            FitnessGoal.GAIN_MUSCLE -> (tdee + 300).toInt()
            FitnessGoal.IMPROVE_FITNESS -> tdee.toInt()
        }

        // Calculate macro targets
        val proteinGramsPerKg = when (currentState.fitnessGoal) {
            FitnessGoal.LOSE_WEIGHT -> 2.0
            FitnessGoal.MAINTAIN_WEIGHT -> 1.6
            FitnessGoal.GAIN_MUSCLE -> 2.2
            FitnessGoal.IMPROVE_FITNESS -> 1.8
        }

        val proteinTarget = (currentState.weight * proteinGramsPerKg).toInt()
        val proteinCalories = proteinTarget * 4

        val fatCalories = (calorieTarget * 0.25).toInt()
        val fatTarget = fatCalories / 9

        val carbsCalories = calorieTarget - proteinCalories - fatCalories
        val carbsTarget = carbsCalories / 4

        // Calculate water target (ml) based on weight
        val waterTarget = (currentState.weight * 35).toInt()

        // Calculate steps target based on activity level and goal
        val stepsTarget = when (currentState.activityLevel) {
            ActivityLevel.SEDENTARY -> 5000
            ActivityLevel.LIGHTLY_ACTIVE -> 7500
            ActivityLevel.MODERATELY_ACTIVE -> 10000
            ActivityLevel.VERY_ACTIVE -> 12500
            ActivityLevel.EXTREMELY_ACTIVE -> 15000
        }

        _state.update {
            it.copy(
                calorieTarget = calorieTarget,
                proteinTarget = proteinTarget,
                carbsTarget = carbsTarget,
                fatTarget = fatTarget,
                waterTarget = waterTarget,
                stepsTarget = stepsTarget
            )
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val currentState = _state.value

                Log.d(TAG, "Saving nutrition targets...")
                setCalorieTargetUseCase(currentState.calorieTarget).getOrThrow()
                setMacroTargetsUseCase(
                    currentState.carbsTarget,
                    currentState.proteinTarget,
                    currentState.fatTarget
                ).getOrThrow()
                setWaterTargetUseCase(currentState.waterTarget).getOrThrow()

                Log.d(TAG, "Saving steps target...")
                updateTargetStepsUseCase(currentState.stepsTarget)

                _state.update { it.copy(isLoading = false) }
                _navigationEvent.emit(OnboardingNavigationEvent.NavigateToHome)

                Log.d(TAG, "Onboarding completed successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error completing onboarding", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to save settings: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

data class OnboardingState(
    val age: Int = 25,
    val weight: Float = 70f,
    val height: Float = 170f,
    val gender: Gender = Gender.MALE,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATELY_ACTIVE,
    val fitnessGoal: FitnessGoal = FitnessGoal.MAINTAIN_WEIGHT,
    val calorieTarget: Int = 2400,
    val proteinTarget: Int = 160,
    val carbsTarget: Int = 300,
    val fatTarget: Int = 65,
    val waterTarget: Int = 2500,
    val stepsTarget: Int = 10000,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class Gender {
    MALE, FEMALE, OTHER
}

enum class ActivityLevel {
    SEDENTARY,
    LIGHTLY_ACTIVE,
    MODERATELY_ACTIVE,
    VERY_ACTIVE,
    EXTREMELY_ACTIVE
}

enum class FitnessGoal {
    LOSE_WEIGHT,
    MAINTAIN_WEIGHT,
    GAIN_MUSCLE,
    IMPROVE_FITNESS
}

sealed class OnboardingNavigationEvent {
    object NavigateToHome : OnboardingNavigationEvent()
}