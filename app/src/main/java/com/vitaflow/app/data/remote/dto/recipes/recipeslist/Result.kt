package com.vitaflow.app.data.remote.dto.recipes.recipeslist


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("imageType")
    val imageType: String,
    @SerializedName("nutrition")
    val nutrition: Nutrition,
    @SerializedName("title")
    val title: String
)