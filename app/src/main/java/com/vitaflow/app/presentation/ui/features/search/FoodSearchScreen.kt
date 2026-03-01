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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vitaflow.app.common.UIEvent
import com.vitaflow.app.domain.models.Food
import com.vitaflow.app.domain.models.NutritionFood
import com.vitaflow.app.presentation.ui.features.nutrition.NutritionViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val PrimaryGreen = Color(0xFF00C853)

@Composable
private fun cardBackgroundColor() = MaterialTheme.colorScheme.surface

@Composable
private fun textPrimaryColor() = MaterialTheme.colorScheme.onSurface

@Composable
private fun textSecondaryColor() = MaterialTheme.colorScheme.onSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodSearchScreen(
    navController: NavController,
    mealType: String,
    viewModel: FoodSearchViewModel = hiltViewModel(),
    nutritionViewModel: NutritionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val searchQuery by remember { derivedStateOf { state.query } }

    var showQuantityDialog by remember { mutableStateOf(false) }
    var selectedFoodForDialog by remember { mutableStateOf<FoodDialogData?>(null) }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { result ->
            when (result) {
                is UIEvent.ShowSnackBar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(message = result.message)
                    }
                }
                is UIEvent.ShowQuantityDialog -> {
                    selectedFoodForDialog = FoodDialogData(
                        foodId = result.foodId,
                        name = result.foodName,
                        caloriesPer100g = result.caloriesPer100g,
                        carbsPer100g = result.carbsPer100g,
                        proteinPer100g = result.proteinPer100g,
                        fatPer100g = result.fatPer100g
                    )
                    showQuantityDialog = true
                }
                UIEvent.PopBackStack -> {
                    navController.popBackStack()
                }
                is UIEvent.Navigate -> {
                    navController.navigate(result.route)
                }
            }
        }
    }

    val showSampleFoods = searchQuery.isEmpty()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = {
                    Text(
                        text = "Add to ${mealType.replaceFirstChar { it.uppercase() }}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = PrimaryGreen)
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { query ->
                    viewModel.onEvent(FoodSearchEvent.OnSearchChange(query))
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.loading && !showSampleFoods) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            }

            if (!showSampleFoods) {
                state.error?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                    ) {
                        Text(text = error, color = Color.Red, modifier = Modifier.padding(16.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            val totalCount = if (showSampleFoods) 0 else state.food.size
            val resultType = if (showSampleFoods) "Search for foods" else "foods found"

            Text(
                text = if (showSampleFoods) resultType else "$totalCount $resultType",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (showSampleFoods) {
                    item { EmptySearchPrompt() }
                } else {
                    items(state.food) { nutritionFood ->
                        NutritionFoodCard(
                            nutritionFood = nutritionFood,
                            onFoodClick = {
                                viewModel.onEvent(
                                    FoodSearchEvent.OnAddFood(
                                        foodId = nutritionFood.id,
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
                            },
                            isLoadingDetails = state.loadingDetailsFor.contains(nutritionFood.id)
                        )
                    }

                    if (state.food.isEmpty() && searchQuery.isNotEmpty() && !state.loading) {
                        item { EmptySearchResult(searchQuery = searchQuery) }
                    }
                }
            }
        }

        if (showQuantityDialog && selectedFoodForDialog != null) {
            QuantityDialog(
                foodData = selectedFoodForDialog!!,
                mealType = mealType,
                onDismiss = {
                    showQuantityDialog = false
                    selectedFoodForDialog = null
                },
                onConfirm = { foodData, quantity ->
                    // Create the Food object
                    val food = Food(
                        id = foodData.foodId,
                        name = foodData.name,
                        caloriesPer100g = foodData.caloriesPer100g,
                        carbsPer100g = foodData.carbsPer100g,
                        proteinPer100g = foodData.proteinPer100g,
                        fatPer100g = foodData.fatPer100g,
                        imageUrl = null
                    )

                    // Add food to meal using NutritionViewModel
                    nutritionViewModel.addFoodToMeal(food, mealType, quantity)

                    // Close dialog and navigate back
                    showQuantityDialog = false
                    selectedFoodForDialog = null

                    // Navigate back to nutrition screen
                    navController.popBackStack()
                }
            )
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
                text = "Search foods from Spoonacular...",
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
private fun EmptySearchPrompt() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                tint = PrimaryGreen,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Search for Foods",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Type in the search bar to find foods from Spoonacular database",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptySearchResult(searchQuery: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                tint = Color.Gray,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No foods found for \"$searchQuery\"",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Try searching with different keywords or check the spelling",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun NutritionFoodCard(
    nutritionFood: NutritionFood,
    onFoodClick: () -> Unit,
    onLoadDetails: (Int) -> Unit,
    isLoadingDetails: Boolean
) {
    LaunchedEffect(nutritionFood.id) {
        if (nutritionFood.calories == null && !isLoadingDetails) {
            onLoadDetails(nutritionFood.id)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFoodClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (isLoadingDetails) {
                        repeat(4) {
                            NutrientInfo(label = "...", value = "⏳")
                        }
                    } else {
                        NutrientInfo(
                            label = "Cal",
                            value = nutritionFood.calories?.toInt()?.toString() ?: "---"
                        )
                        NutrientInfo(
                            label = "Carbs",
                            value = nutritionFood.carbs?.toInt()?.let { "${it}g" } ?: "---"
                        )
                        NutrientInfo(
                            label = "Protein",
                            value = nutritionFood.protein?.toInt()?.let { "${it}g" } ?: "---"
                        )
                        NutrientInfo(
                            label = "Fat",
                            value = nutritionFood.fat?.toInt()?.let { "${it}g" } ?: "---"
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "per 100g",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuantityDialog(
    foodData: FoodDialogData,
    mealType: String,
    onDismiss: () -> Unit,
    onConfirm: (FoodDialogData, Double) -> Unit
) {
    var quantity by remember { mutableStateOf("100") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add ${foodData.name}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Add to: ${mealType.replaceFirstChar { it.uppercase() }}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { newValue ->
                        quantity = newValue.filter { it.isDigit() }
                    },
                    label = { Text("Quantity (grams)") },
                    suffix = { Text("g") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        focusedLabelColor = PrimaryGreen,
                        cursorColor = PrimaryGreen
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                val qty = quantity.toDoubleOrNull() ?: 100.0
                val multiplier = qty / 100.0

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = PrimaryGreen.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Nutrition for ${qty.toInt()}g:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Calories:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "${(foodData.caloriesPer100g * multiplier).toInt()}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        foodData.carbsPer100g?.let {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Carbs:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    "${(it * multiplier).toInt()}g",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Protein:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "${(foodData.proteinPer100g * multiplier).toInt()}g",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Fat:", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "${(foodData.fatPer100g * multiplier).toInt()}g",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantity.toDoubleOrNull() ?: 100.0
                    onConfirm(foodData, qty)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text("Add to ${mealType.replaceFirstChar { it.uppercase() }}")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

data class FoodDialogData(
    val foodId: Int,
    val name: String,
    val caloriesPer100g: Double,
    val carbsPer100g: Double?,
    val proteinPer100g: Double,
    val fatPer100g: Double
)