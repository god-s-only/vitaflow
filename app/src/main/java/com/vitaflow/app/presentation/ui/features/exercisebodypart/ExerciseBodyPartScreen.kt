package com.vitaflow.app.presentation.ui.features.exercisebodypart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

data class Exercise(
    val exerciseId: String,
    val name: String,
    val gifUrl: String,
    val targetMuscles: List<String>,
    val bodyParts: List<String>,
    val equipments: List<String>,
    val secondaryMuscles: List<String>,
    val instructions: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen() {
    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }

    val exercises = remember {
        listOf(
            Exercise(
                exerciseId = "LMGXZn8",
                name = "barbell decline close grip to skull press",
                gifUrl = "https://static.exercisedb.dev/media/LMGXZn8.gif",
                targetMuscles = listOf("triceps"),
                bodyParts = listOf("upper arms"),
                equipments = listOf("barbell"),
                secondaryMuscles = listOf("chest", "shoulders"),
                instructions = listOf(
                    "Lie on a decline bench with your head lower than your feet and hold a barbell with a close grip.",
                    "Lower the barbell towards your forehead by bending your elbows, keeping your upper arms stationary.",
                    "Pause for a moment, then extend your arms to press the barbell back up to the starting position.",
                    "Repeat for the desired number of repetitions."
                )
            ),
            Exercise(
                exerciseId = "a4F9Oyc",
                name = "kettlebell double alternating hang clean",
                gifUrl = "https://static.exercisedb.dev/media/a4F9Oyc.gif",
                targetMuscles = listOf("biceps"),
                bodyParts = listOf("upper arms"),
                equipments = listOf("kettlebell"),
                secondaryMuscles = listOf("forearms", "shoulders"),
                instructions = listOf(
                    "Stand with your feet shoulder-width apart, holding a kettlebell in each hand with an overhand grip.",
                    "Bend your knees slightly and hinge forward at the hips, keeping your back straight and chest up.",
                    "Allow the kettlebells to hang straight down in front of your body.",
                    "In one fluid motion, explosively extend your hips and knees while shrugging your shoulders.",
                    "As the kettlebells rise, pull them up towards your shoulders, keeping your elbows high and out to the sides.",
                    "Catch the kettlebells at shoulder height, with your palms facing inward and your elbows pointing forward.",
                    "Lower the kettlebells back down to the starting position and repeat for the desired number of repetitions."
                )
            ),
            Exercise(
                exerciseId = "gVlnLIJ",
                name = "cable reverse one arm curl",
                gifUrl = "https://static.exercisedb.dev/media/gVlnLIJ.gif",
                targetMuscles = listOf("biceps"),
                bodyParts = listOf("upper arms"),
                equipments = listOf("cable"),
                secondaryMuscles = listOf("forearms"),
                instructions = listOf(
                    "Stand facing a cable machine with your feet shoulder-width apart.",
                    "Grasp the cable handle with an underhand grip, palm facing down.",
                    "Keep your elbow close to your side and slowly curl your forearm up towards your shoulder.",
                    "Pause for a moment at the top, then slowly lower your forearm back down to the starting position.",
                    "Repeat for the desired number of repetitions."
                )
            ),
            Exercise(
                exerciseId = "x6KpKpq",
                name = "close-grip push-up",
                gifUrl = "https://static.exercisedb.dev/media/x6KpKpq.gif",
                targetMuscles = listOf("triceps"),
                bodyParts = listOf("upper arms"),
                equipments = listOf("body weight"),
                secondaryMuscles = listOf("chest", "shoulders"),
                instructions = listOf(
                    "Start in a high plank position with your hands placed close together, directly under your shoulders.",
                    "Engage your core and lower your body towards the ground, keeping your elbows close to your sides.",
                    "Push through your palms to extend your arms and return to the starting position.",
                    "Repeat for the desired number of repetitions."
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Upper Arms Exercises",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(exercises) { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    onClick = { selectedExercise = exercise }
                )
            }
        }
    }

    selectedExercise?.let { exercise ->
        ExerciseDetailsBottomSheet(
            exercise = exercise,
            onDismiss = { selectedExercise = null }
        )
    }
}

@Composable
fun ExerciseCard(
    exercise: Exercise,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = exercise.gifUrl,
                    contentDescription = exercise.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Text(
                        text = exercise.equipments.first().replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = exercise.name.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Primary: ${exercise.targetMuscles.joinToString { it.replaceFirstChar { c -> c.uppercase() } }}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (exercise.secondaryMuscles.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "Secondary: ${exercise.secondaryMuscles.joinToString { it.replaceFirstChar { c -> c.uppercase() } }}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = Color(0xFF00C853)
                    )
                ) {
                    Text("View Instructions")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailsBottomSheet(
    exercise: Exercise,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = exercise.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            AsyncImage(
                model = exercise.gifUrl,
                contentDescription = exercise.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            Divider()

            Text(
                text = "Instructions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            exercise.instructions.forEachIndexed { index, instruction ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Text(
                        text = instruction.removePrefix("Step:${index + 1} "),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Default() {
    ExercisesScreen()
}