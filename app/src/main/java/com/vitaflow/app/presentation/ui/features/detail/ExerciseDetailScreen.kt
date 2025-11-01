package com.vitaflow.app.presentation.ui.features.detail

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.vitaflow.app.domain.models.Exercise

// Theme Colors - Consistent with HomeScreen
private val PrimaryGreen = Color(0xFF00C853)
private val LightGreen = Color(0xFF4CAF50)
private val DarkGreen = Color(0xFF2E7D32)
private val BackgroundWhite = Color(0xFFFAFAFA)
private val CardWhite = Color.White
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF666666)
private val AccentGreen = Color(0xFF66BB6A)

@Composable
private fun ExerciseMediaCard(exercise: Exercise) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image/GIF
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(exercise.gifUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = exercise.name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )

            // Green gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                PrimaryGreen.copy(alpha = 0.6f)
                            ),
                            startY = 200f
                        )
                    )
            )

            // Play button overlay
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(CardWhite.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(36.dp)
                )
            }

            // Rating badge
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                color = CardWhite.copy(alpha = 0.95f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "4.8",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseInfoCard(exercise: Exercise) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = exercise.name,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Duration",
                    value = "15 min",
                    icon = "⏱️",
                    color = PrimaryGreen
                )
                StatItem(
                    label = "Calories",
                    value = "120 kcal",
                    icon = "🔥",
                    color = LightGreen
                )
                StatItem(
                    label = "Level",
                    value = "Intermediate",
                    icon = "📊",
                    color = AccentGreen
                )
            }
        }
    }
}

@Composable
private fun StartWorkoutButton() {
    Button(
        onClick = { /* Handle start workout */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryGreen
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = null,
                tint = CardWhite,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Start Workout",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CardWhite
            )
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    subtitle: String,
    icon: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = icon,
                    fontSize = 20.sp
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }
            content()
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    color.copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MuscleChip(
    text: String,
    isPrimary: Boolean
) {
    val backgroundColor = if (isPrimary) PrimaryGreen else AccentGreen.copy(alpha = 0.15f)
    val textColor = if (isPrimary) CardWhite else PrimaryGreen

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Text(
            text = text.replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun EquipmentChip(text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = LightGreen.copy(alpha = 0.1f),
        modifier = Modifier.border(
            1.5.dp,
            LightGreen.copy(alpha = 0.3f),
            RoundedCornerShape(20.dp)
        )
    ) {
        Text(
            text = text.replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            color = DarkGreen,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun InstructionItem(
    stepNumber: Int,
    instruction: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    PrimaryGreen,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber.toString(),
                color = CardWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = instruction,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            color = TextPrimary,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun ExerciseDetailShimmer(paddingValues: PaddingValues) {
    val shimmerColors = listOf(
        PrimaryGreen.copy(alpha = 0.1f),
        PrimaryGreen.copy(alpha = 0.05f),
        PrimaryGreen.copy(alpha = 0.1f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translationAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translation"
    )

    fun shimmerBrush(): Brush {
        return Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(10f, 10f),
            end = Offset(translationAnimation.value, translationAnimation.value)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Exercise Media Card Shimmer
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(shimmerBrush())
                ) {
                    // Play button
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(CardWhite.copy(alpha = 0.9f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }

        // Exercise Info Card Shimmer
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Title shimmer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(shimmerBrush())
                    )

                    // Stats Row Shimmer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(3) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(CircleShape)
                                        .background(shimmerBrush())
                                )
                                Box(
                                    modifier = Modifier
                                        .width(60.dp)
                                        .height(16.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(shimmerBrush())
                                )
                                Box(
                                    modifier = Modifier
                                        .width(45.dp)
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(shimmerBrush())
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Cards Shimmer
        repeat(4) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header shimmer
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(shimmerBrush())
                            )
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(18.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(shimmerBrush())
                                )
                                Box(
                                    modifier = Modifier
                                        .width(80.dp)
                                        .height(14.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(shimmerBrush())
                                )
                            }
                        }

                        // Content shimmer
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .width(80.dp)
                                        .height(36.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(shimmerBrush())
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Button Shimmer
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(shimmerBrush())
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    navController: NavController,
    viewModel: ExerciseDetailViewModel = hiltViewModel()
) {
    var isFavorite by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val state = viewModel.state.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.exercise?.name ?: "",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
                },
                actions = {
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) PrimaryGreen else TextSecondary
                        )
                    }
                    IconButton(onClick = { /* Handle share */ }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            tint = PrimaryGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardWhite,
                    titleContentColor = TextPrimary
                ),
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        when {
            state.isLoading -> {
                ExerciseDetailShimmer(paddingValues = paddingValues)
            }

            state.error.isNotEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                text = "⚠️",
                                fontSize = 48.sp
                            )

                            Text(
                                text = "Something went wrong",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = state.error,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )

                            Button(
                                onClick = { navController.popBackStack() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryGreen
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "Go Back",
                                    color = CardWhite,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            state.exercise != null -> {
                val exercise = state.exercise
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Exercise Image/GIF Section
                    item {
                        ExerciseMediaCard(exercise = exercise)
                    }

                    // Exercise Info Section
                    item {
                        ExerciseInfoCard(exercise = exercise)
                    }

                    // Target Muscles Section
                    item {
                        SectionCard(
                            title = "Primary Muscles",
                            subtitle = "Main target muscle groups",
                            icon = "💪"
                        ) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(exercise.targetMuscles) { muscle ->
                                    MuscleChip(
                                        text = muscle,
                                        isPrimary = true
                                    )
                                }
                            }
                        }
                    }

                    // Secondary Muscles Section
                    if (exercise.secondaryMuscles.isNotEmpty()) {
                        item {
                            SectionCard(
                                title = "Secondary Muscles",
                                subtitle = "Supporting muscle groups",
                                icon = "🎯"
                            ) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(exercise.secondaryMuscles) { muscle ->
                                        MuscleChip(
                                            text = muscle,
                                            isPrimary = false
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Equipment Section
                    if (exercise.equipments.isNotEmpty()) {
                        item {
                            SectionCard(
                                title = "Equipment",
                                subtitle = "Required equipment for this exercise",
                                icon = "🏋️"
                            ) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(exercise.equipments) { equipment ->
                                        EquipmentChip(text = equipment)
                                    }
                                }
                            }
                        }
                    }

                    // Instructions Section
                    item {
                        SectionCard(
                            title = "How to Perform",
                            subtitle = "Step-by-step instructions",
                            icon = "📋"
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                exercise.instructions.forEachIndexed { index, instruction ->
                                    InstructionItem(
                                        stepNumber = index + 1,
                                        instruction = instruction
                                    )
                                }
                            }
                        }
                    }

                    // Action Button
                    item {
                        StartWorkoutButton()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ExerciseDetailScreenPreview() {
    ExerciseDetailScreen(navController = rememberNavController())
}