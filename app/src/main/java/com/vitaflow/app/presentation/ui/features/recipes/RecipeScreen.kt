package com.vitaflow.app.presentation.ui.features.recipes

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.rememberAsyncImagePainter
import com.vitaflow.app.common.Routes
import com.vitaflow.app.common.UIEvent
import com.vitaflow.app.domain.models.RecipeModel
import kotlinx.coroutines.flow.collectLatest

// Color Palette
object RecipeColors {
    val Primary: Color @Composable get() = MaterialTheme.colorScheme.primary
    val PrimaryLight: Color @Composable get() = MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
    val Surface: Color @Composable get() = MaterialTheme.colorScheme.background
    val OnSurface: Color @Composable get() = MaterialTheme.colorScheme.onSurface
    val OnSurfaceVariant: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
    val NutritionBg: Color @Composable get() = MaterialTheme.colorScheme.primaryContainer
    val SkeletonBase: Color @Composable get() = MaterialTheme.colorScheme.surfaceVariant
    val SkeletonHighlight: Color @Composable get() = MaterialTheme.colorScheme.surface
}

@Composable
fun RecipeScreen(navController: NavController, viewModel: RecipesViewModel = hiltViewModel()) {
    var favorites by remember { mutableStateOf(setOf<Int>()) }
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { result ->
            when(result){
                is UIEvent.Navigate -> {
                    navController.navigate(result.route)
                }
                is UIEvent.ShowSnackBar -> {

                }
                else -> {}
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
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
                                text = if (state.value.recipes.isEmpty() && !state.value.isLoading)
                                    "Search for recipes"
                                else
                                    "${state.value.recipes.size} results",
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

                            BasicTextField(
                                value = state.value.query,
                                onValueChange = {query -> viewModel.onEvent(RecipesEvent.OnQueryChange(query = query))},
                                singleLine = true,
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        viewModel.onEvent(RecipesEvent.SearchRecipes(query = state.value.query))
                                    }
                                ),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = RecipeColors.OnSurfaceVariant
                                ),
                                modifier = Modifier
                                    .padding(start = 12.dp)
                                    .fillMaxWidth()
                            ) { innerTextField ->

                                if (state.value.query.isEmpty()) {
                                    Text(
                                        text = "Search recipes...",
                                        color = RecipeColors.OnSurfaceVariant,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }

                                innerTextField()
                            }
                        }
                    }
                }
            }

            // Content
            if (state.value.isLoading) {
                // Skeleton Loader
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(6) {
                        RecipeCardSkeleton()
                    }
                }
            } else if (state.value.error != null) {
                // Error State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Something went wrong",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = RecipeColors.OnSurface
                        )
                        Text(
                            text = state.value.error ?: "Unknown error",
                            style = MaterialTheme.typography.bodyMedium,
                            color = RecipeColors.OnSurfaceVariant
                        )
                    }
                }
            } else if (state.value.recipes.isEmpty() && state.value.query.isEmpty()) {
                // Empty State - No Search Yet
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = RecipeColors.OnSurfaceVariant,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Search for recipes",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = RecipeColors.OnSurface
                        )
                        Text(
                            text = "Enter ingredients or recipe names to find delicious meals",
                            style = MaterialTheme.typography.bodyMedium,
                            color = RecipeColors.OnSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else if (state.value.recipes.isEmpty()) {
                // Empty State - No Results
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    ) {
                        Text(
                            text = "No recipes found",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = RecipeColors.OnSurface
                        )
                        Text(
                            text = "Try searching with different keywords",
                            style = MaterialTheme.typography.bodyMedium,
                            color = RecipeColors.OnSurfaceVariant
                        )
                    }
                }
            } else {
                // Recipe List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.value.recipes) { recipe ->
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
                            onClick = { viewModel.onEvent(RecipesEvent.OnGotoRecipeDetail(id = recipe.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeCardSkeleton() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = RecipeColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Image Skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                RecipeColors.SkeletonBase,
                                RecipeColors.SkeletonHighlight,
                                RecipeColors.SkeletonBase
                            ),
                            start = Offset(translateAnim - 1000f, translateAnim - 1000f),
                            end = Offset(translateAnim, translateAnim)
                        )
                    )
            )

            // Content Skeleton
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Title Skeleton
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    RecipeColors.SkeletonBase,
                                    RecipeColors.SkeletonHighlight,
                                    RecipeColors.SkeletonBase
                                ),
                                start = Offset(translateAnim - 1000f, translateAnim - 1000f),
                                end = Offset(translateAnim, translateAnim)
                            )
                        )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    RecipeColors.SkeletonBase,
                                    RecipeColors.SkeletonHighlight,
                                    RecipeColors.SkeletonBase
                                ),
                                start = Offset(translateAnim - 1000f, translateAnim - 1000f),
                                end = Offset(translateAnim, translateAnim)
                            )
                        )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Nutrition Chips Skeleton
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            RecipeColors.SkeletonBase,
                                            RecipeColors.SkeletonHighlight,
                                            RecipeColors.SkeletonBase
                                        ),
                                        start = Offset(translateAnim - 1000f, translateAnim - 1000f),
                                        end = Offset(translateAnim, translateAnim)
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeCard(
    recipe: RecipeModel,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = RecipeColors.Surface),
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
    RecipeScreen(rememberNavController())
}

@Preview(showBackground = true)
@Composable
private fun SkeletonPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RecipeColors.Surface)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(3) {
            RecipeCardSkeleton()
        }
    }
}