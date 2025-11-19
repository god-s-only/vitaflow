package com.vitaflow.app.common

sealed class UIEvent {
    data class ShowSnackBar(val message: String, val action: String? = null) : UIEvent()
    data class Navigate(val route: String) : UIEvent()
    data object PopBackStack: UIEvent()
}