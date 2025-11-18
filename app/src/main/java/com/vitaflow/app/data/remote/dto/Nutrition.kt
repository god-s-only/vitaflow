package com.vitaflow.app.data.remote.dto


import com.google.gson.annotations.SerializedName

data class Nutrition(
    @SerializedName("caloricBreakdown")
    val caloricBreakdown: CaloricBreakdown,
    @SerializedName("calories")
    val calories: Double,
    @SerializedName("carbs")
    val carbs: String,
    @SerializedName("fat")
    val fat: String,
    @SerializedName("nutrients")
    val nutrients: List<Nutrient>,
    @SerializedName("protein")
    val protein: String
)