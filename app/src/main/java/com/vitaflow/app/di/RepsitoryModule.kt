package com.vitaflow.app.di

import com.google.firebase.auth.FirebaseAuth
import com.vitaflow.app.data.local.NutritionDatabase
import com.vitaflow.app.data.local.NutritionPreferences
import com.vitaflow.app.data.remote.SpoonacularAPI
import com.vitaflow.app.data.remote.WorkoutAPI
import com.vitaflow.app.data.repository.AuthRepositoryImpl
import com.vitaflow.app.data.repository.ExerciseRepositoryImpl
import com.vitaflow.app.data.repository.NutritionFoodRepositoryImpl
import com.vitaflow.app.domain.repository.AuthRepository
import com.vitaflow.app.domain.repository.ExerciseRepository
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepsitoryModule {

    @Provides
    @Binds
    fun provideRepository(auth: FirebaseAuth): AuthRepository{
        return AuthRepositoryImpl(auth)
    }

    @Provides
    @Binds
    fun provideExerciseRepository(api: WorkoutAPI): ExerciseRepository{
        return ExerciseRepositoryImpl(api)
    }

    @Provides
    @Binds
    fun provideSpoonacularRepository(
        api: SpoonacularAPI,
        db: NutritionDatabase,
        nutritionPreferences: NutritionPreferences
    ): NutritionFoodRepository {
        return NutritionFoodRepositoryImpl(api, db.nutritionDao(), nutritionPreferences)
    }
}