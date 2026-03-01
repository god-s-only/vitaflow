package com.vitaflow.app.presentation.ui.features.home

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
            _state.value = _state.value.copy(isFeaturedWorkoutLoading = true)
            getFeaturedWorkoutUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            featuredWorkout = result.data,
                            isFeaturedWorkoutLoading = false
                        )
                    }

                    is Resource.Error -> {
                        _state.value = _state.value.copy(isFeaturedWorkoutLoading = false)
                        // Don't show snackbar for featured workout to avoid spamming
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
            _state.value = _state.value.copy(isQuickTrainingsLoading = true)
            getQuickTrainingsUseCase(limit = 10).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            quickTrainings = result.data ?: emptyList(),
                            isQuickTrainingsLoading = false
                        )
                    }

                    is Resource.Error -> {
                        _state.value = _state.value.copy(isQuickTrainingsLoading = false)
                        // Don't show snackbar for quick trainings to avoid spamming
                    }

                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isQuickTrainingsLoading = true)
                    }
                }
            }
        }
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
