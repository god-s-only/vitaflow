package com.vitaflow.app.domain.usecase.exercise

import com.vitaflow.app.common.Resource
import com.vitaflow.app.domain.models.Exercise
import com.vitaflow.app.domain.repository.ExerciseRepository
import javax.inject.Inject

class GetExerciseUseCase @Inject constructor(private val repository: ExerciseRepository) {
    suspend operator fun invoke(): Resource<List<Exercise>> {
        return repository.getAllExercises()
    }
}