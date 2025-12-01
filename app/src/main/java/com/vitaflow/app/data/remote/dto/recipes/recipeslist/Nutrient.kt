package com.vitaflow.app.data.remote.dto.recipes.recipeslist


import com.google.gson.annotations.SerializedName

data class Nutrient(
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("name")
    val name: String,
    @SerializedName("unit")
    val unit: String
)