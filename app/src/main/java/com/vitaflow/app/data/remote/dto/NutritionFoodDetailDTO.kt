package com.vitaflow.app.data.remote.dto


import com.google.gson.annotations.SerializedName

data class NutritionFoodDetailDTO(
    @SerializedName("aisle")
    val aisle: Any,
    @SerializedName("badges")
    val badges: List<String>,
    @SerializedName("brand")
    val brand: String,
    @SerializedName("breadcrumbs")
    val breadcrumbs: List<String>,
    @SerializedName("category")
    val category: String,
    @SerializedName("credits")
    val credits: Credits,
    @SerializedName("description")
    val description: String,
    @SerializedName("generatedText")
    val generatedText: Any,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("imageType")
    val imageType: String,
    @SerializedName("images")
    val images: List<String>,
    @SerializedName("importantBadges")
    val importantBadges: List<String>,
    @SerializedName("ingredientCount")
    val ingredientCount: Int,
    @SerializedName("ingredientList")
    val ingredientList: String,
    @SerializedName("ingredients")
    val ingredients: List<Ingredient>,
    @SerializedName("likes")
    val likes: Int,
    @SerializedName("nutrition")
    val nutrition: Nutrition,
    @SerializedName("price")
    val price: Double,
    @SerializedName("servings")
    val servings: Servings,
    @SerializedName("spoonacularScore")
    val spoonacularScore: Double,
    @SerializedName("title")
    val title: String,
    @SerializedName("upc")
    val upc: String,
    @SerializedName("usdaCode")
    val usdaCode: Any
)