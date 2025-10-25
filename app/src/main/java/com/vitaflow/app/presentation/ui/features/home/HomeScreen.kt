package com.vitaflow.app.presentation.ui.features.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.vitaflow.app.domain.models.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val uiState = viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.Black
                ),
                title = {
                    Text(
                        text = "Vita Flow",
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )
                },
                actions = {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Notifications"
                    )
                }
            )
        },
        bottomBar = {
            BottomNavigationBar()
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                // Header Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Hello, User!",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            text = "Stay Fit & Healthy",
                            fontSize = 24.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                // Trending Workouts Section
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Trending Workouts",
                            fontSize = 20.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "🔥",
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Featured Workout Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Background with gradient overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Gray)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.7f)
                                            )
                                        )
                                    )
                            )

                            // Content
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Top badges
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Badge(
                                        text = "Beginner",
                                        backgroundColor = Color.White.copy(alpha = 0.3f)
                                    )
                                    Badge(
                                        text = "Full body",
                                        backgroundColor = Color.White.copy(alpha = 0.3f)
                                    )
                                }

                                // Bottom content
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Column {
                                        Text(
                                            text = "Push Your Limit",
                                            fontSize = 24.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    painter = painterResource(id = android.R.drawable.ic_menu_info_details),
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = "350 kcal",
                                                    color = Color.White,
                                                    fontSize = 14.sp
                                                )
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    painter = painterResource(id = android.R.drawable.ic_menu_recent_history),
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = "1h 25min",
                                                    color = Color.White,
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                    }

                                    // Rating
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color.Yellow,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "4.8",
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                // Workout Types Section
                Column {
                    Text(
                        text = "Workout types",
                        fontSize = 20.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Debug info
                    if (uiState.value.isLoading) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (uiState.value.error != null) {
                        Text(
                            text = "Error: ${uiState.value.error}",
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Show sample exercises as fallback
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(sampleExercises) { exercise ->
                                WorkoutTypeCard(exercise = exercise)
                            }
                        }
                    } else if (uiState.value.exercises.isEmpty()) {
                        Text(
                            text = "No exercises available, showing samples",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Show sample exercises as fallback
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(sampleExercises) { exercise ->
                                WorkoutTypeCard(exercise = exercise)
                            }
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.value.exercises) { exercise ->
                                WorkoutTypeCard(exercise = exercise)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                // Additional Training Section
                Column {
                    Text(
                        text = "Additional training",
                        fontSize = 20.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column {
                        additionalTraining.forEachIndexed { index, training ->
                            AdditionalTrainingItem(training = training)
                            if (index < additionalTraining.size - 1) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Badge(
    text: String,
    backgroundColor: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun WorkoutTypeCard(
    exercise: Exercise
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .width(140.dp)
            .height(160.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background Image with fallback
            if (exercise.gifUrl.isNotEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(exercise.gifUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = exercise.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onSuccess = {
                        println("Successfully loaded: ${exercise.gifUrl}")
                    },
                    onError = { error ->
                        println("Error loading exercise image: ${error.result.throwable}")
                        println("Failed URL: ${exercise.gifUrl}")
                    },
                    onLoading = {
                        println("Loading: ${exercise.gifUrl}")
                    }
                )
            } else {
                // Fallback background if no image
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF6C63FF),
                                    Color(0xFF4CAF50)
                                )
                            )
                        )
                )
            }

            // Gradient overlay for better text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 0f,
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
                    text = exercise.name.take(25), // Limit text length
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Workout",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun AdditionalTrainingItem(
    training: AdditionalTraining
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray)
        )

        // Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = training.name,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Green)
                    )
                    Text(
                        text = "${training.calories} kcal",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.Green)
                    )
                    Text(
                        text = training.duration,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = training.level,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun BottomNavigationBar() {
    var selectedIndex by remember { mutableStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 12.dp,
        tonalElevation = 8.dp
    ) {
        NavigationBar(
            containerColor = Color.White,
            contentColor = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            tonalElevation = 0.dp
        ) {
            bottomNavItems.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex

                NavigationBarItem(
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = if (isSelected) Color.Black else Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                modifier = Modifier.size(24.dp),
                                tint = if (isSelected) Color.White else Color.Gray
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        selectedIndex = index
                        // Handle navigation based on item
                        when (item.title) {
                            "Home" -> { /* Already on home */
                            }

                            "Search" -> { /* Navigate to search */
                            }

                            "Workout" -> { /* Navigate to workout */
                            }

                            "Articles" -> { /* Navigate to articles */
                            }

                            "Profile" -> { /* Navigate to profile */
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Transparent,
                        unselectedIconColor = Color.Transparent,
                        selectedTextColor = Color.Black,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent,
                        disabledIconColor = Color.Gray,
                        disabledTextColor = Color.Gray
                    ),
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }
    }
}

// Data classes
data class WorkoutType(
    val name: String,
    val sessions: Int
)

data class AdditionalTraining(
    val name: String,
    val calories: Int,
    val duration: String,
    val level: String
)

data class BottomNavItem(
    val title: String,
    val icon: ImageVector
)

// Sample data
val workoutTypes = listOf(
    WorkoutType("Hiit", 12),
    WorkoutType("Amrap", 15),
    WorkoutType("For time", 8)
)

val additionalTraining = listOf(
    AdditionalTraining("Deep Amrap Burner", 125, "15min", "Beginner • Full body"),
    AdditionalTraining("Deep Butt Sculp", 180, "20min", "Intermediate • Lower body")
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Outlined.Home),
    BottomNavItem("Search", Icons.Outlined.Search),
    BottomNavItem("Workout", Icons.Default.Star),
    BottomNavItem("Articles", Icons.Default.Menu),
    BottomNavItem("Profile", Icons.Outlined.Person)
)

// Sample exercises
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
    )
)

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}