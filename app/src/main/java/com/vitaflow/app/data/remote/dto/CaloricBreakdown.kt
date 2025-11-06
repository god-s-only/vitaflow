package com.vitaflow.app.data.remote.dto


import com.google.gson.annotations.SerializedName

data class CaloricBreakdown(
    @SerializedName("percentCarbs")
    val percentCarbs: Double,
    @SerializedName("percentFat")
    val percentFat: Int,
    @SerializedName("percentProtein")
    val percentProtein: Double
)