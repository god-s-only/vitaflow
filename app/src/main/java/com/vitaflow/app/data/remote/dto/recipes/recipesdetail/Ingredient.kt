package com.vitaflow.app.data.remote.dto.recipes.recipesdetail


import com.google.gson.annotations.SerializedName

data class Ingredient(
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("nutrients")
    val nutrients: List<NutrientX>,
    @SerializedName("unit")
    val unit: String
)