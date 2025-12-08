package com.vitaflow.app.presentation.ui.features.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaflow.app.common.Resource
import com.vitaflow.app.common.UIEvent
import com.vitaflow.app.domain.usecase.exercise.GetBodyPartsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutBodyPartsViewModel @Inject constructor(private val getBodyPartsUseCase: GetBodyPartsUseCase): ViewModel() {
    private val _state = MutableStateFlow(WorkoutBodyPartsState())
    val state = _state
        .onStart { getBodyParts() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = WorkoutBodyPartsState()
        )

    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onEvent(event: WorkoutBodyPartsEvent){
        when(event){
            WorkoutBodyPartsEvent.PopBackStack -> {
                sendUIEvent(UIEvent.PopBackStack)
            }
        }
    }

    fun getBodyParts(){
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            when(val res = getBodyPartsUseCase()){
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            bodyParts = res.data ?: emptyList()
                        )
                    }
                }

                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = res.message ?: "An unexpected error occurred"
                        )
                    }
                }

                else -> {}
            }
        }
    }
    private fun sendUIEvent(event: UIEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}

sealed class WorkoutBodyPartsEvent {
    data object PopBackStack: WorkoutBodyPartsEvent()
}