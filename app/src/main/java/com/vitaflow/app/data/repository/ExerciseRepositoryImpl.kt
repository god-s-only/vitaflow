package com.vitaflow.app.data.repository

import com.vitaflow.app.common.Resource
import com.vitaflow.app.common.safeApiCall
import com.vitaflow.app.data.remote.WorkoutAPI
import com.vitaflow.app.data.remote.dto.ExerciseRequest
import com.vitaflow.app.data.remote.dto.toExercise
import com.vitaflow.app.domain.models.Exercise
import com.vitaflow.app.domain.repository.ExerciseRepository
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(private val api: WorkoutAPI): ExerciseRepository {
    override suspend fun getAllExercises(): Resource<List<Exercise>> {
        val res = safeApiCall { api.getAllExercises() }
        return when (res) {
            is Resource.Success -> {
                Resource.Success(data = res.data?.data?.map { it.toExercise() } ?: emptyList())
            }
            is Resource.Error -> {
                Resource.Error(message = res.message ?: "Something went wrong")
            }
            is Resource.Loading -> {
                Resource.Loading()
            }
        }
    }

    override suspend fun getExerciseById(exerciseId: String): Resource<Exercise?> {
        val res = safeApiCall { api.getExerciseById(exerciseId) }
        return when (res) {
            is Resource.Success -> {
                Resource.Success(data = res.data?.data?.toExercise())
            }
            is Resource.Error -> {
                Resource.Error(message = res.message ?: "Something went wrong")
            }
            is Resource.Loading -> {
                Resource.Loading()
            }
        }
    }

    override suspend fun getAllMuscles(): Resource<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllBodyParts(): Resource<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllEquipment(): Resource<List<String>> {
        TODO("Not yet implemented")
    }

    override suspend fun getExercisesByBodyPart(bodyPartName: String): Resource<List<Exercise>> {
        TODO("Not yet implemented")
    }

    override suspend fun getExercisesByMuscle(muscleName: String): Resource<List<Exercise>> {
        TODO("Not yet implemented")
    }

    override suspend fun getExercisesByEquipment(equipmentName: String): Resource<List<Exercise>> {
        TODO("Not yet implemented")
    }

}