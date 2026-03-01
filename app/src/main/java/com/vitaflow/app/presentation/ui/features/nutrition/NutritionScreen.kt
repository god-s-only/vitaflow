package com.vitaflow.app.presentation.ui.features.nutrition

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.vitaflow.app.common.Routes
import com.vitaflow.app.domain.models.Food
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    navController: NavController,
    viewModel: NutritionViewModel = hiltViewModel()
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var showQuickCaloriesDialog by remember { mutableStateOf(false) }

    // Handle navigation events
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is NavigationEvent.NavigateToFoodSearch -> {
                    navController.navigate(Routes.FOODSEARCHSCREEN + "/${event.mealType}")
                }
                is NavigationEvent.ShowAddFoodDialog -> {
                    Toast.makeText(context, "Add ${event.food.name}", Toast.LENGTH_SHORT).show()
                }
                is NavigationEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                NavigationEvent.NavigateToBarcodeScan -> {
                    navController.navigate(Routes.BARCODE_SCREEN + "/breakfast")
                }
                NavigationEvent.NavigateToPhotoCapture -> {
                    navController.navigate(Routes.PHOTO_CAPTURE)
                }
                NavigationEvent.NavigateToRecipes -> {
                    navController.navigate(Routes.RECIPES_SCREEN)
                }
                NavigationEvent.ShowQuickCaloriesDialog -> {
                    showQuickCaloriesDialog = true
                }
            }
        }
    }

    LaunchedEffect(navController) {
        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        savedStateHandle?.getStateFlow<Food?>("scanned_food", null)?.collect { food ->
            if (food != null) {
                val mealType = savedStateHandle.get<String>("meal_type") ?: "breakfast"
                viewModel.addFoodToMeal(food, mealType, 100.0)
                savedStateHandle.remove<Food>("scanned_food")
                savedStateHandle.remove<String>("meal_type")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = {
                    Column {
                        Text(
                            text = "Nutrition",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(Date()),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Normal
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("food_search/all") }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Food",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = { navController.navigate(Routes.NUTRITIONSCREENSETTINGS) }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        if (uiState.value.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // CRITICAL: Single LazyColumn with NO nested LazyRows
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(bottom = 80.dp), // Space for bottom nav
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Error Card
                uiState.value.error?.let { error ->
                    item(key = "error") {
                        ErrorCard(error = error)
                    }
                }

                // Calorie Overview
                item(key = "calorie_overview") {
                    CalorieOverviewCard(
                        totalCalories = uiState.value.totalCalories,
                        targetCalories = uiState.value.targetCalories,
                        consumedCalories = uiState.value.consumedCalories,
                        burnedCalories = uiState.value.burnedCalories
                    )
                }

                // Macronutrients
                item(key = "macronutrients") {
                    MacronutrientsCard(
                        carbs = uiState.value.carbs,
                        protein = uiState.value.protein,
                        fat = uiState.value.fat
                    )
                }

                // Quick Add Section Header
                item(key = "quick_add_header") {
                    Text(
                        text = "Quick Add",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Quick Add Buttons - HORIZONTAL ROW (not LazyRow)
                item(key = "quick_add_buttons") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        quickAddItems.forEach { item ->
                            QuickAddButton(
                                icon = item.icon,
                                label = item.label,
                                color = item.color,
                                onClick = { viewModel.onQuickAddClick(item.action) }
                            )
                        }
                    }
                }

                // Meals Section Header
                item(key = "meals_header") {
                    Text(
                        text = "Today's Meals",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Meal Cards - directly in LazyColumn
                items(
                    items = mealTypes,
                    key = { it.type }
                ) { mealType ->
                    val meal = uiState.value.meals.find { it.type == mealType.type }
                    MealCard(
                        mealType = mealType,
                        meal = meal,
                        onAddClick = { viewModel.onAddMealClick(mealType.type) },
                        onRemoveFood = { viewModel.removeFoodEntry(it) }
                    )
                }

                // Water Intake
                item(key = "water_intake") {
                    WaterIntakeCard(
                        currentIntake = uiState.value.waterIntake,
                        targetIntake = uiState.value.targetWaterIntake,
                        onAddWater = { viewModel.addWater(it) }
                    )
                }

                // Recent Foods Header
                if (uiState.value.recentFoods.isNotEmpty()) {
                    item(key = "recent_foods_header") {
                        Text(
                            text = "Recent Foods",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Recent Foods - directly in LazyColumn (2 columns)
                    val recentFoods = uiState.value.recentFoods
                    items(
                        count = (recentFoods.size + 1) / 2,
                        key = { index -> "recent_row_$index" }
                    ) { rowIndex ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val firstIndex = rowIndex * 2
                            val secondIndex = firstIndex + 1

                            RecentFoodCard(
                                food = recentFoods[firstIndex],
                                onClick = { viewModel.onRecentFoodClick(recentFoods[firstIndex]) },
                                modifier = Modifier.weight(1f)
                            )

                            if (secondIndex < recentFoods.size) {
                                RecentFoodCard(
                                    food = recentFoods[secondIndex],
                                    onClick = { viewModel.onRecentFoodClick(recentFoods[secondIndex]) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Nutrition Tips
                item(key = "nutrition_tips") {
                    NutritionTipsCard()
                }

                // Bottom spacer
                item(key = "bottom_spacer") {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Quick Calories Dialog
    if (showQuickCaloriesDialog) {
        QuickCaloriesDialog(
            onDismiss = { showQuickCaloriesDialog = false },
            onSave = { calories, mealType, note ->
                viewModel.addQuickCalories(calories, mealType, note)
                showQuickCaloriesDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickCaloriesDialog(
    onDismiss: () -> Unit,
    onSave: (calories: Int, mealType: String, note: String) -> Unit
) {
    var caloriesText by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf("snacks") }
    var noteText by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Calories",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Calories Input
                OutlinedTextField(
                    value = caloriesText,
                    onValueChange = {
                        caloriesText = it
                        errorText = null
                    },
                    label = { Text("Calories") },
                    placeholder = { Text("Enter calories") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    isError = errorText != null,
                    supportingText = errorText?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF9C27B0),
                        focusedLabelColor = Color(0xFF9C27B0),
                        cursorColor = Color(0xFF9C27B0)
                    )
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedMealType.replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Meal Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF9C27B0),
                            focusedLabelColor = Color(0xFF9C27B0)
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        listOf("breakfast", "lunch", "dinner", "snacks").forEach { mealType ->
                            DropdownMenuItem(
                                text = { Text(mealType.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    selectedMealType = mealType
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Optional Note
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Note (Optional)") },
                    placeholder = { Text("e.g., Protein shake, Snack bar") },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val calories = caloriesText.toIntOrNull()
                            if (calories != null && calories > 0) {
                                onSave(calories, selectedMealType, noteText)
                            }
                        }
                    ),
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF9C27B0),
                        focusedLabelColor = Color(0xFF9C27B0),
                        cursorColor = Color(0xFF9C27B0)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val calories = caloriesText.toIntOrNull()
                            when {
                                calories == null -> errorText = "Please enter a valid number"
                                calories <= 0 -> errorText = "Calories must be greater than 0"
                                calories > 10000 -> errorText = "Calories seem too high"
                                else -> onSave(calories, selectedMealType, noteText)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9C27B0)
                        )
                    ) {
                        Text("Add", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorCard(error: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "Error",
                tint = Color(0xFFD32F2F)
            )
            Text(
                text = error,
                fontSize = 14.sp,
                color = Color(0xFFD32F2F)
            )
        }
    }
}

@Composable
fun CalorieOverviewCard(
    totalCalories: Int,
    targetCalories: Int,
    consumedCalories: Int,
    burnedCalories: Int
) {
    val remainingCalories = targetCalories - consumedCalories + burnedCalories
    val progress = (consumedCalories.toFloat() / targetCalories.toFloat()).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progress"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Daily Calories",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                    strokeWidth = 12.dp,
                    trackColor = Color.Transparent
                )
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = if (remainingCalories >= 0) Color(0xFF4CAF50) else Color(0xFFFF5722),
                    strokeWidth = 12.dp,
                    trackColor = Color.Transparent
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$remainingCalories",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (remainingCalories >= 0) "remaining" else "over",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CalorieInfo("Target", targetCalories, Color(0xFF2196F3))
                CalorieInfo("Food", consumedCalories, Color(0xFF4CAF50))
                CalorieInfo("Exercise", burnedCalories, Color(0xFFFF9800))
            }
        }
    }
}

@Composable
fun CalorieInfo(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$value",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun MacronutrientsCard(carbs: MacroNutrient, protein: MacroNutrient, fat: MacroNutrient) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Macronutrients",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                MacroProgressBar("Carbs", carbs, Color(0xFF2196F3))
                MacroProgressBar("Protein", protein, Color(0xFF4CAF50))
                MacroProgressBar("Fat", fat, Color(0xFFFF9800))
            }
        }
    }
}

@Composable
fun MacroProgressBar(name: String, macro: MacroNutrient, color: Color) {
    val progress = (macro.current.toFloat() / macro.target.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000),
        label = "macro_progress"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${macro.current}g / ${macro.target}g",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun QuickAddButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "button_scale",
        finishedListener = { isPressed = false }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clickable {
                isPressed = true
                onClick()
            }
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(color.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun MealCard(
    mealType: MealType,
    meal: MealWithEntries?,
    onAddClick: () -> Unit,
    onRemoveFood: (Long) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (meal != null && meal.entries.isNotEmpty()) isExpanded = !isExpanded
                    }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = mealType.icon,
                        contentDescription = mealType.name,
                        tint = mealType.color,
                        modifier = Modifier.size(24.dp)
                    )

                    Column {
                        Text(
                            text = mealType.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (meal != null) "${meal.totalCalories} cal" else "0 cal",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onAddClick) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add ${mealType.name}",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (isExpanded && meal != null && meal.entries.isNotEmpty()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    meal.entries.forEach { entry ->
                        FoodEntryItem(
                            entry = entry,
                            onRemove = { onRemoveFood(entry.entryId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FoodEntryItem(entry: FoodEntryWithDetails, onRemove: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.food.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${entry.quantity.toInt()}g • ${entry.calculatedCalories} cal",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Remove",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun WaterIntakeCard(currentIntake: Int, targetIntake: Int, onAddWater: (Int) -> Unit) {
    val progress = (currentIntake.toFloat() / targetIntake.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.FavoriteBorder,
                        contentDescription = "Water",
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Water Intake",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "$currentIntake / $targetIntake ml",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF2196F3), Color(0xFF03DAC6))
                            ),
                            RoundedCornerShape(6.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(250, 500, 750).forEach { amount ->
                    OutlinedButton(
                        onClick = { onAddWater(amount) },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF2196F3)
                        )
                    ) {
                        Text(text = "+${amount}ml", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun RecentFoodCard(
    food: FoodItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(food.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = food.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = food.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${food.caloriesPer100g.toInt()} cal/100g",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun NutritionTipsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF6C63FF).copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Tip",
                tint = Color(0xFF6C63FF),
                modifier = Modifier.size(32.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Daily Tip",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Stay hydrated! Drinking water before meals can help with portion control and digestion.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

// Data classes
data class MacroNutrient(
    val current: Int,
    val target: Int
)

data class MealType(
    val type: String,
    val name: String,
    val icon: ImageVector,
    val color: Color
)

data class QuickAddItemData(
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val action: QuickAddAction
)

// Sample data
val mealTypes = listOf(
    MealType("breakfast", "Breakfast", Icons.Filled.Star, Color(0xFFFF9800)),
    MealType("lunch", "Lunch", Icons.Filled.Favorite, Color(0xFF4CAF50)),
    MealType("dinner", "Dinner", Icons.Filled.FavoriteBorder, Color(0xFF9C27B0)),
    MealType("snacks", "Snacks", Icons.Filled.Menu, Color(0xFF2196F3))
)

val quickAddItems = listOf(
    QuickAddItemData("Scan", Icons.Filled.Search, Color(0xFF4CAF50), QuickAddAction.SCAN_BARCODE),
    QuickAddItemData("Photo", Icons.Filled.Add, Color(0xFF2196F3), QuickAddAction.TAKE_PHOTO),
    QuickAddItemData("Recipe", Icons.Filled.Menu, Color(0xFFFF9800), QuickAddAction.ADD_RECIPE),
    QuickAddItemData("Quick Cal", Icons.Filled.Add, Color(0xFF9C27B0), QuickAddAction.QUICK_CALORIES)
)