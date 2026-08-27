package com.vitaflow.app.presentation.ui.features.steps

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vitaflow.app.domain.models.DailySteps
import com.vitaflow.app.domain.models.StepsData
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.cos
import kotlin.math.sin

// Color Palette
object StepsColors {
    val Primary = Color(0xFF2D6A4F)
    val PrimaryLight = Color(0xFF40916C)
    val PrimaryLighter = Color(0xFF52B788)
    val Surface = Color(0xFFFAFAFA)
    val OnSurface = Color(0xFF1A1A1A)
    val OnSurfaceVariant = Color(0xFF6B6B6B)
    val CardBg = Color(0xFFFFFFFF)
    val ProgressBg = Color(0xFFE8F5E9)
    val AccentOrange = Color(0xFFFF9800)
    val AccentBlue = Color(0xFF2196F3)
}



@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepsTrackerScreen(
    stepsData: StepsData = StepsData()
) {
    val progress = (stepsData.currentSteps.toFloat() / stepsData.targetSteps.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic),
        label = "progress"
    )

    Scaffold(

    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(StepsColors.Surface)
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Main Steps Circle
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Today's Steps",
                            style = MaterialTheme.typography.titleMedium,
                            color = StepsColors.OnSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Circular Progress
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(240.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = animatedProgress,
                                stepsData = stepsData
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${stepsData.currentSteps}",
                                    style = MaterialTheme.typography.displayMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = StepsColors.Primary,
                                    fontSize = 48.sp
                                )
                                Text(
                                    text = "of ${stepsData.targetSteps} steps",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = StepsColors.OnSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${(progress * 100).toInt()}% Complete",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = StepsColors.PrimaryLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Remaining Steps
                        val remainingSteps = (stepsData.targetSteps - stepsData.currentSteps).coerceAtLeast(0)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = StepsColors.ProgressBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = StepsColors.Primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = if (remainingSteps > 0)
                                        "$remainingSteps steps to go!"
                                    else
                                        "Goal achieved! 🎉",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = StepsColors.Primary
                                )
                            }
                        }
                    }
                }
            }

            // Stats Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        icon = Icons.Default.Settings,
                        value = "${stepsData.caloriesBurned}",
                        label = "Calories",
                        color = StepsColors.AccentOrange,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.Default.Place,
                        value = "${stepsData.distanceKm}",
                        label = "Km",
                        color = StepsColors.AccentBlue,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        icon = Icons.Default.Settings,
                        value = "${stepsData.activeMinutes}",
                        label = "Minutes",
                        color = StepsColors.PrimaryLight,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.Default.Settings,
                        value = "${((stepsData.currentSteps.toFloat() / stepsData.targetSteps) * 100).toInt()}%",
                        label = "Goal",
                        color = StepsColors.Primary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Weekly Progress
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Weekly Progress",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = StepsColors.OnSurface
                            )

                            val weeklyAverage = stepsData.weeklyData.map { it.steps }.average().toInt()
                            Text(
                                text = "Avg: $weeklyAverage",
                                style = MaterialTheme.typography.bodyMedium,
                                color = StepsColors.OnSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Bar Chart
                        WeeklyBarChart(
                            data = stepsData.weeklyData,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
            }

            // Daily History
            item {
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = StepsColors.OnSurface
                )
            }

            items(stepsData.weeklyData.reversed()) { dailySteps ->
                DailyStepsItem(dailySteps)
            }
        }
    }
}

@Composable
fun CircularProgressIndicator(
    progress: Float,
    stepsData: StepsData
) {
    Canvas(modifier = Modifier.size(240.dp)) {
        val strokeWidth = 24.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)

        // Background circle
        drawCircle(
            color = StepsColors.ProgressBg,
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Progress arc
        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    StepsColors.PrimaryLighter,
                    StepsColors.PrimaryLight,
                    StepsColors.Primary
                )
            ),
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            topLeft = Offset(
                center.x - radius,
                center.y - radius
            ),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = StepsColors.OnSurface
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = StepsColors.OnSurfaceVariant
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklyBarChart(
    data: List<DailySteps>,
    modifier: Modifier = Modifier
) {
    val maxSteps = (data.maxOfOrNull { it.steps } ?: 10000).coerceAtLeast(1)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { dailySteps ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Bar
                val heightFraction = (dailySteps.steps.toFloat() / maxSteps).coerceIn(0.1f, 1f)
                val isGoalMet = dailySteps.steps >= dailySteps.targetSteps

                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .fillMaxHeight(heightFraction)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(
                            if (isGoalMet)
                                Brush.verticalGradient(
                                    colors = listOf(StepsColors.Primary, StepsColors.PrimaryLight)
                                )
                            else
                                Brush.verticalGradient(
                                    colors = listOf(
                                        StepsColors.OnSurfaceVariant.copy(alpha = 0.3f),
                                        StepsColors.OnSurfaceVariant.copy(alpha = 0.2f)
                                    )
                                )
                        )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Day label
                Text(
                    text = dailySteps.date.format(DateTimeFormatter.ofPattern("EEE")).take(1),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (dailySteps.date == LocalDate.now())
                        StepsColors.Primary
                    else
                        StepsColors.OnSurfaceVariant,
                    fontWeight = if (dailySteps.date == LocalDate.now())
                        FontWeight.Bold
                    else
                        FontWeight.Normal
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyStepsItem(dailySteps: DailySteps) {
    val progress = (dailySteps.steps.toFloat() / dailySteps.targetSteps).coerceIn(0f, 1f)
    val isToday = dailySteps.date == LocalDate.now()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isToday) StepsColors.ProgressBg else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isToday) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date
            Column(
                modifier = Modifier.width(70.dp)
            ) {
                Text(
                    text = if (isToday) "Today" else dailySteps.date.format(DateTimeFormatter.ofPattern("MMM dd")),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Medium,
                    color = if (isToday) StepsColors.Primary else StepsColors.OnSurface
                )
                Text(
                    text = dailySteps.date.format(DateTimeFormatter.ofPattern("EEEE")),
                    style = MaterialTheme.typography.bodySmall,
                    color = StepsColors.OnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Progress Bar
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${dailySteps.steps} steps",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = StepsColors.OnSurface
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = StepsColors.OnSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (progress >= 1f) StepsColors.Primary else StepsColors.PrimaryLight,
                    trackColor = StepsColors.OnSurfaceVariant.copy(alpha = 0.1f)
                )
            }

            // Goal indicator
            if (dailySteps.steps >= dailySteps.targetSteps) {
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Goal achieved",
                    tint = StepsColors.Primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
private fun StepsTrackerPreview() {
    StepsTrackerScreen()
}