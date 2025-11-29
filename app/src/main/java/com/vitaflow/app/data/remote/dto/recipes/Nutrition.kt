package com.vitaflow.app.data.remote.dto.recipes


import com.google.gson.annotations.SerializedName

data class Nutrition(
    @SerializedName("nutrients")
    val nutrients: List<Nutrient>
)