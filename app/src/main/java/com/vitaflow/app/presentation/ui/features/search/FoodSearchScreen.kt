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
import com.vitaflow.app.common.UIEvent
import com.vitaflow.app.domain.models.NutritionFood
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Theme Colors - Consistent with app theme
private val PrimaryGreen = Color(0xFF00C853)
private val LightGreen = Color(0xFF4CAF50)
private val BackgroundWhite = Color(0xFFFAFAFA)
private val CardWhite = Color.White
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF666666)

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

val sampleFoods = listOf(
    FoodItem("1", "Banana", "Fresh", 105, 27, 1, 0, "1 medium"),
    FoodItem("2", "Chicken Breast", "Fresh", 165, 0, 31, 4, "100g"),
    FoodItem("3", "Brown Rice", "Generic", 216, 45, 5, 2, "1 cup cooked"),
    FoodItem("4", "Greek Yogurt", "Chobani", 100, 6, 18, 0, "170g container"),
    FoodItem("5", "Oatmeal", "Quaker", 150, 27, 5, 3, "1/2 cup dry"),
    FoodItem("6", "Eggs", "Large", 70, 0, 6, 5, "1 large"),
    FoodItem("7", "Almonds", "Raw", 160, 6, 6, 14, "28g (23 nuts)"),
    FoodItem("8", "Sweet Potato", "Fresh", 112, 26, 2, 0, "1 medium"),
    FoodItem("9", "Salmon", "Atlantic", 206, 0, 22, 12, "100g"),
    FoodItem("10", "Spinach", "Fresh", 7, 1, 1, 0, "1 cup"),
    FoodItem("11", "Apple", "Medium", 95, 25, 0, 0, "1 medium"),
    FoodItem("12", "Avocado", "Fresh", 160, 9, 2, 15, "1/2 medium"),
    FoodItem("13", "Protein Powder", "Whey", 120, 2, 24, 1, "1 scoop"),
    FoodItem("14", "Peanut Butter", "Jif", 190, 8, 8, 16, "2 tbsp"),
    FoodItem("15", "Bread", "Whole Wheat", 80, 14, 4, 1, "1 slice")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSearchScreen(
    navController: NavController,
    mealType: String = "breakfast",
    viewModel: FoodSearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val searchQuery by remember { derivedStateOf { state.query } }
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { result ->
            when(result){
                is UIEvent.ShowSnackBar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(message = result.message)
                    }
                }

                is UIEvent.Navigate -> TODO()
                UIEvent.PopBackStack -> {
                    navController.popBackStack()
                }
            }
        }
    }



    // Decide what to show: sample foods or API results
    val showSampleFoods = searchQuery.isEmpty()
    val displayFoods = if (showSampleFoods) sampleFoods else emptyList()
    val displayApiResults = if (!showSampleFoods) state.food else emptyList()

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
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

            // Show loading indicator only when searching (not for sample foods)
            if (state.loading && !showSampleFoods) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            }

            // Show error message only when searching
            if (!showSampleFoods) {
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
            }

            val totalCount = if (showSampleFoods) displayFoods.size else displayApiResults.size
            val resultType = if (showSampleFoods) "sample foods" else "foods found"

            Text(
                text = "$totalCount $resultType",
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showSampleFoods) {
                    items(displayFoods) { food ->
                        FoodItemCard(
                            food = food,
                            onFoodClick = {
                                navController.popBackStack()
                            }
                        )
                    }
                } else {
                    // Show API results when searching
                    items(displayApiResults) { nutritionFood ->
                        NutritionFoodCard(
                            nutritionFood = nutritionFood,
                            onFoodClick = {
                                viewModel.onEvent(
                                    FoodSearchEvent.OnAddFood(
                                    name = nutritionFood.title,
                                    calories = nutritionFood.calories ?: 0.0,
                                    carbs = nutritionFood.carbs ?: 0.0,
                                    protein = nutritionFood.protein ?: 0.0,
                                    fat = nutritionFood.fat ?: 0.0
                                )
                                )
                            },
                            onLoadDetails = {
                                viewModel.onEvent(FoodSearchEvent.LoadNutritionDetails(it))
                            }
                        )
                    }

                    // Show empty state only when search returns no results
                    if (displayApiResults.isEmpty() && searchQuery.isNotEmpty() && !state.loading) {
                        item {
                            EmptySearchResult(searchQuery = searchQuery)
                        }
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
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier
                    .size(25.dp)
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
    onFoodClick: (NutritionFood) -> Unit,
    onLoadDetails: (Int) -> Unit
) {
    LaunchedEffect(nutritionFood.id) {
        if(nutritionFood.calories == null && !nutritionFood.isLoadingDetails){
            onLoadDetails(nutritionFood.id)
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFoodClick(nutritionFood) },
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

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (nutritionFood.isLoadingDetails) {
                        repeat(4) {
                            NutrientInfo(label = "...", value = "⏳")
                        }
                    } else {
                        NutrientInfo(label = "Cal", value = nutritionFood.calories?.toString() ?: "---")
                        NutrientInfo(label = "Carbs", value = nutritionFood.carbs?.let { "${it}g" } ?: "---")
                        NutrientInfo(label = "Protein", value = nutritionFood.protein?.let { "${it}g" } ?: "---")
                        NutrientInfo(label = "Fat", value = nutritionFood.fat?.let { "${it}g" } ?: "---")
                    }
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

@Preview
@Composable
fun FoodSearchScreenPreview() {
    FoodSearchScreen(navController = rememberNavController())
}