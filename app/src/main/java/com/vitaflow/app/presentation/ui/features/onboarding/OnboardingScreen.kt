package com.vitaflow.app.presentation.ui.features.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vitaflow.app.common.Routes
import com.vitaflow.app.presentation.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 5 })
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is OnboardingNavigationEvent.NavigateToHome -> {
                    navController.navigate(Routes.HOMESCREEN) {
                        popUpTo(Routes.ONBOARDINGSCREEN) { inclusive = true }
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundWhite
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = false
            ) { page ->
                when (page) {
                    0 -> WelcomePage()
                    1 -> PersonalInfoPage(state, viewModel)
                    2 -> GoalsAndActivityPage(state, viewModel)
                    3 -> NutritionTargetsPage(state, viewModel)
                    4 -> StepsTargetPage(state, viewModel)
                }
            }

            // Bottom Navigation
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                // Page Indicators
                Row(
                    Modifier.align(Alignment.CenterStart),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(5) { index ->
                        val isSelected = pagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .height(6.dp)
                                .width(if (isSelected) 24.dp else 6.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) PrimaryGreen else Color.LightGray)
                                .animateContentSize()
                        )
                    }
                }

                // Navigation Buttons
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Back Button
                    if (pagerState.currentPage > 0) {
                        FloatingActionButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            modifier = Modifier.size(48.dp),
                            containerColor = Color.White,
                            contentColor = PrimaryGreen,
                            shape = CircleShape,
                            elevation = FloatingActionButtonDefaults.elevation(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }

                    // Next/Complete Button
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                if (pagerState.currentPage < 4) {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                } else {
                                    viewModel.completeOnboarding()
                                }
                            }
                        },
                        modifier = Modifier.size(56.dp),
                        containerColor = PrimaryGreen,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Icon(
                                imageVector = if (pagerState.currentPage == 4) Icons.Default.Check else Icons.Default.ArrowForward,
                                contentDescription = "Next"
                            )
                        }
                    }
                }
            }
        }

        // Error Snackbar
        state.error?.let { error ->
            LaunchedEffect(error) {
                kotlinx.coroutines.delay(3000)
                viewModel.clearError()
            }
        }
    }
}

@Composable
private fun WelcomePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(240.dp)
                .background(
                    Brush.radialGradient(listOf(PrimaryGreen.copy(0.15f), Color.Transparent)),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = PrimaryGreen
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Your Health,\nFlowing Naturally",
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 40.sp
            ),
            textAlign = TextAlign.Center,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Let's personalize your journey to reach your fitness peaks without the guesswork.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = TextSecondary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

@Composable
private fun PersonalInfoPage(state: OnboardingState, viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "About You",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Help us understand your body and create personalized targets",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Gender Selection
        Text(
            text = "Gender",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Gender.values().forEach { gender ->
                SelectableChip(
                    label = gender.name.lowercase().replaceFirstChar { it.uppercase() },
                    selected = state.gender == gender,
                    onClick = { viewModel.updateGender(gender) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Age Input
        InputField(
            label = "Age",
            value = state.age.toString(),
            onValueChange = { viewModel.updateAge(it.toIntOrNull() ?: state.age) },
            suffix = "years",
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Weight Input
        InputField(
            label = "Weight",
            value = state.weight.toString(),
            onValueChange = { viewModel.updateWeight(it.toFloatOrNull() ?: state.weight) },
            suffix = "kg",
            keyboardType = KeyboardType.Decimal
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Height Input
        InputField(
            label = "Height",
            value = state.height.toString(),
            onValueChange = { viewModel.updateHeight(it.toFloatOrNull() ?: state.height) },
            suffix = "cm",
            keyboardType = KeyboardType.Decimal
        )
    }
}

@Composable
private fun GoalsAndActivityPage(state: OnboardingState, viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Your Goals",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "What brings you here?",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Fitness Goal Selection
        Text(
            text = "Fitness Goal",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        FitnessGoal.values().forEach { goal ->
            GoalCard(
                title = when (goal) {
                    FitnessGoal.LOSE_WEIGHT -> "Lose Weight"
                    FitnessGoal.MAINTAIN_WEIGHT -> "Maintain Weight"
                    FitnessGoal.GAIN_MUSCLE -> "Gain Muscle"
                    FitnessGoal.IMPROVE_FITNESS -> "Improve Fitness"
                },
                description = when (goal) {
                    FitnessGoal.LOSE_WEIGHT -> "Burn fat and shed pounds"
                    FitnessGoal.MAINTAIN_WEIGHT -> "Stay healthy at current weight"
                    FitnessGoal.GAIN_MUSCLE -> "Build strength and mass"
                    FitnessGoal.IMPROVE_FITNESS -> "Boost endurance and health"
                },
                icon = when (goal) {
                    FitnessGoal.LOSE_WEIGHT -> Icons.Default.Settings
                    FitnessGoal.MAINTAIN_WEIGHT -> Icons.Default.Settings
                    FitnessGoal.GAIN_MUSCLE -> Icons.Default.Settings
                    FitnessGoal.IMPROVE_FITNESS -> Icons.Default.Settings
                },
                selected = state.fitnessGoal == goal,
                onClick = { viewModel.updateGoal(goal) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Activity Level Selection
        Text(
            text = "Activity Level",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        ActivityLevel.values().forEach { level ->
            ActivityLevelCard(
                title = when (level) {
                    ActivityLevel.SEDENTARY -> "Sedentary"
                    ActivityLevel.LIGHTLY_ACTIVE -> "Lightly Active"
                    ActivityLevel.MODERATELY_ACTIVE -> "Moderately Active"
                    ActivityLevel.VERY_ACTIVE -> "Very Active"
                    ActivityLevel.EXTREMELY_ACTIVE -> "Extremely Active"
                },
                description = when (level) {
                    ActivityLevel.SEDENTARY -> "Little to no exercise"
                    ActivityLevel.LIGHTLY_ACTIVE -> "Exercise 1-3 times/week"
                    ActivityLevel.MODERATELY_ACTIVE -> "Exercise 4-5 times/week"
                    ActivityLevel.VERY_ACTIVE -> "Intense exercise 6-7 times/week"
                    ActivityLevel.EXTREMELY_ACTIVE -> "Very intense daily exercise"
                },
                selected = state.activityLevel == level,
                onClick = { viewModel.updateActivityLevel(level) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun NutritionTargetsPage(state: OnboardingState, viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Nutrition Targets",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Based on your profile, we recommend these daily targets",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Calories Target
        NutritionSlider(
            label = "Daily Calories",
            value = state.calorieTarget,
            onValueChange = { viewModel.updateCalorieTarget(it) },
            range = 1200f..4000f,
            steps = 55,
            unit = "kcal",
            icon = Icons.Default.Settings,
            color = OrangeAccent
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Protein Target
        NutritionSlider(
            label = "Protein",
            value = state.proteinTarget,
            onValueChange = { viewModel.updateProteinTarget(it) },
            range = 50f..300f,
            steps = 49,
            unit = "g",
            icon = Icons.Default.Settings,
            color = PrimaryGreen
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Carbs Target
        NutritionSlider(
            label = "Carbohydrates",
            value = state.carbsTarget,
            onValueChange = { viewModel.updateCarbsTarget(it) },
            range = 100f..500f,
            steps = 79,
            unit = "g",
            icon = Icons.Default.Settings,
            color = SecondaryBlue
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Fat Target
        NutritionSlider(
            label = "Fat",
            value = state.fatTarget,
            onValueChange = { viewModel.updateFatTarget(it) },
            range = 30f..150f,
            steps = 23,
            unit = "g",
            icon = Icons.Default.Settings,
            color = PurpleAccent
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Water Target
        NutritionSlider(
            label = "Water Intake",
            value = state.waterTarget,
            onValueChange = { viewModel.updateWaterTarget(it) },
            range = 1000f..5000f,
            steps = 79,
            unit = "ml",
            icon = Icons.Default.Settings,
            color = SecondaryBlue
        )

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun StepsTargetPage(state: OnboardingState, viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = PrimaryGreen
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Daily Activity Goal",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Movement is medicine. Set a step goal that challenges you.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(PrimaryGreen.copy(0.1f), PrimaryGreen.copy(0.05f))
                    )
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Daily Step Goal",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    state.stepsTarget.toString(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 56.sp
                    ),
                    color = PrimaryGreen
                )
                Text(
                    "steps",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Slider(
                    value = state.stepsTarget.toFloat(),
                    onValueChange = { viewModel.updateStepsTarget(it.toInt()) },
                    valueRange = 2000f..20000f,
                    steps = 35,
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryGreen,
                        activeTrackColor = PrimaryGreen,
                        inactiveTrackColor = PrimaryGreen.copy(0.2f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("2,000", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text("20,000", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Recommended: ${when (state.activityLevel) {
                ActivityLevel.SEDENTARY -> "5,000"
                ActivityLevel.LIGHTLY_ACTIVE -> "7,500"
                ActivityLevel.MODERATELY_ACTIVE -> "10,000"
                ActivityLevel.VERY_ACTIVE -> "12,500"
                ActivityLevel.EXTREMELY_ACTIVE -> "15,000"
            }} steps based on your activity level",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) PrimaryGreen else Color.White,
        border = BorderStroke(
            width = 2.dp,
            color = if (selected) PrimaryGreen else Color.LightGray
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = if (selected) Color.White else TextSecondary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String,
    keyboardType: KeyboardType
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text(suffix) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors().copy(
                focusedIndicatorColor = PrimaryGreen,
                unfocusedIndicatorColor = Color.LightGray
            )
        )
    }
}

@Composable
private fun GoalCard(
    title: String,
    description: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) PrimaryGreen.copy(0.1f) else Color.White
        ),
        border = BorderStroke(
            width = 2.dp,
            color = if (selected) PrimaryGreen else Color.LightGray
        ),
        elevation = CardDefaults.cardElevation(if (selected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (selected) PrimaryGreen else Color.LightGray.copy(0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) Color.White else TextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) PrimaryGreen else TextPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryGreen
                )
            }
        }
    }
}

@Composable
private fun ActivityLevelCard(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) PrimaryGreen.copy(0.1f) else Color.White
        ),
        border = BorderStroke(
            width = 2.dp,
            color = if (selected) PrimaryGreen else Color.LightGray
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selected) PrimaryGreen else TextPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryGreen
                )
            }
        }
    }
}

@Composable
private fun NutritionSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    unit: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(0.05f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "$value $unit",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = range,
                steps = steps,
                colors = SliderDefaults.colors(
                    thumbColor = color,
                    activeTrackColor = color,
                    inactiveTrackColor = color.copy(0.2f)
                )
            )
        }
    }
}