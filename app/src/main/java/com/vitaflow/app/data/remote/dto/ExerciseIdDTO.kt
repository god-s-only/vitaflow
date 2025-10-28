package com.vitaflow.app.data.remote.dto


import com.google.gson.annotations.SerializedName

data class ExerciseIdDTO(
    @SerializedName("data")
    val `data`: ExerciseIdRequest,
    @SerializedName("success")
    val success: Boolean
)