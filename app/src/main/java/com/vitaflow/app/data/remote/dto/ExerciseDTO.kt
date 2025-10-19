package com.vitaflow.app.data.remote.dto


import com.google.gson.annotations.SerializedName

data class ExerciseDTO(
    @SerializedName("data")
    val `data`: List<ExerciseRequest>,
    @SerializedName("metadata")
    val metadata: Metadata,
    @SerializedName("success")
    val success: Boolean
)