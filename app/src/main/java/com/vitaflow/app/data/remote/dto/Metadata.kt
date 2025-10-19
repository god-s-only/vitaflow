package com.vitaflow.app.data.remote.dto


import com.google.gson.annotations.SerializedName

data class Metadata(
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("nextPage")
    val nextPage: String,
    @SerializedName("previousPage")
    val previousPage: Any,
    @SerializedName("totalExercises")
    val totalExercises: Int,
    @SerializedName("totalPages")
    val totalPages: Int
)