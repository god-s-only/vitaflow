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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

data class BodyPart(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutBodyPartsScreen(
    onBackClick: () -> Unit = {},
    onBodyPartClick: (String) -> Unit = {},
    isLoading: Boolean = false
) {
    val bodyParts = remember {
        listOf(
            BodyPart("Neck", Icons.Default.Settings, WorkoutColors.AccentPurple),
            BodyPart("Lower Arms", Icons.Default.Settings, WorkoutColors.AccentBlue),
            BodyPart("Shoulders", Icons.Default.Settings, WorkoutColors.AccentOrange),
            BodyPart("Cardio", Icons.Default.Favorite, WorkoutColors.AccentRed),
            BodyPart("Upper Arms", Icons.Default.Settings, WorkoutColors.Primary),
            BodyPart("Chest", Icons.Default.Settings, WorkoutColors.PrimaryLight),
            BodyPart("Lower Legs", Icons.Default.Settings, WorkoutColors.AccentBlue),
            BodyPart("Back", Icons.Default.Settings, WorkoutColors.PrimaryLighter),
            BodyPart("Upper Legs", Icons.Default.Settings, WorkoutColors.AccentOrange),
            BodyPart("Waist", Icons.Default.Settings, WorkoutColors.AccentPurple)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Body Parts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                        Text(
                            text = "Choose a muscle group to train",
                            style = MaterialTheme.typography.bodySmall,
                            color = WorkoutColors.OnSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = WorkoutColors.OnSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { /* Filter/Settings */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
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
            StatsCard()

            if (isLoading) {
                // Skeleton Loader
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
            } else {
                // Body Parts Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bodyParts) { bodyPart ->
                        BodyPartCard(
                            bodyPart = bodyPart,
                            onClick = { onBodyPartClick(bodyPart.name.lowercase()) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WorkoutColors.Primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Ready to Train?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "10 muscle groups available",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun BodyPartCard(
    bodyPart: BodyPart,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            bodyPart.color.copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon Container
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    bodyPart.color.copy(alpha = 0.2f),
                                    bodyPart.color.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = bodyPart.icon,
                        contentDescription = bodyPart.name,
                        modifier = Modifier.size(40.dp),
                        tint = bodyPart.color
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Body Part Name
                Text(
                    text = bodyPart.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = WorkoutColors.OnSurface,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }

            // Arrow indicator
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(20.dp),
                tint = bodyPart.color.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun BodyPartCardSkeleton() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            WorkoutColors.SkeletonBase,
            WorkoutColors.SkeletonHighlight,
            WorkoutColors.SkeletonBase
        ),
        start = Offset(translateAnim - 1000f, translateAnim - 1000f),
        end = Offset(translateAnim, translateAnim)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon skeleton
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(shimmerBrush)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmerBrush)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle skeleton
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmerBrush)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WorkoutBodyPartsPreview() {
    WorkoutBodyPartsScreen()
}

@Preview(showBackground = true)
@Composable
private fun WorkoutBodyPartsLoadingPreview() {
    WorkoutBodyPartsScreen(isLoading = true)
}