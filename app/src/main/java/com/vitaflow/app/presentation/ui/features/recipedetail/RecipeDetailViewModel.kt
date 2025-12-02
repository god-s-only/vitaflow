package com.vitaflow.app.presentation.ui.features.recipedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaflow.app.common.Routes
import com.vitaflow.app.common.UIEvent
import com.vitaflow.app.domain.usecase.recipes.GetRecipeDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val savedStateHandle: SavedStateHandle
    ): ViewModel() {
    private val _state = MutableStateFlow(RecipeDetailState())
    val state = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val apiKey = "e502be08e8b14c8290b76df779e11de1"

    init {
        savedStateHandle.get<String>("recipeId")?.let {
            getRecipesDetail(it)
        }
    }

    fun onEvent(event: RecipeDetailEvent){
        when(event){
            is RecipeDetailEvent.OnStartCooking -> {
                sendUIEvent(UIEvent.Navigate(Routes.RECIPE_START_COOKING + "/${_state.value.data?.id}"))
            }
        }
    }

    fun getRecipesDetail(recipeId: String){
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            getRecipeDetailUseCase.invoke(recipeId.toInt(), apiKey)
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            data = null,
                            error = e.message
                        )
                    }
                }
                .collectLatest { result ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            data = result,
                            error = null
                        )
                    }
                }
        }

    }


    private fun sendUIEvent(uiEvent: UIEvent){
        viewModelScope.launch {
            _uiEvent.emit(uiEvent)
        }
    }
}