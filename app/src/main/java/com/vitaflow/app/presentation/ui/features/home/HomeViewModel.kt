package com.vitaflow.app.presentation.ui.features.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaflow.app.common.Resource
import com.vitaflow.app.common.UIEvent
import com.vitaflow.app.domain.models.Exercise
import com.vitaflow.app.domain.models.FeaturedWorkout
import com.vitaflow.app.domain.models.QuickTraining
import com.vitaflow.app.domain.repository.AuthRepository
import com.vitaflow.app.domain.usecase.exercise.GetExerciseUseCase
import com.vitaflow.app.domain.usecase.workout.GetFeaturedWorkoutUseCase
import com.vitaflow.app.domain.usecase.workout.GetQuickTrainingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeViewModel"

data class HomeState(
    val userName: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val exercises: List<Exercise> = emptyList(),
    val featuredWorkout: FeaturedWorkout? = null,
    val isFeaturedWorkoutLoading: Boolean = false,
    val quickTrainings: List<QuickTraining> = emptyList(),
    val isQuickTrainingsLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getExerciseUseCase: GetExerciseUseCase,
    private val getFeaturedWorkoutUseCase: GetFeaturedWorkoutUseCase,
    private val getQuickTrainingsUseCase: GetQuickTrainingsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<UIEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        getCurrentUser()
        getExercises()
        getFeaturedWorkout()
        getQuickTrainings()
    }

    private fun getExercises() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val res = getExerciseUseCase()) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        exercises = res.data ?: emptyList(),
                        isLoading = false
                    )
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(error = res.message, isLoading = false)
                    sendUIEvent(UIEvent.ShowSnackBar(res.message ?: "Unknown error"))
                }

                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }

    private fun getFeaturedWorkout() {
        viewModelScope.launch {
            Log.d(TAG, "Fetching featured workout...")
            _state.value = _state.value.copy(isFeaturedWorkoutLoading = true)
            getFeaturedWorkoutUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.d(TAG, "Featured workout loaded: ${result.data?.title}")
                        _state.value = _state.value.copy(
                            featuredWorkout = result.data,
                            isFeaturedWorkoutLoading = false
                        )
                    }

                    is Resource.Error -> {
                        Log.e(TAG, "Featured workout error: ${result.message}")
                        _state.value = _state.value.copy(isFeaturedWorkoutLoading = false)
                        // Show fallback data when API fails
                        _state.value = _state.value.copy(
                            featuredWorkout = FeaturedWorkout(
                                id = "featured_fallback",
                                title = "Daily HIIT Challenge",
                                description = "High-intensity interval training for maximum results",
                                imageUrl = "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=800",
                                category = "HIIT",
                                difficulty = "Intermediate",
                                duration = 30,
                                calories = 400,
                                rating = 4.9,
                                exercises = emptyList()
                            ),
                            isFeaturedWorkoutLoading = false
                        )
                    }

                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isFeaturedWorkoutLoading = true)
                    }
                }
            }
        }
    }

    private fun getQuickTrainings() {
        viewModelScope.launch {
            Log.d(TAG, "Fetching quick trainings...")
            _state.value = _state.value.copy(isQuickTrainingsLoading = true)
            getQuickTrainingsUseCase(limit = 10).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.d(TAG, "Quick trainings loaded: ${result.data?.size} items")
                        if (result.data.isNullOrEmpty()) {
                            // Use fallback data if API returns empty
                            _state.value = _state.value.copy(
                                quickTrainings = getFallbackQuickTrainings(),
                                isQuickTrainingsLoading = false
                            )
                        } else {
                            _state.value = _state.value.copy(
                                quickTrainings = result.data,
                                isQuickTrainingsLoading = false
                            )
                        }
                    }

                    is Resource.Error -> {
                        Log.e(TAG, "Quick trainings error: ${result.message}")
                        // Use fallback data when API fails
                        _state.value = _state.value.copy(
                            quickTrainings = getFallbackQuickTrainings(),
                            isQuickTrainingsLoading = false
                        )
                    }

                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isQuickTrainingsLoading = true)
                    }
                }
            }
        }
    }

    private fun getFallbackQuickTrainings(): List<QuickTraining> {
        return listOf(
            QuickTraining(
                id = "qt_1",
                name = "Morning Energy Boost",
                description = "Quick cardio to start your day with energy",
                duration = 15,
                calories = 120,
                difficulty = "Beginner",
                emoji = "🌅",
                category = "Cardio",
                imageUrl = null,
                exerciseCount = 5
            ),
            QuickTraining(
                id = "qt_2",
                name = "Core Power Session",
                description = "Strengthen your core muscles for better stability",
                duration = 20,
                calories = 150,
                difficulty = "Intermediate",
                emoji = "💪",
                category = "Strength",
                imageUrl = null,
                exerciseCount = 6
            ),
            QuickTraining(
                id = "qt_3",
                name = "HIIT Fat Burner",
                description = "High intensity interval training for maximum burn",
                duration = 25,
                calories = 200,
                difficulty = "Advanced",
                emoji = "🔥",
                category = "HIIT",
                imageUrl = null,
                exerciseCount = 8
            ),
            QuickTraining(
                id = "qt_4",
                name = "Flexibility Flow",
                description = "Improve mobility and flexibility with yoga",
                duration = 18,
                calories = 80,
                difficulty = "All Levels",
                emoji = "🧘",
                category = "Yoga",
                imageUrl = null,
                exerciseCount = 7
            )
        )
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val user = authRepository.getCurrentUser()
                _state.value = _state.value.copy(
                    userName = user?.displayName ?: user?.email?.substringBefore("@") ?: "User",
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    userName = "User",
                    isLoading = false
                )
            }
        }
    }

    private fun sendUIEvent(event: UIEvent) {
        viewModelScope.launch {
            _navigationEvent.emit(event)
        }
    }
}

sealed class NavigationEvent {
    object NavigateToExerciseDetail : NavigationEvent()
}
