package com.vitaflow.app.data.repository

import com.vitaflow.app.common.Resource
import com.vitaflow.app.data.remote.WorkoutApiService
import com.vitaflow.app.data.remote.dto.FeaturedWorkoutDTO
import com.vitaflow.app.data.remote.dto.QuickTrainingDTO
import com.vitaflow.app.domain.models.FeaturedWorkout
import com.vitaflow.app.domain.models.QuickTraining
import com.vitaflow.app.domain.models.WorkoutExercise
import com.vitaflow.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepositoryImpl @Inject constructor(
    private val apiService: WorkoutApiService
) : WorkoutRepository {

    override suspend fun getFeaturedWorkout(): Flow<Resource<FeaturedWorkout>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getFeaturedWorkout()
            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()?.workout
                if (dto != null) {
                    emit(Resource.Success(mapFeaturedWorkout(dto)))
                } else {
                    emit(Resource.Error("No featured workout available"))
                }
            } else {
                emit(Resource.Error(response.body()?.message ?: "Failed to load featured workout"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unexpected error occurred"))
        }
    }

    override suspend fun getQuickTrainings(limit: Int): Flow<Resource<List<QuickTraining>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getQuickTrainings(limit)
            if (response.isSuccessful && response.body()?.success == true) {
                val trainings = response.body()?.trainings?.map { mapQuickTraining(it) } ?: emptyList()
                emit(Resource.Success(trainings))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Failed to load trainings"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unexpected error occurred"))
        }
    }

    override suspend fun getTrainingsByCategory(
        category: String?,
        difficulty: String?,
        limit: Int
    ): Flow<Resource<List<QuickTraining>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getTrainingsByCategory(category, difficulty, limit)
            if (response.isSuccessful && response.body()?.success == true) {
                val trainings = response.body()?.trainings?.map { mapQuickTraining(it) } ?: emptyList()
                emit(Resource.Success(trainings))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Failed to load trainings"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unexpected error occurred"))
        }
    }

    private fun mapFeaturedWorkout(dto: FeaturedWorkoutDTO): FeaturedWorkout {
        return FeaturedWorkout(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            imageUrl = dto.imageUrl,
            category = dto.category,
            difficulty = dto.difficulty,
            duration = dto.duration,
            calories = dto.calories,
            rating = dto.rating,
            exercises = dto.exercises?.map { exerciseDto ->
                WorkoutExercise(
                    id = exerciseDto.id,
                    name = exerciseDto.name,
                    gifUrl = exerciseDto.gifUrl,
                    sets = exerciseDto.sets,
                    reps = exerciseDto.reps,
                    duration = exerciseDto.duration,
                    restTime = exerciseDto.restTime
                )
            } ?: emptyList()
        )
    }

    private fun mapQuickTraining(dto: QuickTrainingDTO): QuickTraining {
        return QuickTraining(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            duration = dto.duration,
            calories = dto.calories,
            difficulty = dto.difficulty,
            emoji = dto.emoji,
            category = dto.category,
            imageUrl = dto.imageUrl,
            exerciseCount = dto.exerciseCount
        )
    }
}
