package com.vitaflow.app.presentation.ui.features.search

sealed class UIEvent {
    data class ShowSnackBar(val message: String, val action: String? = null): UIEvent()
}