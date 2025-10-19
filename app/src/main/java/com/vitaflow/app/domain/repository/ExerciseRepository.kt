package com.vitaflow.app.domain.repository

import com.vitaflow.app.common.Resource
import com.vitaflow.app.domain.models.Exercise

interface ExerciseRepository {
    suspend fun getAllExercises(): Resource<List<Exercise>>
    suspend fun getExerciseById(exerciseId: String): Resource<Exercise>
    suspend fun getAllMuscles(): Resource<List<String>>
    suspend fun getAllBodyParts(): Resource<List<String>>
    suspend fun getAllEquipment(): Resource<List<String>>
    suspend fun getExercisesByBodyPart(bodyPartName: String): Resource<List<Exercise>>
    suspend fun getExercisesByMuscle(muscleName: String): Resource<List<Exercise>>
    suspend fun getExercisesByEquipment(equipmentName: String): Resource<List<Exercise>>
}