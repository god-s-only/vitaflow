package com.vitaflow.app.data.remote.dto.recipes.recipeslist


import com.google.gson.annotations.SerializedName

data class Nutrition(
    @SerializedName("nutrients")
    val nutrients: List<Nutrient>
)