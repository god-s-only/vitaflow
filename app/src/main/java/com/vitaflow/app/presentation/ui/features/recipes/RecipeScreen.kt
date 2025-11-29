package com.vitaflow.app.presentation.ui.features.recipes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter

// Color Palette
object RecipeColors {
    val Primary = Color(0xFF2D6A4F) // Deep forest green
    val PrimaryLight = Color(0xFF40916C) // Leaf green
    val Surface = Color(0xFFFAFAFA)
    val OnSurface = Color(0xFF1A1A1A)
    val OnSurfaceVariant = Color(0xFF6B6B6B)
    val NutritionBg = Color(0xFFE8F5E9)
}

data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val fat: Double,
    val calories: Int = 420,
    val protein: Double = 18.5,
    val carbs: Double = 45.2
)

@Composable
fun RecipeScreen() {
    val recipes = listOf(
        Recipe(
            id = 654959,
            title = "Pasta With Tuna",
            image = "https://img.spoonacular.com/recipes/654959-312x231.jpg",
            fat = 10.3,
            calories = 450,
            protein = 22.0,
            carbs = 52.0
        ),
        Recipe(
            id = 648279,
            title = "Italian Tuna Pasta",
            image = "https://img.spoonacular.com/recipes/648279-312x231.jpg",
            fat = 2.9,
            calories = 380,
            protein = 20.5,
            carbs = 48.0
        )
    )

    var favorites by remember { mutableStateOf(setOf<Int>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RecipeColors.Surface)
    ) {
        // Top Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Recipes",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = RecipeColors.OnSurface,
                            fontSize = 32.sp
                        )
                        Text(
                            text = "130 healthy options",
                            style = MaterialTheme.typography.bodyMedium,
                            color = RecipeColors.OnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    IconButton(
                        onClick = { /* Filter action */ },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                RecipeColors.NutritionBg,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.List,
                            contentDescription = "Filter",
                            tint = RecipeColors.Primary
                        )
                    }
                }

                // Search Bar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = RecipeColors.Surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = RecipeColors.OnSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Search recipes...",
                            color = RecipeColors.OnSurfaceVariant,
                            modifier = Modifier.padding(start = 12.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        // Recipe List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(recipes) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    isFavorite = favorites.contains(recipe.id),
                    onFavoriteClick = {
                        favorites = if (favorites.contains(recipe.id)) {
                            favorites - recipe.id
                        } else {
                            favorites + recipe.id
                        }
                    },
                    onClick = { /* Navigate to detail */ }
                )
            }
        }
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Image with Gradient Overlay and Favorite Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(recipe.image),
                    contentDescription = recipe.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f)
                                ),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )

                // Favorite button
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(40.dp)
                        .background(
                            Color.White.copy(alpha = 0.9f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFE53935) else RecipeColors.OnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = RecipeColors.OnSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Nutrition Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NutritionChip(
                        label = "Cal",
                        value = "${recipe.calories}",
                        modifier = Modifier.weight(1f)
                    )
                    NutritionChip(
                        label = "Protein",
                        value = "${recipe.protein}g",
                        modifier = Modifier.weight(1f)
                    )
                    NutritionChip(
                        label = "Fat",
                        value = "${recipe.fat}g",
                        modifier = Modifier.weight(1f),
                        highlighted = true
                    )
                }
            }
        }
    }
}

@Composable
fun NutritionChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = if (highlighted) RecipeColors.NutritionBg else RecipeColors.Surface
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = RecipeColors.OnSurfaceVariant,
                fontSize = 10.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (highlighted) RecipeColors.Primary else RecipeColors.OnSurface,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    RecipeScreen()
}