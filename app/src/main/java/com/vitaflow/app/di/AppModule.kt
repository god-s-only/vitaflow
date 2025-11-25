package com.vitaflow.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.firebase.auth.FirebaseAuth
import com.vitaflow.app.common.BASE_URL
import com.vitaflow.app.common.SPOONACULAR_API
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



@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth{
        return FirebaseAuth.getInstance()
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
    fun provideNutritionPreferences(@ApplicationContext context: Context): NutritionPreferences {
        return NutritionPreferences(context)
    }
}