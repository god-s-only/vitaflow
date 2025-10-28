package com.vitaflow.app.presentation.ui.features.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaflow.app.common.Resource
import com.vitaflow.app.domain.usecase.exercise.GetExerciseByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(savedStateHandle: SavedStateHandle, private val getExerciseByIdUseCase: GetExerciseByIdUseCase): ViewModel() {
    private val _state = MutableStateFlow(ExerciseDetailState())
    val state = _state.asStateFlow()

    init {
        savedStateHandle.get<String>("exerciseId")?.let { exerciseId ->
            getExerciseById(exerciseId)
        }
    }

    private fun getExerciseById(exerciseId: String) {
        _state.value  = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            when(val res = getExerciseByIdUseCase(exerciseId)){
                is Resource.Success ->{
                    _state.value = _state.value.copy(isLoading = false, exercise = res.data)
                }
                is Resource.Error ->{
                    _state.value = _state.value.copy(isLoading = false, error = res.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }

    }
}