package com.vitaflow.app.presentation.ui.features.nutrition

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.vitaflow.app.common.Routes
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
                    navController.navigate("barcode_scan")
                }
                NavigationEvent.NavigateToPhotoCapture -> {
                    navController.navigate("photo_capture")
                }
                NavigationEvent.NavigateToRecipes -> {
                    navController.navigate("recipes")
                }
                NavigationEvent.ShowQuickCaloriesDialog -> {
                    Toast.makeText(context, "Quick Calories", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                ),
                title = {
                    Column {
                        Text(
                            text = "Nutrition",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault()).format(Date()),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Normal
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate("food_search/all")
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Food",
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = {
                        navController.navigate("nutrition_settings")
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings",
                            tint = Color.Black
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->

        // Show loading indicator
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Show error if any
                uiState.value.error?.let { error ->
                    item {
                        ErrorCard(error = error)
                    }
                }

                // Calorie Overview Card
                item {
                    CalorieOverviewCard(
                        totalCalories = uiState.value.totalCalories,
                        targetCalories = uiState.value.targetCalories,
                        consumedCalories = uiState.value.consumedCalories,
                        burnedCalories = uiState.value.burnedCalories
                    )
                }

                // Macronutrients Card
                item {
                    MacronutrientsCard(
                        carbs = uiState.value.carbs,
                        protein = uiState.value.protein,
                        fat = uiState.value.fat
                    )
                }

                // Quick Add Buttons
                item {
                    QuickAddSection(
                        onActionClick = { action ->
                            viewModel.onQuickAddClick(action)
                        }
                    )
                }

                // Meals Section
                item {
                    MealsSection(
                        meals = uiState.value.meals,
                        onAddMeal = { mealType ->
                            viewModel.onAddMealClick(mealType)
                        },
                        onRemoveFood = { entryId ->
                            viewModel.removeFoodEntry(entryId)
                        }
                    )
                }

                // Water Intake
                item {
                    WaterIntakeCard(
                        currentIntake = uiState.value.waterIntake,
                        targetIntake = uiState.value.targetWaterIntake,
                        onAddWater = { amount ->
                            viewModel.addWater(amount)
                        }
                    )
                }

                // Recent Foods
                item {
                    RecentFoodsSection(
                        recentFoods = uiState.value.recentFoods,
                        onFoodClick = { food ->
                            viewModel.onRecentFoodClick(food)
                        }
                    )
                }

                // Nutrition Tips
                item {
                    NutritionTipsCard()
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
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFEBEE)
        )
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Circular Progress
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(160.dp)
            ) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Gray.copy(alpha = 0.2f),
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$remainingCalories",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = if (remainingCalories >= 0) "remaining" else "over",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CalorieInfo(
                    label = "Target",
                    value = targetCalories,
                    color = Color(0xFF2196F3)
                )
                CalorieInfo(
                    label = "Food",
                    value = consumedCalories,
                    color = Color(0xFF4CAF50)
                )
                CalorieInfo(
                    label = "Exercise",
                    value = burnedCalories,
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
fun CalorieInfo(
    label: String,
    value: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$value",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun MacronutrientsCard(
    carbs: MacroNutrient,
    protein: MacroNutrient,
    fat: MacroNutrient
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Macronutrients",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MacroProgressBar("Carbs", carbs, Color(0xFF2196F3))
                MacroProgressBar("Protein", protein, Color(0xFF4CAF50))
                MacroProgressBar("Fat", fat, Color(0xFFFF9800))
            }
        }
    }
}

@Composable
fun MacroProgressBar(
    name: String,
    macro: MacroNutrient,
    color: Color
) {
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
                color = Color.Black
            )
            Text(
                text = "${macro.current}g / ${macro.target}g",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    Color.Gray.copy(alpha = 0.2f),
                    RoundedCornerShape(4.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .background(
                        color,
                        RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

@Composable
fun QuickAddSection(
    onActionClick: (QuickAddAction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Quick Add",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(quickAddItems) { item ->
                    QuickAddButton(
                        icon = item.icon,
                        label = item.label,
                        color = item.color,
                        onClick = { onActionClick(item.action) }
                    )
                }
            }
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
                .size(60.dp)
                .background(
                    color.copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MealsSection(
    meals: List<MealWithEntries>,
    onAddMeal: (String) -> Unit,
    onRemoveFood: (Long) -> Unit
) {
    Column {
        Text(
            text = "Today's Meals",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            mealTypes.forEach { mealType ->
                val meal = meals.find { it.type == mealType.type }
                MealCard(
                    mealType = mealType,
                    meal = meal,
                    onAddClick = { onAddMeal(mealType.type) },
                    onRemoveFood = onRemoveFood
                )
            }
        }
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { if (meal != null && meal.entries.isNotEmpty()) isExpanded = !isExpanded }
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
                            color = Color.Black
                        )
                        Text(
                            text = if (meal != null) "${meal.totalCalories} cal" else "0 cal",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                IconButton(onClick = onAddClick) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add ${mealType.name}",
                        tint = Color.Gray
                    )
                }
            }

            // Expandable food entries
            if (isExpanded && meal != null && meal.entries.isNotEmpty()) {
                HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
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
fun FoodEntryItem(
    entry: FoodEntryWithDetails,
    onRemove: () -> Unit
) {
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
                color = Color.Black
            )
            Text(
                text = "${entry.quantity.toInt()}g • ${entry.calculatedCalories} cal",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Remove",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun WaterIntakeCard(
    currentIntake: Int,
    targetIntake: Int,
    onAddWater: (Int) -> Unit
) {
    val progress = (currentIntake.toFloat() / targetIntake.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
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
                        color = Color.Black
                    )
                }

                Text(
                    text = "$currentIntake / $targetIntake ml",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Water Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .background(
                        Color.Gray.copy(alpha = 0.2f),
                        RoundedCornerShape(6.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF2196F3),
                                    Color(0xFF03DAC6)
                                )
                            ),
                            RoundedCornerShape(6.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Water Add Buttons
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
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF2196F3), Color(0xFF03DAC6))
                            )
                        )
                    ) {
                        Text(
                            text = "+${amount}ml",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentFoodsSection(
    recentFoods: List<FoodItem>,
    onFoodClick: (FoodItem) -> Unit
) {
    if (recentFoods.isNotEmpty()) {
        Column {
            Text(
                text = "Recent Foods",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recentFoods) { food ->
                    RecentFoodCard(
                        food = food,
                        onClick = { onFoodClick(food) }
                    )
                }
            }
        }
    }
}

@Composable
fun RecentFoodCard(
    food: FoodItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(food.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = food.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = food.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${food.caloriesPer100g.toInt()} cal/100g",
                fontSize = 12.sp,
                color = Color.Gray
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

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Daily Tip",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Stay hydrated! Drinking water before meals can help with portion control and digestion.",
                    fontSize = 14.sp,
                    color = Color.Gray,
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

@Preview
@Composable
fun NutritionScreenPreview() {
    NutritionScreen(navController = rememberNavController())
}