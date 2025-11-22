package com.vitaflow.app.presentation.ui.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaflow.app.common.UIEvent
import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.usecase.nutrition.AddFoodUseCase
import com.vitaflow.app.domain.usecase.nutrition.GetNutritionFoodById
import com.vitaflow.app.domain.usecase.nutrition.GetNutritionFoodSpoonacular
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodSearchViewModel @Inject constructor(
    private val getNutritionFoodSpoonacular: GetNutritionFoodSpoonacular,
    private val getNutritionFoodById: GetNutritionFoodById,
    private val addFoodUseCase: AddFoodUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FoodSearchState())
    val state = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val apiKey = "e502be08e8b14c8290b76df779e11de1"
    private var searchJob: Job? = null


    private fun searchFoodProducts(query: String) {
        _state.value = _state.value.copy(loading = true)
        if (query.isBlank()) {
            _state.value = _state.value.copy(
                food = emptyList(),
                error = null,
                loading = false
            )
            return
        }
        viewModelScope.launch {
            getNutritionFoodSpoonacular(query, apiKey).collect { result ->
                when {
                    result.isSuccess -> {
                        _state.value = _state.value.copy(
                            food = result.getOrNull() ?: emptyList(),
                            error = null,
                            loading = false
                        )
                    }
                    result.isFailure -> {
                        _state.value = _state.value.copy(
                            food = emptyList(),
                            error = result.exceptionOrNull()?.message,
                            loading = false
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: FoodSearchEvent) {
        when (event) {
            is FoodSearchEvent.OnSearchChange -> {
                _state.value = _state.value.copy(query = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(300)
                    if (event.query.isNotBlank()) {
                        searchFoodProducts(event.query)
                    } else {
                        clearResults()
                    }
                }
            }

            is FoodSearchEvent.LoadNutritionDetails -> {
                loadNutritionDetails(event.productId)
            }

            is FoodSearchEvent.OnAddFood -> {
                viewModelScope.launch {
                    addFoodUseCase.invoke(
                        Food(
                            id = event.foodId,
                            name = event.name,
                            caloriesPer100g = event.calories,
                            carbsPer100g = event.carbs,
                            proteinPer100g = event.protein,
                            fatPer100g = event.fat,
                            imageUrl = null
                        )
                    )

                    _uiEvent.emit(
                        UIEvent.ShowQuantityDialog(
                            foodId = event.foodId,
                            foodName = event.name,
                            caloriesPer100g = event.calories,
                            carbsPer100g = event.carbs,
                            proteinPer100g = event.protein,
                            fatPer100g = event.fat
                        )
                    )
                }
            }
        }
    }

    private fun loadNutritionDetails(productId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                loadingDetailsFor = _state.value.loadingDetailsFor + productId
            )
            getNutritionFoodById(productId, apiKey).collect { result ->
                when {
                    result.isSuccess -> {
                        val detailedFood = result.getOrNull()
                        if (detailedFood != null) {
                            val updatedFoods = _state.value.food.map { food ->
                                if (food.id == productId) detailedFood else food
                            }
                            _state.value = _state.value.copy(
                                food = updatedFoods,
                                loadingDetailsFor = _state.value.loadingDetailsFor - productId
                            )
                        }
                    }
                    result.isFailure -> {
                        _state.value = _state.value.copy(
                            loadingDetailsFor = _state.value.loadingDetailsFor - productId,
                            error = "Failed to load details: ${result.exceptionOrNull()?.message}"
                        )
                    }
                }
            }
        }
    }

    private fun clearResults() {
        _state.value = _state.value.copy(
            food = emptyList(),
            error = null,
            loading = false
        )
    }
}