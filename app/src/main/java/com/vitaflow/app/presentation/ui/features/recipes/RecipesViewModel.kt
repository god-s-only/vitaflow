package com.vitaflow.app.presentation.ui.features.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.query
import com.vitaflow.app.common.UIEvent
import com.vitaflow.app.domain.usecase.recipes.SearchRecipesUseCase
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
class RecipesViewModel @Inject constructor(private val searchRecipesUseCase: SearchRecipesUseCase): ViewModel() {
    private val _state = MutableStateFlow(RecipesState())
    val state = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val apiKey = "e502be08e8b14c8290b76df779e11de1"

    fun onEvent(event: RecipesEvent){
        when(event){
            is RecipesEvent.OnQueryChange -> {
                _state.update { it.copy(query = event.query) }
            }

            is RecipesEvent.SearchRecipes -> {
                searchRecipes(query = event.query)
            }
        }
    }
    private fun searchRecipes(query: String){
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            if(query.isEmpty()){
                _state.update {
                    it.copy(isLoading = false, recipes = emptyList())
                }
                return@launch
            }
            searchRecipesUseCase.invoke(query = query, apiKey = apiKey)
                .catch { e ->
                    _state.update { it.copy(isLoading = false, recipes = emptyList(), error = e.message) }
                }
                .collectLatest { recipes ->
                    _state.update {
                        it.copy(isLoading = false, recipes = recipes, error = null)
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