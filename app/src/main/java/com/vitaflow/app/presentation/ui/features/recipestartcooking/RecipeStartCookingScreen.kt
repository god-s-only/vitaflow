package com.vitaflow.app.presentation.ui.features.recipestartcooking

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vitaflow.app.domain.models.Ingredient
import com.vitaflow.app.presentation.ui.features.recipedetail.RecipeDetailViewModel

// Color Palette
object CookingColors {
    val Primary = Color(0xFF2D6A4F)
    val PrimaryLight = Color(0xFF40916C)
    val PrimaryLighter = Color(0xFF52B788)
    val Surface = Color(0xFFFAFAFA)
    val OnSurface = Color(0xFF1A1A1A)
    val OnSurfaceVariant = Color(0xFF6B6B6B)
    val CardBg = Color(0xFFFFFFFF)
    val CompletedBg = Color(0xFFE8F5E9)
    val AccentOrange = Color(0xFFFF9800)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeStartCookingScreen(
    navController: NavController,
    viewModel: RecipeStartCookingViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val recipe = state.value.data

    var currentIngredientIndex by remember { mutableStateOf(0) }
    var completedIngredients by remember { mutableStateOf(setOf<Int>()) }

    val ingredients = recipe?.extendedIngredients ?: emptyList()
    val totalIngredients = ingredients.size
    val progress = if (totalIngredients > 0) (currentIngredientIndex.toFloat() / totalIngredients) else 0f

    Scaffold(
        containerColor = CookingColors.Surface
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (ingredients.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Recipe",
                        modifier = Modifier.size(64.dp),
                        tint = CookingColors.OnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No ingredients found",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Top Bar with Progress
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { navController.popBackStack() },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(CookingColors.Surface, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = CookingColors.OnSurface
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = recipe?.title ?: "Recipe",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = CookingColors.OnSurface,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${currentIngredientIndex + 1} of $totalIngredients",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = CookingColors.OnSurfaceVariant
                                    )
                                }

                                IconButton(
                                    onClick = { /* More options */ },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(CookingColors.Surface, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "More",
                                        tint = CookingColors.OnSurface
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Progress Bar
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = CookingColors.Primary,
                                trackColor = CookingColors.Surface
                            )
                        }
                    }

                    // Main Content - Ingredient Display
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = currentIngredientIndex,
                            transitionSpec = {
                                if (targetState > initialState) {
                                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                                            slideOutHorizontally { width -> -width } + fadeOut()
                                } else {
                                    slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                            slideOutHorizontally { width -> width } + fadeOut()
                                }.using(SizeTransform(clip = false))
                            },
                            label = "ingredient_animation"
                        ) { index ->
                            if (index < ingredients.size) {
                                IngredientCard(
                                    ingredient = ingredients[index],
                                    isCompleted = completedIngredients.contains(index),
                                    onToggleComplete = {
                                        completedIngredients = if (completedIngredients.contains(index)) {
                                            completedIngredients - index
                                        } else {
                                            completedIngredients + index
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // Bottom Navigation
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Previous Button
                            OutlinedButton(
                                onClick = {
                                    if (currentIngredientIndex > 0) {
                                        currentIngredientIndex--
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                enabled = currentIngredientIndex > 0,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = CookingColors.Primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Previous",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Next/Finish Button
                            Button(
                                onClick = {
                                    if (currentIngredientIndex < totalIngredients - 1) {
                                        currentIngredientIndex++
                                    } else {
                                        // Navigate to completion or back
                                        navController.popBackStack()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CookingColors.Primary
                                )
                            ) {
                                Text(
                                    text = if (currentIngredientIndex < totalIngredients - 1) "Next" else "Finish",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = if (currentIngredientIndex < totalIngredients - 1)
                                        Icons.Default.ArrowForward
                                    else
                                        Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IngredientCard(
    ingredient: Ingredient,
    isCompleted: Boolean,
    onToggleComplete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) CookingColors.CompletedBg else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Check button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onToggleComplete,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (isCompleted) CookingColors.Primary else CookingColors.Surface,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.AccessTime,
                        contentDescription = "Mark complete",
                        tint = if (isCompleted) Color.White else CookingColors.OnSurfaceVariant,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Ingredient icon/placeholder
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                CookingColors.PrimaryLighter.copy(alpha = 0.2f),
                                CookingColors.Primary.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalDining,
                contentDescription = "Cooking",
                modifier = Modifier.size(60.dp),
                tint = CookingColors.Primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Ingredient name
            Text(
                text = ingredient.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = CookingColors.OnSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Amount card
            if (ingredient.amount > 0) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = CookingColors.Primary.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${ingredient.amount.toInt()}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = CookingColors.Primary,
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = ingredient.unit,
                            style = MaterialTheme.typography.titleLarge,
                            color = CookingColors.Primary,
                            fontSize = 24.sp
                        )
                    }
                }
            }

            // Original description
            if (ingredient.original.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))

                Divider(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .padding(vertical = 8.dp),
                    color = CookingColors.OnSurfaceVariant.copy(alpha = 0.2f)
                )

                Text(
                    text = ingredient.original,
                    style = MaterialTheme.typography.bodyLarge,
                    color = CookingColors.OnSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tips card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CookingColors.Surface
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = CookingColors.AccentOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Tap the circle above when you've gathered this ingredient",
                        style = MaterialTheme.typography.bodySmall,
                        color = CookingColors.OnSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun IngredientCardPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CookingColors.Surface)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        IngredientCard(
            ingredient = Ingredient(
                id = 1,
                name = "Butter",
                amount = 1.0,
                unit = "tbsp",
                original = "1 tbsp butter, softened",
                image = "butter.jpg"
            ),
            isCompleted = false,
            onToggleComplete = {}
        )
    }
}