package com.vitaflow.app.presentation.ui.features.barcode

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.usecase.nutrition.AddFoodEntryUseCase
import com.vitaflow.app.domain.usecase.nutrition.ScanBarcodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "BarcodeScanViewModel"



@HiltViewModel
class BarcodeScanViewModel @Inject constructor(private val scanBarcodeUseCase: ScanBarcodeUseCase, private val addFoodEntryUseCase: AddFoodEntryUseCase,): ViewModel() {
    private val _state = MutableStateFlow(BarcodeScanState())
    val state: StateFlow<BarcodeScanState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<BarcodeScanEvent>()
    val events: SharedFlow<BarcodeScanEvent> = _events.asSharedFlow()

    private val apiKey = "e502be08e8b14c8290b76df779e11de1"

    fun onBarcodeScanned(barcode: String) {
        // Prevent duplicate scans
        if (_state.value.lastScannedBarcode == barcode && _state.value.scannedProduct != null) {
            Log.d(TAG, "Duplicate barcode scan ignored: $barcode")
            return
        }

        viewModelScope.launch {
            Log.d(TAG, "Scanning barcode: $barcode")
            _state.update { it.copy(isLoading = true, error = null) }

            scanBarcodeUseCase(barcode, apiKey).collect { result ->
                result.fold(
                    onSuccess = { food ->
                        Log.d(TAG, "Product found: ${food.title}")
                        _state.update {
                            it.copy(
                                isLoading = false,
                                scannedProduct = food,
                                lastScannedBarcode = barcode,
                                error = null
                            )
                        }
                    },
                    onFailure = { error ->
                        Log.e(TAG, "Failed to scan barcode", error)
                        val errorMessage = when {
                            error is IllegalArgumentException -> error.message ?: "Invalid barcode"
                            error.message?.contains("404") == true -> "Product not found in database"
                            error.message?.contains("Network") == true -> "Network error. Check your connection"
                            error.message?.contains("API quota") == true -> "API limit reached"
                            else -> "Could not find product. Try another barcode."
                        }
                        _state.update {
                            it.copy(
                                isLoading = false,
                                scannedProduct = null,
                                error = errorMessage
                            )
                        }
                        _events.emit(BarcodeScanEvent.ShowError(errorMessage))
                    }
                )
            }
        }
    }

    fun onAddToMeal(mealType: String, quantity: Double = 100.0) {
        viewModelScope.launch {
            val product = _state.value.scannedProduct
            if (product == null) {
                _events.emit(BarcodeScanEvent.ShowError("No product scanned"))
                return@launch
            }

            _state.update { it.copy(isAddingToMeal = true) }
            Log.d(TAG, "Adding ${product.title} to $mealType with quantity $quantity")

            // Convert NutritionFood to Food entity
            val food = Food(
                id = product.id,
                name = product.title,
                caloriesPer100g = product.calories ?: 0.0,
                carbsPer100g = product.carbs,
                proteinPer100g = product.protein ?: 0.0,
                fatPer100g = product.fat ?: 0.0,
                imageUrl = null
            )

            addFoodEntryUseCase(food, mealType, quantity).fold(
                onSuccess = {
                    Log.d(TAG, "Food added successfully")
                    _state.update { it.copy(isAddingToMeal = false) }
                    _events.emit(
                        BarcodeScanEvent.ShowSuccess(
                            "${product.title} added to $mealType"
                        )
                    )
                    // Navigate back after success
                    _events.emit(BarcodeScanEvent.NavigateBack)
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to add food", error)
                    _state.update { it.copy(isAddingToMeal = false) }
                    _events.emit(
                        BarcodeScanEvent.ShowError(
                            "Failed to add food: ${error.message}"
                        )
                    )
                }
            )
        }
    }

    fun onDismiss() {
        viewModelScope.launch {
            _events.emit(BarcodeScanEvent.NavigateBack)
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun resetScan() {
        _state.update { BarcodeScanState() }
    }
}