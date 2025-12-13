package com.vitaflow.app.domain.usecase.exercise

import com.vitaflow.app.domain.repository.ExerciseRepository
import javax.inject.Inject

class GetWorkoutByBodyPart @Inject constructor(private val exerciseRepository: ExerciseRepository) {
    suspend operator fun invoke(bodyPart: String) = exerciseRepository.getExercisesByBodyPart(bodyPart)
}