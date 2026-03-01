package com.vitaflow.app.presentation.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.vitaflow.app.common.Routes
import com.vitaflow.app.common.UIEvent
import com.vitaflow.app.domain.models.Exercise
import com.vitaflow.app.domain.models.FeaturedWorkout
import com.vitaflow.app.domain.models.QuickTraining
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// App Theme Colors
val PrimaryGreen = Color(0xFF00C853)
val LightGreen = Color(0xFF4CAF50)
val DarkGreen = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.state.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { result ->
            when (result) {
                is UIEvent.ShowSnackBar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(message = result.message)
                    }
                }

                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = {
                    Text(
                        text = "VitaFlow",
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = PrimaryGreen
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: Notifications */ }) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            tint = PrimaryGreen
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(bottom = 80.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item(key = "welcome") {
                WelcomeHeader(userName = uiState.value.userName)
            }

            // Featured Workout Section
            item(key = "featured") {
                when {
                    uiState.value.isFeaturedWorkoutLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryGreen)
                        }
                    }

                    uiState.value.featuredWorkout != null -> {
                        FeaturedWorkoutCard(
                            workout = uiState.value.featuredWorkout!!,
                            onClick = {
                                navController.navigate(Routes.WORKOUTSCREEN + "/${uiState.value.featuredWorkout!!.id}")
                            }
                        )
                    }

                    else -> {
                        // Show default featured workout with fallback data
                        FeaturedWorkoutCard(
                            workout = getDefaultFeaturedWorkout(),
                            onClick = {}
                        )
                    }
                }
            }

            item(key = "workout_header") {
                WorkoutCategoriesHeader()
            }

            item(key = "workout_cards") {
                when {
                    uiState.value.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryGreen)
                        }
                    }

                    uiState.value.error != null || uiState.value.exercises.isEmpty() -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            sampleExercises.forEach { exercise ->
                                WorkoutTypeCard(
                                    exercise = exercise,
                                    onClick = {
                                        navController.navigate(Routes.WORKOUTSCREEN + "/${exercise.exerciseId}")
                                    }
                                )
                            }
                        }
                    }

                    else -> {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            uiState.value.exercises.forEach { exercise ->
                                WorkoutTypeCard(
                                    exercise = exercise,
                                    onClick = {
                                        navController.navigate(Routes.WORKOUTSCREEN + "/${exercise.exerciseId}")
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Quick Training Section
            item(key = "quick_training_header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Training Sessions",
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(onClick = { /* TODO: See all */ }) {
                        Text(
                            text = "See All",
                            color = PrimaryGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            when {
                uiState.value.isQuickTrainingsLoading -> {
                    item(key = "quick_training_loading") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryGreen)
                        }
                    }
                }

                uiState.value.quickTrainings.isNotEmpty() -> {
                    items(
                        items = uiState.value.quickTrainings,
                        key = { it.id }
                    ) { training ->
                        QuickTrainingCard(
                            training = training,
                            onClick = {
                                // Navigate to training detail
                                navController.navigate(Routes.WORKOUTSCREEN + "/${training.id}")
                            }
                        )
                    }
                }

                else -> {
                    // Show default quick trainings as fallback
                    items(
                        items = defaultQuickTrainings,
                        key = { it.id }
                    ) { training ->
                        QuickTrainingCard(
                            training = training,
                            onClick = {}
                        )
                    }
                }
            }

            // Bottom spacer for better scrolling
            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun WelcomeHeader(userName: String?) {
    Column {
        Text(
            text = "Hello, ${userName ?: "Fitness Enthusiast"}! 👋",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Let's Get Moving Today",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun WorkoutCategoriesHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Workout Categories",
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        TextButton(onClick = { /* TODO: See all */ }) {
            Text(
                text = "See All",
                color = PrimaryGreen,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun FeaturedWorkoutCard(workout: FeaturedWorkout, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(workout.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = workout.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Green Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                PrimaryGreen.copy(alpha = 0.3f),
                                DarkGreen.copy(alpha = 0.8f)
                            )
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Badge(
                        text = "Featured",
                        backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        textColor = PrimaryGreen
                    )
                    Badge(
                        text = workout.category,
                        backgroundColor = PrimaryGreen.copy(alpha = 0.8f),
                        textColor = Color.White
                    )
                }

                Column {
                    Text(
                        text = workout.title,
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        InfoChip(icon = "🔥", text = "${workout.calories} kcal")
                        InfoChip(icon = "⏱️", text = "${workout.duration} min")
                        InfoChip(icon = "⭐", text = "${workout.rating}")
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickTrainingCard(training: QuickTraining, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PrimaryGreen, LightGreen)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = training.emoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = training.name,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = training.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    InfoTag(text = "${training.duration} min", color = PrimaryGreen)
                    InfoTag(text = "${training.calories} kcal", color = LightGreen)
                    InfoTag(text = training.difficulty, color = DarkGreen)
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Start workout",
                tint = PrimaryGreen,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun Badge(
    text: String,
    backgroundColor: Color,
    textColor: Color = Color.White
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun InfoChip(icon: String, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = icon, fontSize = 14.sp)
        Text(
            text = text,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun InfoTag(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun WorkoutTypeCard(
    exercise: Exercise,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(280.dp)
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (exercise.gifUrl.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(exercise.gifUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = exercise.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PrimaryGreen, LightGreen)
                            )
                        )
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                PrimaryGreen.copy(alpha = 0.8f)
                            ),
                            startY = 100f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = exercise.name.take(25),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Start Workout",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Fallback data functions
private fun getDefaultFeaturedWorkout(): FeaturedWorkout {
    return FeaturedWorkout(
        id = "featured_default",
        title = "Today's Featured Workout",
        description = "Full body workout to boost your energy and strength",
        imageUrl = "https://img.freepik.com/free-photo/portrait-anime-character-doing-fitness-exercising_23-2151666671.jpg",
        category = "Full Body",
        difficulty = "Intermediate",
        duration = 45,
        calories = 350,
        rating = 4.8,
        exercises = emptyList()
    )
}

// Fallback quick training data
private val defaultQuickTrainings = listOf(
    QuickTraining(
        id = "qt_1",
        name = "Morning Energy Boost",
        description = "Quick cardio to start your day with energy",
        duration = 15,
        calories = 120,
        difficulty = "Beginner",
        emoji = "🌅",
        category = "Cardio",
        imageUrl = null,
        exerciseCount = 5
    ),
    QuickTraining(
        id = "qt_2",
        name = "Core Power Session",
        description = "Strengthen your core muscles for better stability",
        duration = 20,
        calories = 150,
        difficulty = "Intermediate",
        emoji = "💪",
        category = "Strength",
        imageUrl = null,
        exerciseCount = 6
    ),
    QuickTraining(
        id = "qt_3",
        name = "HIIT Fat Burner",
        description = "High intensity interval training for maximum burn",
        duration = 25,
        calories = 200,
        difficulty = "Advanced",
        emoji = "🔥",
        category = "HIIT",
        imageUrl = null,
        exerciseCount = 8
    ),
    QuickTraining(
        id = "qt_4",
        name = "Flexibility Flow",
        description = "Improve mobility and flexibility with yoga",
        duration = 18,
        calories = 80,
        difficulty = "All Levels",
        emoji = "🧘",
        category = "Yoga",
        imageUrl = null,
        exerciseCount = 7
    )
)

// Sample exercises for fallback
private val sampleExercises = listOf(
    Exercise(
        exerciseId = "1",
        name = "Push Ups",
        gifUrl = "https://media.giphy.com/media/26FLdmIp6wJr91JAI/giphy.gif",
        bodyParts = listOf("upper body"),
        equipments = listOf("body weight"),
        instructions = listOf("Standard push up exercise"),
        secondaryMuscles = listOf("shoulders"),
        targetMuscles = listOf("chest", "triceps")
    ),
    Exercise(
        exerciseId = "2",
        name = "Squats",
        gifUrl = "https://media.giphy.com/media/ZdUnQS4LXNWnII2HIv/giphy.gif",
        bodyParts = listOf("lower body"),
        equipments = listOf("body weight"),
        instructions = listOf("Standard squat exercise"),
        secondaryMuscles = listOf("glutes"),
        targetMuscles = listOf("quadriceps")
    ),
    Exercise(
        exerciseId = "3",
        name = "Plank",
        gifUrl = "https://media.giphy.com/media/26FLbdnqSIk5ddfJm/giphy.gif",
        bodyParts = listOf("core"),
        equipments = listOf("body weight"),
        instructions = listOf("Hold plank position"),
        secondaryMuscles = listOf("shoulders"),
        targetMuscles = listOf("abs")
    ),
    Exercise(
        exerciseId = "4",
        name = "Burpees",
        gifUrl = "",
        bodyParts = listOf("full body"),
        equipments = listOf("body weight"),
        instructions = listOf("Full body exercise"),
        secondaryMuscles = listOf("core"),
        targetMuscles = listOf("legs", "arms")
    )
)
