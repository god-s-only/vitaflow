package com.vitaflow.app.presentation.ui.features.recipestartcooking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaflow.app.domain.usecase.recipes.GetRecipeDetailUseCase
import com.vitaflow.app.presentation.ui.features.recipedetail.RecipeDetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeStartCookingViewModel @Inject constructor(
    private val getRecipeDetailUseCase: GetRecipeDetailUseCase,
    private val savedStateHandle: SavedStateHandle): ViewModel(){

    private val _state = MutableStateFlow(RecipeDetailState())
    val state = _state.asStateFlow()

    private val apiKey = "d2c131a9ef64479c80be1352a98a6028"

    init {
        savedStateHandle.get<String>("recipeId")?.let {
            getRecipesDetail(it)
        }
    }

    private fun getRecipesDetail(recipeId: String){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getRecipeDetailUseCase.invoke(recipeId.toInt(), apiKey)
                .catch { e ->
                    _state.update { it.copy(isLoading = false, data = null, error = e.message) }
                }
                .collectLatest { result ->
                    _state.update { it.copy(isLoading = false, data = result, error = null) }
                }
        }
    }
}