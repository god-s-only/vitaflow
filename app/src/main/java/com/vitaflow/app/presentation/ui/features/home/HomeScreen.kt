package com.vitaflow.app.presentation.ui.features.home

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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.vitaflow.app.domain.models.Exercise
import kotlin.random.Random

// App Theme Colors
val PrimaryGreen = Color(0xFF00C853)
val LightGreen = Color(0xFF4CAF50)
val DarkGreen = Color(0xFF2E7D32)
val BackgroundWhite = Color(0xFFFAFAFA)
val CardWhite = Color.White
val TextPrimary = Color(0xFF1A1A1A)
val TextSecondary = Color(0xFF666666)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val uiState = viewModel.state.collectAsStateWithLifecycle()

    // Random character image for featured workout
    val randomAnimeCharacter by remember {
        mutableStateOf(animeCharacterImages.random())
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = CardWhite,
                    titleContentColor = TextPrimary
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
        containerColor = BackgroundWhite
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                // Welcome Header
                WelcomeHeader()
            }

            item {
                // Featured Workout Card
                FeaturedWorkoutCard(randomAnimeCharacter)
            }

            item {
                // Workout Types Section
                WorkoutTypesSection(uiState, navController)
            }

            item {
                // Quick Training Section (Redesigned Additional Training)
                QuickTrainingSection()
            }
        }
    }
}

@Composable
private fun WelcomeHeader() {
    Column {
        Text(
            text = "Hello, Fitness Enthusiast! 👋",
            fontSize = 16.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Let's Get Moving Today",
            fontSize = 28.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FeaturedWorkoutCard(character: AnimeCharacter) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(character.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = character.name,
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
                // Top badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Badge(
                        text = "Featured",
                        backgroundColor = CardWhite.copy(alpha = 0.9f),
                        textColor = PrimaryGreen
                    )
                    Badge(
                        text = "Full Body",
                        backgroundColor = PrimaryGreen.copy(alpha = 0.8f),
                        textColor = CardWhite
                    )
                }

                // Bottom content
                Column {
                    Text(
                        text = "Today's Featured Workout",
                        fontSize = 18.sp,
                        color = CardWhite,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        InfoChip(icon = "🔥", text = "350 kcal")
                        InfoChip(icon = "⏱️", text = "45 min")
                        InfoChip(icon = "⭐", text = "4.8")
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutTypesSection(
    uiState: State<com.vitaflow.app.presentation.ui.features.home.HomeState>,
    navController: NavController
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Workout Categories",
                fontSize = 22.sp,
                color = TextPrimary,
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

        Spacer(modifier = Modifier.height(16.dp))

        when {
            uiState.value.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            }

            uiState.value.error != null -> {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(sampleExercises) { exercise ->
                        WorkoutTypeCard(exercise = exercise) {}
                    }
                }
            }

            uiState.value.exercises.isEmpty() -> {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(sampleExercises) { exercise ->
                        WorkoutTypeCard(exercise = exercise) {}
                    }
                }
            }

            else -> {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.value.exercises) { exercise ->
                        WorkoutTypeCard(exercise = exercise) {
                            navController.navigate(Routes.WORKOUTSCREEN + "/${exercise.exerciseId}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickTrainingSection() {
    Column {
        Text(
            text = "Quick Training Sessions",
            fontSize = 22.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            quickTrainingItems.forEach { training ->
                QuickTrainingCard(training = training)
            }
        }
    }
}

@Composable
private fun QuickTrainingCard(training: QuickTraining) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Navigate to workout */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon/Image placeholder
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
                Text(
                    text = training.emoji,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = training.name,
                    fontSize = 16.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = training.description,
                    fontSize = 14.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoTag(text = "${training.duration} min", color = PrimaryGreen)
                    InfoTag(text = "${training.calories} kcal", color = LightGreen)
                    InfoTag(text = training.level, color = DarkGreen)
                }
            }

            // Arrow
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_send),
                contentDescription = "Start workout",
                tint = PrimaryGreen,
                modifier = Modifier.size(20.dp)
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
            color = CardWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun InfoTag(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(
                color.copy(alpha = 0.1f),
                RoundedCornerShape(8.dp)
            )
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
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image
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

            // Green gradient overlay
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

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = exercise.name.take(20),
                    color = CardWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Start Workout",
                    color = CardWhite.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Data classes
data class QuickTraining(
    val name: String,
    val description: String,
    val duration: Int,
    val calories: Int,
    val level: String,
    val emoji: String
)

data class AnimeCharacter(
    val name: String,
    val anime: String,
    val imageUrl: String
)

// Sample data with updated content
val quickTrainingItems = listOf(
    QuickTraining(
        name = "Morning Energy Boost",
        description = "Quick cardio to start your day",
        duration = 15,
        calories = 120,
        level = "Beginner",
        emoji = "🌅"
    ),
    QuickTraining(
        name = "Core Power Session",
        description = "Strengthen your core muscles",
        duration = 20,
        calories = 150,
        level = "Intermediate",
        emoji = "💪"
    ),
    QuickTraining(
        name = "HIIT Fat Burner",
        description = "High intensity interval training",
        duration = 25,
        calories = 200,
        level = "Advanced",
        emoji = "🔥"
    ),
    QuickTraining(
        name = "Flexibility Flow",
        description = "Improve mobility and flexibility",
        duration = 18,
        calories = 80,
        level = "All Levels",
        emoji = "🧘"
    )
)

val animeCharacterImages = listOf(
    AnimeCharacter(
        "Fitness Hero",
        "Training Legends",
        "https://img.freepik.com/free-photo/portrait-anime-character-doing-fitness-exercising_23-2151666671.jpg"
    ),
    AnimeCharacter(
        "Workout Master",
        "Gym Champions",
        "https://img.freepik.com/free-photo/portrait-anime-character-doing-fitness-exercising_23-2151666669.jpg"
    ),
    AnimeCharacter(
        "Strength Builder",
        "Power Training",
        "https://img.freepik.com/free-photo/fit-cartoon-character-training_23-2151149035.jpg"
    )
)

// Sample exercises with green theme
val sampleExercises = listOf(
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

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}