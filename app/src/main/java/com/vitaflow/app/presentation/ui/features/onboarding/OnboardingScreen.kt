package com.vitaflow.app.presentation.ui.features.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch

private val PrimaryGreen = Color(0xFF4CAF50)
private val SecondaryBlue = Color(0xFF2196F3)
private val PurpleAccent = Color(0xFF9C27B0)
private val OrangeAccent = Color(0xFFFF9800)
private val BackgroundWhite = Color(0xFFFAFAFA)
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF666666)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    navController: NavController
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    var selectedGoal by remember { mutableStateOf<FitnessGoal?>(null) }

    var age by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf<Gender?>(null) }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var selectedActivityLevel by remember { mutableStateOf<ActivityLevel?>(null) }
    var targetWeight by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(BackgroundWhite)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> WelcomePage()
                    1 -> GoalSelectionPage(
                        selectedGoal = selectedGoal,
                        onGoalSelected = { selectedGoal = it }
                    )
                    2 -> PersonalizationPage(
                        age = age,
                        onAgeChange = { age = it },
                        selectedGender = selectedGender,
                        onGenderSelected = { selectedGender = it },
                        height = height,
                        onHeightChange = { height = it },
                        weight = weight,
                        onWeightChange = { weight = it },
                        selectedActivityLevel = selectedActivityLevel,
                        onActivityLevelSelected = { selectedActivityLevel = it },
                        targetWeight = targetWeight,
                        onTargetWeightChange = { targetWeight = it }
                    )
                }
            }

            // Page Indicators (Bottom Left)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(32.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { index ->
                    PageIndicator(
                        isActive = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }

            // CTA Button (Bottom Right)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(32.dp)
            ) {
                when (pagerState.currentPage) {
                    0 -> {
                        Button(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreen
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(56.dp)
                        ) {
                            Text(
                                text = "Get Started",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    1 -> {
                        Button(
                            onClick = {
                                if (selectedGoal != null) {
                                    scope.launch {
                                        pagerState.animateScrollToPage(2)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedGoal != null) PrimaryGreen else Color.Gray
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(56.dp),
                            enabled = selectedGoal != null
                        ) {
                            Text(
                                text = "Continue",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    2 -> {
                        val isFormValid = age.isNotEmpty() &&
                                selectedGender != null &&
                                height.isNotEmpty() &&
                                weight.isNotEmpty() &&
                                selectedActivityLevel != null

                        Button(
                            onClick = {
                                // TODO: Save onboarding data and navigate to main screen
                                navController.navigate("nutrition") {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFormValid) PrimaryGreen else Color.Gray
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(56.dp),
                            enabled = isFormValid
                        ) {
                            Text(
                                text = "Generate Plan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.Check,
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

@Composable
private fun PageIndicator(
    isActive: Boolean,
    onClick: () -> Unit
) {
    val width by animateFloatAsState(
        targetValue = if (isActive) 32f else 8f,
        animationSpec = tween(300),
        label = "indicator_width"
    )

    Box(
        modifier = Modifier
            .width(width.dp)
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                if (isActive) PrimaryGreen else Color.Gray.copy(alpha = 0.3f)
            )
            .clickable { onClick() }
    )
}

@Composable
private fun WelcomePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .padding(bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Illustration
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            PrimaryGreen.copy(alpha = 0.1f),
                            SecondaryBlue.copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(140.dp),
                tint = PrimaryGreen
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Title
        Text(
            text = "Welcome to VitaFlow",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Subtitle
        Text(
            text = "Your all-in-one fitness companion — track workouts, monitor nutrition, and hit your daily goals with confidence.",
            fontSize = 16.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BulletPoint(
                icon = Icons.Default.Star,
                text = "Personalized fitness and nutrition plans",
                color = PrimaryGreen
            )
            BulletPoint(
                icon = Icons.Default.Settings,
                text = "Track calories with food search + barcode scanner",
                color = SecondaryBlue
            )
            BulletPoint(
                icon = Icons.Default.Settings,
                text = "Monitor workouts, steps, and daily habits",
                color = OrangeAccent
            )
        }
    }
}

@Composable
private fun BulletPoint(
    icon: ImageVector,
    text: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = text,
            fontSize = 14.sp,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun GoalSelectionPage(
    selectedGoal: FitnessGoal?,
    onGoalSelected: (FitnessGoal) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .padding(bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Illustration
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                PurpleAccent.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = PurpleAccent
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text(
                text = "What's Your Main Goal?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
        }

        item {
            Text(
                text = "We'll personalize your recommendations based on what you want to achieve.",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(FitnessGoal.values().size) { index ->
            val goal = FitnessGoal.values()[index]
            GoalCard(
                goal = goal,
                isSelected = selectedGoal == goal,
                onClick = { onGoalSelected(goal) }
            )
        }
    }
}

@Composable
private fun GoalCard(
    goal: FitnessGoal,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryGreen.copy(alpha = 0.1f) else Color.White
        ),
        border = if (isSelected) {
            ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.linearGradient(
                    colors = listOf(PrimaryGreen, SecondaryBlue)
                ),
                width = 2.dp
            )
        } else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(goal.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = goal.icon,
                    contentDescription = null,
                    tint = goal.color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goal.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = goal.description,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun PersonalizationPage(
    age: String,
    onAgeChange: (String) -> Unit,
    selectedGender: Gender?,
    onGenderSelected: (Gender) -> Unit,
    height: String,
    onHeightChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit,
    selectedActivityLevel: ActivityLevel?,
    onActivityLevelSelected: (ActivityLevel) -> Unit,
    targetWeight: String,
    onTargetWeightChange: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .padding(bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                OrangeAccent.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(90.dp),
                    tint = OrangeAccent
                )
            }
        }

        item {
            Text(
                text = "Let's Personalize Your Plan",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
        }

        item {
            Text(
                text = "A few details help us calculate your daily calorie and nutrition targets.",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }

        // Age Input
        item {
            OutlinedTextField(
                value = age,
                onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) onAgeChange(it) },
                label = { Text("Age") },
                leadingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    focusedLabelColor = PrimaryGreen,
                    cursorColor = PrimaryGreen
                )
            )
        }

        // Gender Selection
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Gender",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Gender.values().forEach { gender ->
                        GenderChip(
                            gender = gender,
                            isSelected = selectedGender == gender,
                            onClick = { onGenderSelected(gender) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Height Input
        item {
            OutlinedTextField(
                value = height,
                onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) onHeightChange(it) },
                label = { Text("Height (cm)") },
                leadingIcon = {
                    Icon(Icons.Default.Settings, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    focusedLabelColor = PrimaryGreen,
                    cursorColor = PrimaryGreen
                )
            )
        }

        // Weight Input
        item {
            OutlinedTextField(
                value = weight,
                onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) onWeightChange(it) },
                label = { Text("Weight (kg)") },
                leadingIcon = {
                    Icon(Icons.Default.Settings, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    focusedLabelColor = PrimaryGreen,
                    cursorColor = PrimaryGreen
                )
            )
        }

        // Activity Level
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Activity Level",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            }
        }

        items(ActivityLevel.values().size) { index ->
            val level = ActivityLevel.values()[index]
            ActivityLevelCard(
                level = level,
                isSelected = selectedActivityLevel == level,
                onClick = { onActivityLevelSelected(level) }
            )
        }

        // Target Weight (Optional)
        item {
            OutlinedTextField(
                value = targetWeight,
                onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) onTargetWeightChange(it) },
                label = { Text("Target Weight (kg) - Optional") },
                leadingIcon = {
                    Icon(Icons.Default.Settings, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    focusedLabelColor = PrimaryGreen,
                    cursorColor = PrimaryGreen
                )
            )
        }
    }
}

@Composable
private fun GenderChip(
    gender: Gender,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(60.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryGreen.copy(alpha = 0.1f) else Color.White
        ),
        border = if (isSelected) {
            ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.linearGradient(colors = listOf(PrimaryGreen, PrimaryGreen)),
                width = 2.dp
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = gender.icon,
                contentDescription = null,
                tint = if (isSelected) PrimaryGreen else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = gender.label,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) PrimaryGreen else TextPrimary
            )
        }
    }
}

@Composable
private fun ActivityLevelCard(
    level: ActivityLevel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryGreen.copy(alpha = 0.1f) else Color.White
        ),
        border = if (isSelected) {
            ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.linearGradient(colors = listOf(PrimaryGreen, SecondaryBlue)),
                width = 2.dp
            )
        } else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 2.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = level.icon,
                contentDescription = null,
                tint = if (isSelected) PrimaryGreen else TextSecondary,
                modifier = Modifier.size(24.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = level.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) PrimaryGreen else TextPrimary
                )
                Text(
                    text = level.description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Data Classes
enum class FitnessGoal(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color
) {
    LOSE_WEIGHT(
        "Lose Weight",
        "Burn calories and shed pounds with a calorie deficit plan",
        Icons.Default.Settings,
        Color(0xFFE91E63)
    ),
    MAINTAIN_WEIGHT(
        "Maintain Weight",
        "Keep your current weight with balanced nutrition",
        Icons.Default.Settings,
        SecondaryBlue
    ),
    BUILD_MUSCLE(
        "Build Muscle",
        "Gain strength and muscle mass with protein-rich diet",
        Icons.Default.Settings,
        PrimaryGreen
    ),
    IMPROVE_FITNESS(
        "Improve Fitness",
        "Boost endurance and overall health",
        Icons.Default.Settings,
        OrangeAccent
    ),
    EAT_HEALTHIER(
        "Eat Healthier",
        "Focus on nutritious, wholesome foods",
        Icons.Default.Settings,
        PurpleAccent
    )
}

enum class Gender(val label: String, val icon: ImageVector) {
    MALE("Male", Icons.Default.Person),
    FEMALE("Female", Icons.Default.Settings),
    OTHER("Other", Icons.Outlined.Person)
}

enum class ActivityLevel(
    val title: String,
    val description: String,
    val icon: ImageVector
) {
    SEDENTARY(
        "Sedentary",
        "Little to no exercise, desk job",
        Icons.Default.Settings
    ),
    LIGHTLY_ACTIVE(
        "Lightly Active",
        "Light exercise 1-3 days/week",
        Icons.Default.Settings
    ),
    MODERATELY_ACTIVE(
        "Moderately Active",
        "Moderate exercise 3-5 days/week",
        Icons.Default.Settings
    ),
    VERY_ACTIVE(
        "Very Active",
        "Intense exercise 6-7 days/week",
        Icons.Default.Settings
    )
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    OnboardingScreen(rememberNavController())
}