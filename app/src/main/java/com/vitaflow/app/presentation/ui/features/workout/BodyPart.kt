package com.vitaflow.app.presentation.ui.features.workout

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsMartialArts
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class BodyPart(
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val exerciseCount: Int = 0
)

fun String.toBodyPart(): BodyPart {
    return when (this.lowercase()) {
        "back" -> BodyPart(
            name = this,
            icon = Icons.Default.FitnessCenter,
            color = Color(0xFF2196F3) // Blue
        )
        "cardio" -> BodyPart(
            name = this,
            icon = Icons.Default.Favorite,
            color = Color(0xFFE53935) // Red
        )
        "chest" -> BodyPart(
            name = this,
            icon = Icons.Default.FitnessCenter,
            color = Color(0xFFFF9800) // Orange
        )
        "lower arms" -> BodyPart(
            name = this,
            icon = Icons.Default.SportsMartialArts,
            color = Color(0xFF9C27B0) // Purple
        )
        "lower legs" -> BodyPart(
            name = this,
            icon = Icons.AutoMirrored.Filled.DirectionsRun,
            color = Color(0xFF4CAF50) // Green
        )
        "neck" -> BodyPart(
            name = this,
            icon = Icons.Default.Person,
            color = Color(0xFF00BCD4) // Cyan
        )
        "shoulders" -> BodyPart(
            name = this,
            icon = Icons.Default.FitnessCenter,
            color = Color(0xFFFF5722) // Deep Orange
        )
        "upper arms" -> BodyPart(
            name = this,
            icon = Icons.Default.SportsMartialArts,
            color = Color(0xFF673AB7) // Deep Purple
        )
        "upper legs" -> BodyPart(
            name = this,
            icon = Icons.AutoMirrored.Filled.DirectionsRun,
            color = Color(0xFF3F51B5) // Indigo
        )
        "waist" -> BodyPart(
            name = this,
            icon = Icons.Default.Accessibility,
            color = Color(0xFFFFEB3B) // Yellow
        )
        else -> BodyPart(
            name = this,
            icon = Icons.Default.FitnessCenter,
            color = Color(0xFF9E9E9E) // Grey
        )
    }
}