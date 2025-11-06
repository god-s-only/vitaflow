package com.vitaflow.app.data.remote.dto


import com.google.gson.annotations.SerializedName

data class NutritionFoodDTO(
    @SerializedName("number")
    val number: Int,
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("processingTimeMs")
    val processingTimeMs: Int,
    @SerializedName("products")
    val nutritionFoodResponses: List<NutritionFoodResponse>,
    @SerializedName("totalProducts")
    val totalProducts: Int,
    @SerializedName("type")
    val type: String
)