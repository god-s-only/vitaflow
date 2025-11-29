package com.vitaflow.app.presentation.ui.features.barcode

import com.vitaflow.app.domain.models.Food

sealed class BarcodeScanEvent {
    object NavigateBack : BarcodeScanEvent()
    data class ShowError(val message: String) : BarcodeScanEvent()
    data class ShowSuccess(val message: String) : BarcodeScanEvent()
}