package com.vitaflow.app.presentation.ui.features.barcode

import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.models.NutritionFood

data class BarcodeScanState(
    val isLoading: Boolean = false,
    val scannedProduct: NutritionFood? = null,
    val error: String? = null,
    val lastScannedBarcode: String? = null,
    val isAddingToMeal: Boolean = false
)