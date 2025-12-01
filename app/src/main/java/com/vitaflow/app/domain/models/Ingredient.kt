package com.vitaflow.app.domain.models

data class Ingredient(
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String,
    val original: String,
    val image: String
)
