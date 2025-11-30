package com.vitaflow.app.data.remote.dto.recipes


import com.google.gson.annotations.SerializedName

data class Measures(
    @SerializedName("metric")
    val metric: Metric,
    @SerializedName("us")
    val us: Us
)