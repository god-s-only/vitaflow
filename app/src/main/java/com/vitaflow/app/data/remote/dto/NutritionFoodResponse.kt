package com.vitaflow.app.data.remote.dto


import com.google.gson.annotations.SerializedName

data class NutritionFoodResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("imageType")
    val imageType: String,
    @SerializedName("title")
    val title: String
)