package com.vitaflow.app.presentation.ui.features.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vitaflow.app.presentation.ui.features.nutrition.NutritionViewModel
import kotlinx.coroutines.delay

private val PrimaryGreen = Color(0xFF4CAF50)
private val SecondaryBlue = Color(0xFF2196F3)
private val OrangeAccent = Color(0xFFFF9800)
private val PurpleAccent = Color(0xFF9C27B0)
private val BackgroundWhite = Color(0xFFF8F9FA)
private val CardWhite = Color.White
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF666666)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionSettingsScreen(
    navController: NavController,
    viewModel: NutritionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var calorieTarget by remember { mutableStateOf(state.targetCalories.toString()) }
    var carbsTarget by remember { mutableStateOf(state.carbs.target.toString()) }
    var proteinTarget by remember { mutableStateOf(state.protein.target.toString()) }
    var fatTarget by remember { mutableStateOf(state.fat.target.toString()) }
    var waterTarget by remember { mutableStateOf(state.targetWaterIntake.toString()) }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var expandedSection by remember { mutableStateOf<SettingsSection?>(SettingsSection.CALORIES) }

    // Update local state when viewModel state changes
    LaunchedEffect(state.targetCalories) {
        calorieTarget = state.targetCalories.toString()
    }
    LaunchedEffect(state.carbs.target) {
        carbsTarget = state.carbs.target.toString()
    }
    LaunchedEffect(state.protein.target) {
        proteinTarget = state.protein.target.toString()
    }
    LaunchedEffect(state.fat.target) {
        fatTarget = state.fat.target.toString()
    }
    LaunchedEffect(state.targetWaterIntake) {
        waterTarget = state.targetWaterIntake.toString()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardWhite,
                    titleContentColor = TextPrimary
                ),
                title = {
                    Text(
                        text = "Nutrition Goals",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            // Save all targets
                            calorieTarget.toIntOrNull()?.let {
                                viewModel.updateCalorieTarget(it)
                            }

                            val carbs = carbsTarget.toIntOrNull() ?: 0
                            val protein = proteinTarget.toIntOrNull() ?: 0
                            val fat = fatTarget.toIntOrNull() ?: 0
                            viewModel.updateMacroTargets(carbs, protein, fat)

                            waterTarget.toIntOrNull()?.let {
                                viewModel.updateWaterTarget(it)
                            }

                            showSuccessDialog = true
                        }
                    ) {
                        Text(
                            text = "Save",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PrimaryGreen
                        )
                    }
                }
            )
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Card
            item {
                HeaderCard()
            }

            // Daily Calorie Goal
            item {
                SettingSection(
                    title = "Daily Calorie Goal",
                    subtitle = "Your target daily calories",
                    icon = Icons.Default.Settings,
                    iconColor = Color(0xFFFF5722),
                    value = calorieTarget,
                    onValueChange = { calorieTarget = it },
                    unit = "kcal",
                    isExpanded = expandedSection == SettingsSection.CALORIES,
                    onExpandClick = {
                        expandedSection = if (expandedSection == SettingsSection.CALORIES) null else SettingsSection.CALORIES
                    },
                    quickValues = listOf(1500, 2000, 2500, 3000),
                    onQuickValueClick = { calorieTarget = it.toString() }
                )
            }

            // Macronutrients Section
            item {
                Text(
                    text = "Macronutrient Goals",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Carbs
            item {
                SettingSection(
                    title = "Carbohydrates",
                    subtitle = "Daily carbs target",
                    icon = Icons.Default.Settings,
                    iconColor = SecondaryBlue,
                    value = carbsTarget,
                    onValueChange = { carbsTarget = it },
                    unit = "g",
                    isExpanded = expandedSection == SettingsSection.CARBS,
                    onExpandClick = {
                        expandedSection = if (expandedSection == SettingsSection.CARBS) null else SettingsSection.CARBS
                    },
                    quickValues = listOf(150, 200, 250, 300),
                    onQuickValueClick = { carbsTarget = it.toString() }
                )
            }

            // Protein
            item {
                SettingSection(
                    title = "Protein",
                    subtitle = "Daily protein target",
                    icon = Icons.Default.Settings,
                    iconColor = PrimaryGreen,
                    value = proteinTarget,
                    onValueChange = { proteinTarget = it },
                    unit = "g",
                    isExpanded = expandedSection == SettingsSection.PROTEIN,
                    onExpandClick = {
                        expandedSection = if (expandedSection == SettingsSection.PROTEIN) null else SettingsSection.PROTEIN
                    },
                    quickValues = listOf(80, 100, 150, 200),
                    onQuickValueClick = { proteinTarget = it.toString() }
                )
            }

            // Fat
            item {
                SettingSection(
                    title = "Fat",
                    subtitle = "Daily fat target",
                    icon = Icons.Default.Settings,
                    iconColor = OrangeAccent,
                    value = fatTarget,
                    onValueChange = { fatTarget = it },
                    unit = "g",
                    isExpanded = expandedSection == SettingsSection.FAT,
                    onExpandClick = {
                        expandedSection = if (expandedSection == SettingsSection.FAT) null else SettingsSection.FAT
                    },
                    quickValues = listOf(50, 65, 80, 100),
                    onQuickValueClick = { fatTarget = it.toString() }
                )
            }

            // Water Section
            item {
                Text(
                    text = "Hydration Goal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Water Intake
            item {
                SettingSection(
                    title = "Daily Water Intake",
                    subtitle = "Your hydration target",
                    icon = Icons.Default.Settings,
                    iconColor = SecondaryBlue,
                    value = waterTarget,
                    onValueChange = { waterTarget = it },
                    unit = "ml",
                    isExpanded = expandedSection == SettingsSection.WATER,
                    onExpandClick = {
                        expandedSection = if (expandedSection == SettingsSection.WATER) null else SettingsSection.WATER
                    },
                    quickValues = listOf(1500, 2000, 2500, 3000),
                    onQuickValueClick = { waterTarget = it.toString() }
                )
            }

            // Info Card
            item {
                InfoCard()
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Success Dialog
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = {
                    showSuccessDialog = false
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = {
                    Text(
                        text = "Goals Updated!",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Text(
                        text = "Your nutrition goals have been saved successfully. Keep tracking to reach your targets!",
                        textAlign = TextAlign.Center,
                        color = TextSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            viewModel.refreshData()
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryGreen
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continue")
                    }
                },
                containerColor = CardWhite,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun HeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PrimaryGreen, SecondaryBlue)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Personalize Your Goals",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Set targets that align with your health and fitness objectives",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun SettingSection(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    quickValues: List<Int>,
    onQuickValueClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandClick() }
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                iconColor.copy(alpha = 0.1f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$value $unit",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = iconColor
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            }

            // Expandable Content
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))

                    // Input Field
                    OutlinedTextField(
                        value = value,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                onValueChange(newValue)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Enter $title") },
                        suffix = { Text(unit, color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = iconColor,
                            focusedLabelColor = iconColor,
                            cursorColor = iconColor
                        )
                    )

                    // Quick Select Buttons
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Quick Select",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            quickValues.forEach { quickValue ->
                                OutlinedButton(
                                    onClick = { onQuickValueClick(quickValue) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (value == quickValue.toString())
                                            iconColor.copy(alpha = 0.1f) else Color.Transparent,
                                        contentColor = if (value == quickValue.toString())
                                            iconColor else TextSecondary
                                    ),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        brush = if (value == quickValue.toString())
                                            Brush.linearGradient(colors = listOf(iconColor, iconColor))
                                        else
                                            Brush.linearGradient(colors = listOf(Color.Gray.copy(0.3f), Color.Gray.copy(0.3f)))
                                    )
                                ) {
                                    Text(
                                        text = "$quickValue",
                                        fontSize = 13.sp,
                                        fontWeight = if (value == quickValue.toString())
                                            FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SecondaryBlue.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = SecondaryBlue,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = "Your goals can be adjusted anytime. Listen to your body and modify targets as needed.",
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 18.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

enum class SettingsSection {
    CALORIES,
    CARBS,
    PROTEIN,
    FAT,
    WATER
}