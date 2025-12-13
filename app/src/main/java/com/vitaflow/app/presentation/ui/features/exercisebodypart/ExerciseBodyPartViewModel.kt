package com.vitaflow.app.presentation.ui.features.exercisebodypart

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaflow.app.common.Resource
import com.vitaflow.app.common.UIEvent
import com.vitaflow.app.domain.usecase.exercise.GetWorkoutByBodyPart
import com.vitaflow.app.presentation.ui.features.workout.WorkoutBodyPartsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseBodyPartViewModel @Inject constructor(private val getWorkoutByBodyPart: GetWorkoutByBodyPart, savedStateHandle: SavedStateHandle): ViewModel() {
    private val _state = MutableStateFlow(ExerciseBodyPartState())
    val state = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = WorkoutBodyPartsState()
        )
    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        savedStateHandle.get<String>("bodyPart")?.let {
            getWorkoutByBodyPart(it)
        }
    }

    fun getWorkoutByBodyPart(bodyPart: String){
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }
            when(val response = getWorkoutByBodyPart.invoke(bodyPart)){
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            data = response.data!!,
                            error = null
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = response.message,
                            data = emptyList()
                        )
                    }
                }
                else -> {

                }
            }
        }
    }


    private fun sendUIEvent(event: UIEvent){
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}