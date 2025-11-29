package com.vitaflow.app.data.remote.dto.recipes


import com.google.gson.annotations.SerializedName

data class RecipeDTO(
    @SerializedName("number")
    val number: Int,
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("results")
    val results: List<Result>,
    @SerializedName("totalResults")
    val totalResults: Int
)