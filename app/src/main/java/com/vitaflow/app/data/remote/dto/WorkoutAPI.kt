package com.vitaflow.app.data.remote.dto

import retrofit2.Response
import retrofit2.http.GET

interface WorkoutAPI {
    @GET("/exercises")
    suspend fun getAllExercises(): Response<ExerciseDTO>
}