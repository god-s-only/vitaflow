package com.vitaflow.app.presentation.ui.features.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.vitaflow.app.domain.models.NutritionFood

// Theme Colors - Consistent with app theme
private val PrimaryGreen = Color(0xFF00C853)
private val LightGreen = Color(0xFF4CAF50)
private val BackgroundWhite = Color(0xFFFAFAFA)
private val CardWhite = Color.White
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF666666)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSearchScreen(
    navController: NavController,
    mealType: String = "breakfast",
    viewModel: FoodSearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val searchQuery by remember { derivedStateOf { state.query } }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardWhite,
                    titleContentColor = TextPrimary
                ),
                title = {
                    Text(
                        text = "Add to ${mealType.replaceFirstChar { it.uppercase() }}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryGreen
                        )
                    }
                }
            )
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { query ->
                    viewModel.onEvent(FoodSearchEvent.OnSearchChange(query))
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Show loading indicator
            if (state.loading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            }

            // Show error message
            state.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = error,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Results Count
            Text(
                text = "${state.food.size} foods found",
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Food List - Now using API data from viewModel
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.food) { nutritionFood ->
                    NutritionFoodCard(
                        nutritionFood = nutritionFood,
                        onFoodClick = {
                            // TODO: Pass selected food back to nutrition screen
                            // For now, just navigate back
                            navController.popBackStack()
                        }
                    )
                }

                if (state.food.isEmpty() && searchQuery.isNotEmpty() && !state.loading) {
                    item {
                        EmptySearchResult(searchQuery = searchQuery)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = {
            Text(
                text = "Search foods...",
                color = TextSecondary
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = PrimaryGreen
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryGreen,
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
            focusedLabelColor = PrimaryGreen,
            cursorColor = PrimaryGreen
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
private fun FoodItemCard(
    food: FoodItem,
    onFoodClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFoodClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = food.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = food.brand,
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NutrientInfo(label = "Cal", value = "${food.calories}")
                    NutrientInfo(label = "Carbs", value = "${food.carbs}g")
                    NutrientInfo(label = "Protein", value = "${food.protein}g")
                    NutrientInfo(label = "Fat", value = "${food.fat}g")
                }
            }

            // Serving size
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = food.servingSize,
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Icon(
                    painter = painterResource(id = android.R.drawable.ic_input_add),
                    contentDescription = "Add",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun NutrientInfo(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun EmptySearchResult(searchQuery: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🔍",
                fontSize = 48.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No foods found for \"$searchQuery\"",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Try searching with different keywords or check the spelling",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun NutritionFoodCard(
    nutritionFood: NutritionFood,
    onFoodClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFoodClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = nutritionFood.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "ID: ${nutritionFood.id}",
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NutrientInfo(label = "Cal", value = "---")
                    NutrientInfo(label = "Carbs", value = "---")
                    NutrientInfo(label = "Protein", value = "---")
                    NutrientInfo(label = "Fat", value = "---")
                }
            }

            // Add button
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Tap for details",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Icon(
                    painter = painterResource(id = android.R.drawable.ic_input_add),
                    contentDescription = "Add",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// Data class for food items
data class FoodItem(
    val id: String,
    val name: String,
    val brand: String,
    val calories: Int,
    val carbs: Int,
    val protein: Int,
    val fat: Int,
    val servingSize: String
)

@Preview
@Composable
fun FoodSearchScreenPreview() {
    FoodSearchScreen(navController = rememberNavController())
}