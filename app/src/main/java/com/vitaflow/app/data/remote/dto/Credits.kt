package com.vitaflow.app.data.remote.dto


import com.google.gson.annotations.SerializedName

data class Credits(
    @SerializedName("image")
    val image: String,
    @SerializedName("imageLink")
    val imageLink: String,
    @SerializedName("link")
    val link: String,
    @SerializedName("text")
    val text: String
)