package com.vitaflow.app.presentation.ui.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {

    }

    private fun searchFoodProducts(query: String, apiKey: String){
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

                }
            }
        }

    }
}