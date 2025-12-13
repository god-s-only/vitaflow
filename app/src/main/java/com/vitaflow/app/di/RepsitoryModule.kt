package com.vitaflow.app.di

import com.google.firebase.auth.FirebaseAuth
import com.vitaflow.app.data.local.NutritionDatabase
import com.vitaflow.app.data.local.NutritionPreferences
import com.vitaflow.app.data.local.StepsDAO
import com.vitaflow.app.data.local.StepsPreferences
import com.vitaflow.app.data.remote.HealthConnectService
import com.vitaflow.app.data.remote.SpoonacularAPI
import com.vitaflow.app.data.remote.WorkoutAPI
import com.vitaflow.app.data.repository.AuthRepositoryImpl
import com.vitaflow.app.data.repository.ExerciseRepositoryImpl
import com.vitaflow.app.data.repository.NutritionFoodRepositoryImpl
import com.vitaflow.app.data.repository.StepCounterRepositoryImpl
import com.vitaflow.app.domain.repository.AuthRepository
import com.vitaflow.app.domain.repository.ExerciseRepository
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import com.vitaflow.app.domain.repository.StepCounterRepository
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
    @Singleton
    fun provideRepository(auth: FirebaseAuth): AuthRepository{
        return AuthRepositoryImpl(auth)
    }

    @Provides
    @Singleton
    fun provideExerciseRepository(api: WorkoutAPI): ExerciseRepository{
        return ExerciseRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideSpoonacularRepository(
        api: SpoonacularAPI,
        db: NutritionDatabase,
        nutritionPreferences: NutritionPreferences
    ): NutritionFoodRepository {
        return NutritionFoodRepositoryImpl(api, db.nutritionDao(), nutritionPreferences)
    }

    @Provides
    @Singleton
    fun provideStepsRepository(
        stepsDao: StepsDAO,
        healthConnectService: HealthConnectService,
        stepsPreferences: StepsPreferences
    ): StepCounterRepository {
        return StepCounterRepositoryImpl(stepsDao, healthConnectService, stepsPreferences)
    }
    @Provides
    @Singleton
    fun provideStepsDao(database: NutritionDatabase): StepsDAO {
        return database.stepsDao()
    }
}