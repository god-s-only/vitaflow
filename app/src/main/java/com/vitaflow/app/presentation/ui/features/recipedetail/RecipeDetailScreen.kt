package com.vitaflow.app.presentation.ui.features.recipedetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter

// Color Palette
object RecipeColors {
    val Primary = Color(0xFF2D6A4F)
    val PrimaryLight = Color(0xFF40916C)
    val Surface = Color(0xFFFAFAFA)
    val OnSurface = Color(0xFF1A1A1A)
    val OnSurfaceVariant = Color(0xFF6B6B6B)
    val CardBg = Color(0xFFFFFFFF)
    val NutritionBg = Color(0xFFE8F5E9)
    val AccentOrange = Color(0xFFFF9800)
    val AccentRed = Color(0xFFE53935)
}

data class Ingredient(
    val id: Int,
    val name: String,
    val amount: Double,
    val unit: String,
    val original: String,
    val image: String
)

data class RecipeDetail(
    val id: Int,
    val title: String,
    val image: String,
    val readyInMinutes: Int,
    val servings: Int,
    val sourceUrl: String,
    val preparationMinutes: Int,
    val cookingMinutes: Int,
    val aggregateLikes: Int,
    val healthScore: Int,
    val sourceName: String,
    val pricePerServing: Double,
    val extendedIngredients: List<Ingredient>,
    val summary: String,
    val dishTypes: List<String>,
    val spoonacularScore: Double,
    val calories: Int = 543,
    val protein: Int = 17,
    val fat: Int = 16,
    val carbs: Int = 65
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipe: RecipeDetail,
    onBackClick: () -> Unit = {},
    onStartCooking: () -> Unit = {}
) {
    var isFavorite by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(RecipeColors.Surface)
        ) {
            // Hero Image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
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
                                        Color.Black.copy(alpha = 0.3f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.7f)
                                    )
                                )
                            )
                    )

                    // Back button
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = RecipeColors.OnSurface
                        )
                    }

                    // Favorite button
                    IconButton(
                        onClick = { isFavorite = !isFavorite },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) RecipeColors.AccentRed else RecipeColors.OnSurface
                        )
                    }
                }
            }

            // Title Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(20.dp)
                ) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = RecipeColors.OnSurface,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = RecipeColors.AccentOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${recipe.spoonacularScore.toInt()}% Score",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = RecipeColors.OnSurface
                        )
                        Text(
                            text = "• ${recipe.aggregateLikes} likes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = RecipeColors.OnSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "by ${recipe.sourceName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = RecipeColors.OnSurfaceVariant
                    )
                }
            }

            // Quick Stats
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickStatCard(
                        icon = Icons.Default.Settings,
                        label = "Total",
                        value = "${recipe.readyInMinutes} min",
                        modifier = Modifier.weight(1f)
                    )
                    QuickStatCard(
                        icon = Icons.Default.Settings,
                        label = "Servings",
                        value = "${recipe.servings}",
                        modifier = Modifier.weight(1f)
                    )
                    QuickStatCard(
                        icon = Icons.Default.Settings,
                        label = "Calories",
                        value = "${recipe.calories}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Nutrition Info
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = RecipeColors.NutritionBg
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        NutritionItem("Protein", "${recipe.protein}g")
                        Divider(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp),
                            color = RecipeColors.Primary.copy(alpha = 0.2f)
                        )
                        NutritionItem("Fat", "${recipe.fat}g")
                        Divider(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp),
                            color = RecipeColors.Primary.copy(alpha = 0.2f)
                        )
                        NutritionItem("Carbs", "${recipe.carbs}g")
                        Divider(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp),
                            color = RecipeColors.Primary.copy(alpha = 0.2f)
                        )
                        NutritionItem("Price", "${"%.2f".format(recipe.pricePerServing / 100)}")
                    }
                }
            }

            // Time Breakdown
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TimeChip(
                        label = "Prep",
                        time = "${recipe.preparationMinutes} min",
                        modifier = Modifier.weight(1f)
                    )
                    TimeChip(
                        label = "Cook",
                        time = "${recipe.cookingMinutes} min",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Tabs
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    containerColor = RecipeColors.Surface,
                    indicator = { tabPositions ->
                        Box(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .height(4.dp)
                                .padding(horizontal = 20.dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(RecipeColors.Primary)
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                "Ingredients",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                "Overview",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Tab Content
            if (selectedTab == 0) {
                // Ingredients List
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Text(
                            text = "${recipe.extendedIngredients.size} ingredients",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = RecipeColors.OnSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }

                items(recipe.extendedIngredients) { ingredient ->
                    IngredientItem(ingredient)
                }
            } else {
                // Overview
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "About this recipe",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = RecipeColors.OnSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Text(
                            text = recipe.summary
                                .replace("<b>", "")
                                .replace("</b>", "")
                                .replace(Regex("<a[^>]*>"), "")
                                .replace("</a>", "")
                                .split(". ")
                                .take(4)
                                .joinToString(". ") + ".",
                            style = MaterialTheme.typography.bodyLarge,
                            color = RecipeColors.OnSurfaceVariant,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Dietary Tags
                        if (recipe.dishTypes.isNotEmpty()) {
                            Text(
                                text = "Dish Types",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = RecipeColors.OnSurface,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                recipe.dishTypes.take(3).forEach { dishType ->
                                    DietaryTag(dishType.replaceFirstChar { it.uppercase() })
                                }
                            }
                        }

                        Text(
                            text = "Health Score: ${recipe.healthScore}/100",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = RecipeColors.OnSurface
                        )
                    }
                }
            }

            // Bottom Spacing
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onStartCooking,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .fillMaxWidth(0.9f)
                .height(56.dp),
            containerColor = RecipeColors.Primary,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Start Cooking",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun QuickStatCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = RecipeColors.Surface
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = RecipeColors.Primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = RecipeColors.OnSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = RecipeColors.OnSurfaceVariant
            )
        }
    }
}

@Composable
fun NutritionItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = RecipeColors.Primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = RecipeColors.OnSurfaceVariant
        )
    }
}

@Composable
fun TimeChip(label: String, time: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = RecipeColors.PrimaryLight,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = RecipeColors.OnSurfaceVariant
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = RecipeColors.OnSurface
                )
            }
        }
    }
}

@Composable
fun IngredientItem(ingredient: Ingredient) {
    var isChecked by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isChecked) RecipeColors.NutritionBg else Color.White,
        tonalElevation = if (isChecked) 0.dp else 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = RecipeColors.Primary,
                    uncheckedColor = RecipeColors.OnSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ingredient.name.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (isChecked)
                        RecipeColors.OnSurfaceVariant
                    else
                        RecipeColors.OnSurface
                )
                if (ingredient.original.isNotEmpty()) {
                    Text(
                        text = ingredient.original,
                        style = MaterialTheme.typography.bodySmall,
                        color = RecipeColors.OnSurfaceVariant
                    )
                }
            }

            if (ingredient.amount > 0) {
                Text(
                    text = "${ingredient.amount.toInt()} ${ingredient.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = RecipeColors.Primary,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
fun DietaryTag(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = RecipeColors.Surface
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = RecipeColors.Primary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    val sampleRecipe = RecipeDetail(
        id = 716429,
        title = "Pasta with Garlic, Scallions, Cauliflower & Breadcrumbs",
        image = "https://img.spoonacular.com/recipes/716429-556x370.jpg",
        readyInMinutes = 45,
        servings = 2,
        sourceUrl = "https://fullbellysisters.blogspot.com/2012/06/pasta-with-garlic-scallions-cauliflower.html",
        preparationMinutes = 20,
        cookingMinutes = 25,
        aggregateLikes = 209,
        healthScore = 18,
        sourceName = "Full Belly Sisters",
        pricePerServing = 157.06,
        extendedIngredients = listOf(
            Ingredient(1001, "butter", 1.0, "tbsp", "1 tbsp butter", "butter-sliced.jpg"),
            Ingredient(10011135, "cauliflower florets", 2.0, "cups", "2 cups frozen cauliflower florets, thawed", "cauliflower.jpg")
        ),
        summary = "You can never have too many main course recipes, so give Pasta with Garlic, Scallions, Cauliflower & Breadcrumbs a try. One serving contains 543 calories, 17g of protein, and 16g of fat.",
        dishTypes = listOf("side dish", "lunch", "main course", "main dish", "dinner"),
        spoonacularScore = 83.97
    )

    RecipeDetailScreen(recipe = sampleRecipe)
}