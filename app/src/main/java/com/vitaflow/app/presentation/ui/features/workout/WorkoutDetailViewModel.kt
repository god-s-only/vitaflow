package com.vitaflow.app.presentation.ui.features.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaflow.app.domain.models.FeaturedWorkout
import com.vitaflow.app.domain.models.QuickTraining
import com.vitaflow.app.domain.models.WorkoutExercise
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutDetailState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val workout: FeaturedWorkout? = null,
    val quickTraining: QuickTraining? = null,
    val exercises: List<WorkoutExercise> = emptyList()
)

@HiltViewModel
class WorkoutDetailViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutDetailState())
    val uiState: StateFlow<WorkoutDetailState> = _uiState.asStateFlow()

    fun loadWorkout(workoutId: String) {
        viewModelScope.launch {
            _uiState.value = WorkoutDetailState(isLoading = true)

            // For now, simulate loading - in production, this would call the API
            // TODO: Replace with actual API call to get workout details

            // Simulate loading delay
            delay(500)

            // Check if it's a quick training ID or featured workout ID
            when {
                workoutId.startsWith("qt_") || workoutId.startsWith("quick_") -> {
                    // Load as quick training
                    _uiState.value = WorkoutDetailState(
                        quickTraining = getMockQuickTraining(workoutId),
                        isLoading = false
                    )
                }

                else -> {
                    // Load as featured workout
                    _uiState.value = WorkoutDetailState(
                        workout = getMockFeaturedWorkout(workoutId),
                        exercises = getMockExercises(),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun getMockFeaturedWorkout(id: String): FeaturedWorkout {
        return FeaturedWorkout(
            id = id,
            title = "Full Body HIIT Blast",
            description = "A high-intensity full body workout designed to burn calories and build strength. This workout combines cardio and strength training for maximum results.",
            imageUrl = "https://images.unsplash.com/photo-1517836357463-d25dfeac3438?w=800",
            category = "HIIT",
            difficulty = "Intermediate",
            duration = 30,
            calories = 350,
            rating = 4.8,
            exercises = getMockExercises()
        )
    }

    private fun getMockQuickTraining(id: String): QuickTraining {
        val trainings = mapOf(
            "qt_1" to QuickTraining(
                id = "qt_1",
                name = "Morning Energy Boost",
                description = "Start your day with this energizing cardio session. Perfect for waking up your body and mind.",
                duration = 15,
                calories = 120,
                difficulty = "Beginner",
                emoji = "🌅",
                category = "Cardio",
                imageUrl = null,
                exerciseCount = 5
            ),
            "qt_2" to QuickTraining(
                id = "qt_2",
                name = "Core Power Session",
                description = "Strengthen your core muscles with this targeted workout. Great for improving stability and posture.",
                duration = 20,
                calories = 150,
                difficulty = "Intermediate",
                emoji = "💪",
                category = "Strength",
                imageUrl = null,
                exerciseCount = 6
            ),
            "qt_3" to QuickTraining(
                id = "qt_3",
                name = "HIIT Fat Burner",
                description = "Maximum calorie burn in minimum time. This high-intensity workout will push your limits.",
                duration = 25,
                calories = 200,
                difficulty = "Advanced",
                emoji = "🔥",
                category = "HIIT",
                imageUrl = null,
                exerciseCount = 8
            ),
            "qt_4" to QuickTraining(
                id = "qt_4",
                name = "Flexibility Flow",
                description = "Improve your mobility and flexibility with this yoga-inspired session. Perfect for recovery days.",
                duration = 18,
                calories = 80,
                difficulty = "All Levels",
                emoji = "🧘",
                category = "Yoga",
                imageUrl = null,
                exerciseCount = 7
            )
        )
        return trainings[id] ?: trainings["qt_1"]!!
    }

    private fun getMockExercises(): List<WorkoutExercise> {
        return listOf(
            WorkoutExercise(
                id = "ex_1",
                name = "Jumping Jacks",
                gifUrl = "https://media.giphy.com/media/26FLdmIp6wJr91JAI/giphy.gif",
                sets = 3,
                reps = 20,
                duration = null,
                restTime = 30
            ),
            WorkoutExercise(
                id = "ex_2",
                name = "Push-ups",
                gifUrl = "https://media.giphy.com/media/ZdUnQS4LXNWnII2HIv/giphy.gif",
                sets = 3,
                reps = 15,
                duration = null,
                restTime = 45
            ),
            WorkoutExercise(
                id = "ex_3",
                name = "Mountain Climbers",
                gifUrl = null,
                sets = 3,
                reps = null,
                duration = 30,
                restTime = 30
            ),
            WorkoutExercise(
                id = "ex_4",
                name = "Squats",
                gifUrl = null,
                sets = 3,
                reps = 20,
                duration = null,
                restTime = 30
            ),
            WorkoutExercise(
                id = "ex_5",
                name = "Plank",
                gifUrl = null,
                sets = 3,
                reps = null,
                duration = 45,
                restTime = 30
            ),
            WorkoutExercise(
                id = "ex_6",
                name = "Burpees",
                gifUrl = null,
                sets = 3,
                reps = 10,
                duration = null,
                restTime = 60
            )
        )
    }
}
