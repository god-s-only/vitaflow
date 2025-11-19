package com.vitaflow.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.firebase.auth.FirebaseAuth
import com.vitaflow.app.common.BASE_URL
import com.vitaflow.app.common.SPOONACULAR_API
import com.vitaflow.app.data.local.NutritionDatabase
import com.vitaflow.app.data.remote.SpoonacularAPI
import com.vitaflow.app.data.remote.WorkoutAPI
import com.vitaflow.app.data.repository.AuthRepositoryImpl
import com.vitaflow.app.data.repository.ExerciseRepositoryImpl
import com.vitaflow.app.data.repository.NutritionFoodRepositoryImpl
import com.vitaflow.app.domain.repository.AuthRepository
import com.vitaflow.app.domain.repository.ExerciseRepository
import com.vitaflow.app.domain.repository.NutritionFoodRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RapidApiClient

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class SpoonacularClient

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRepository(auth: FirebaseAuth): AuthRepository{
        return AuthRepositoryImpl(auth)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideRetrofitInstance(): WorkoutAPI{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WorkoutAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideExerciseRepository(api: WorkoutAPI): ExerciseRepository{
        return ExerciseRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): NutritionDatabase{
        return Room.databaseBuilder(
            context,
            NutritionDatabase::class.java,
            "nutrition_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    private const val RAPIDAPI_KEY = "a65d1be0e9mshf05431a65c41954p1a2950jsn2d27cf267aa8"
    private const val RAPIDAPI_HOST = "exercisedb.p.rapidapi.com"

    @Provides
    @Singleton
    @RapidApiClient
    fun provideRapidApiOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val originalRequest = chain.request()

                // Add RapidAPI headers
                val requestWithHeaders = originalRequest.newBuilder()
                    .addHeader("X-RapidAPI-Key", RAPIDAPI_KEY)
                    .addHeader("X-RapidAPI-Host", RAPIDAPI_HOST)
                    .addHeader("Content-Type", "application/json")
                    .build()

                chain.proceed(requestWithHeaders)
            }
            .build()
    }

    @Provides
    @Singleton
    @SpoonacularClient
    fun provideSpoonacularOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideSpoonacularApi(@SpoonacularClient spoonacularClient: OkHttpClient): SpoonacularAPI {
        return Retrofit.Builder()
            .baseUrl(SPOONACULAR_API)
            .client(spoonacularClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpoonacularAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideSpoonacularRepository(api: SpoonacularAPI, db: NutritionDatabase): NutritionFoodRepository{
        return NutritionFoodRepositoryImpl(api, db.nutritionDao())
    }
}