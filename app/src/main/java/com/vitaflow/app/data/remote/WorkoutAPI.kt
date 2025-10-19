package com.vitaflow.app.data.remote

import com.vitaflow.app.data.remote.dto.ExerciseDTO
import com.vitaflow.app.data.remote.dto.ExerciseRequest
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface WorkoutAPI {
    @GET("/exercises")
    suspend fun getAllExercises(): Response<ExerciseDTO>
    @GET("/exercises/{exerciseId}")
    suspend fun getExerciseById(@Path("exerciseId") exerciseId: String): Response<ExerciseRequest>
    @GET("/muscles")
    suspend fun getAllMuscles(): Response<ExerciseDTO>
    @GET("/bodyparts")
    suspend fun getAllBodyParts(): Response<ExerciseDTO>
    @GET("/equipments")
    suspend fun getAllEquipment(): Response<ExerciseDTO>
    @GET("/bodyparts/{bodyPartName}/exercises")
    suspend fun getExercisesByBodyPart(@Path("bodyPartName") bodyPartName: String): Response<ExerciseDTO>
    @GET("/muscles/{muscleName}/exercises")
    suspend fun getExercisesByMuscle(@Path("muscleName") muscleName: String): Response<ExerciseDTO>
    @GET("/equipments/{equipmentName}/exercises")
    suspend fun getExercisesByEquipment(@Path("equipmentName") equipmentName: String): Response<ExerciseDTO>

}