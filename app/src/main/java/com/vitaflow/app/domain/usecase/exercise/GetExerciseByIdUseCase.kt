package com.vitaflow.app.domain.usecase.exercise

import com.vitaflow.app.domain.repository.ExerciseRepository
import javax.inject.Inject

class GetExerciseByIdUseCase @Inject constructor(private val exerciseRepository: ExerciseRepository) {
    suspend operator fun invoke(exerciseId: String) = exerciseRepository.getExerciseById(exerciseId)
}