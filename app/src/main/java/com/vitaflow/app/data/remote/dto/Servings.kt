package com.vitaflow.app.data.remote.dto


import com.google.gson.annotations.SerializedName

data class Servings(
    @SerializedName("number")
    val number: Int,
    @SerializedName("raw")
    val raw: String,
    @SerializedName("size")
    val size: Double,
    @SerializedName("unit")
    val unit: String
)