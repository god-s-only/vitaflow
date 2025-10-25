package com.vitaflow.app.presentation.ui.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaflow.app.common.Resource
import com.vitaflow.app.domain.models.Exercise
import com.vitaflow.app.domain.repository.AuthRepository
import com.vitaflow.app.domain.usecase.exercise.GetExerciseUseCase
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
    val exercises: List<Exercise> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val getExerciseUseCase: GetExerciseUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        getCurrentUser()
        getExercises()
    }


    private fun getExercises(){
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val res = getExerciseUseCase()
            when(res){
                is Resource.Success -> {
                    _state.value = _state.value.copy(exercises = res.data ?: emptyList(), isLoading = false)
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(error = res.message, isLoading = false)

                }

                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
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
}

sealed class NavigationEvent {
    object NavigateToExerciseDetail : NavigationEvent()
}