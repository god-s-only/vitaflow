package com.vitaflow.app.presentation.ui.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaflow.app.BuildConfig
import com.vitaflow.app.domain.usecase.nutrition.GetNutritionFoodSpoonacular
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodSearchViewModel @Inject constructor(private val getNutritionFoodSpoonacular: GetNutritionFoodSpoonacular): ViewModel() {
    private val _state = MutableStateFlow(FoodSearchState())
    val state = _state.asStateFlow()

    private val apiKey = BuildConfig.SPOONACULAR_API_KEY


    private fun searchFoodProducts(query: String){
        _state.value = _state.value.copy(loading = true)
        if(query.isBlank()){
            _state.value = _state.value.copy(
                food = emptyList(),
                error = null,
                loading = false
            )
            return
        }
        viewModelScope.launch {
            getNutritionFoodSpoonacular(query, apiKey).collect { result ->
                when{
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

    fun onEvent(event: FoodSearchEvent){
        when(event){
            is FoodSearchEvent.OnSearchChange -> {
                searchFoodProducts(event.query)
            }
        }
    }
}