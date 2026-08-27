package com.vitaflow.app.presentation.ui.features.workout

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vitaflow.app.common.UIEvent
import com.vitaflow.app.common.Routes
import kotlinx.coroutines.flow.collectLatest

// Color Palette
object WorkoutColors {
    val Primary = Color(0xFF2D6A4F)
    val PrimaryLight = Color(0xFF40916C)
    val PrimaryLighter = Color(0xFF52B788)
    val Surface = Color(0xFFFAFAFA)
    val OnSurface = Color(0xFF1A1A1A)
    val OnSurfaceVariant = Color(0xFF6B6B6B)
    val CardBg = Color(0xFFFFFFFF)
    val AccentOrange = Color(0xFFFF9800)
    val AccentBlue = Color(0xFF2196F3)
    val AccentPurple = Color(0xFF9C27B0)
    val AccentRed = Color(0xFFE53935)
    val SkeletonBase = Color(0xFFE0E0E0)
    val SkeletonHighlight = Color(0xFFF5F5F5)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutBodyPartsScreen(
    navController: NavController,
    viewModel: WorkoutBodyPartsViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val bodyPartsWithUI = state.value.bodyParts.map { it.toBodyPart() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UIEvent.Navigate -> {
                    navController.navigate(event.route)
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Workouts",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = WorkoutColors.OnSurface
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Text(
                            text = "←",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = WorkoutColors.OnSurface
                            ),
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { /* Filter/Settings */ }) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = "Filter",
                            tint = WorkoutColors.OnSurface
                        )
                    }
                }
            )
        },
        containerColor = WorkoutColors.Surface
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Stats Card
            StatsCard(bodyPartCount = bodyPartsWithUI.size)

            if (state.value.isLoading) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(10) {
                        BodyPartCardSkeleton()
                    }
                }
            } else if (state.value.error != null) {
                // Error state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "⚠️",
                            fontSize = 48.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            text = "Oops! Something went wrong",
                            style = MaterialTheme.typography.titleMedium,
                            color = WorkoutColors.OnSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = state.value.error ?: "Unknown error",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WorkoutColors.OnSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bodyPartsWithUI) { bodyPart ->
                        BodyPartCard(
                            bodyPart = bodyPart,
                            onClick = {
                                viewModel.onEvent(
                                    WorkoutBodyPartsEvent.OnBodyPartClick(bodyPart.name)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsCard(bodyPartCount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WorkoutColors.CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(
                value = bodyPartCount.toString(),
                label = "Body Parts",
                color = WorkoutColors.Primary
            )
            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp),
                color = WorkoutColors.SkeletonBase
            )
            StatItem(
                value = "50+",
                label = "Exercises",
                color = WorkoutColors.AccentOrange
            )
            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp),
                color = WorkoutColors.SkeletonBase
            )
            StatItem(
                value = "∞",
                label = "Possibilities",
                color = WorkoutColors.AccentPurple
            )
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = WorkoutColors.OnSurfaceVariant
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun BodyPartCard(
    bodyPart: BodyPart,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bodyPart.color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Decorative gradient circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .offset(x = (-20).dp, y = (-20).dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                bodyPart.color.copy(alpha = 0.2f),
                                bodyPart.color.copy(alpha = 0.0f)
                            ),
                            center = Offset(40f, 40f),
                            radius = 40f
                        ),
                        shape = CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Icon container
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(bodyPart.color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = bodyPart.icon,
                        contentDescription = bodyPart.name,
                        tint = bodyPart.color,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Text content
                Column {
                    Text(
                        text = bodyPart.name.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = WorkoutColors.OnSurface
                        )
                    )
                    Text(
                        text = "${bodyPart.exerciseCount} exercises",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = WorkoutColors.OnSurfaceVariant
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Arrow indicator
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "→",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = bodyPart.color
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun BodyPartCardSkeleton() {
    val shimmerColors = listOf(
        WorkoutColors.SkeletonBase,
        WorkoutColors.SkeletonHighlight,
        WorkoutColors.SkeletonBase
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(0f, 0f),
        end = Offset(
            x = translateAnimation.value * 200f,
            y = translateAnimation.value * 200f
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WorkoutColors.SkeletonBase),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(brush)
            )

            Column {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .width(80.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(brush)
                )
            }
        }
    }
}
