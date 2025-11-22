package com.vitaflow.app.common

sealed class UIEvent {
    data class ShowSnackBar(val message: String, val action: String? = null) : UIEvent()
    data class Navigate(val route: String) : UIEvent()
    data object PopBackStack: UIEvent()
    data class ShowQuantityDialog(
        val foodId: Int,
        val foodName: String,
        val caloriesPer100g: Double,
        val carbsPer100g: Double?,
        val proteinPer100g: Double,
        val fatPer100g: Double
    ) : UIEvent()
}