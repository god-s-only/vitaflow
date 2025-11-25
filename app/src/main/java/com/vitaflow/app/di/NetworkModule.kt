package com.vitaflow.app.di

import com.vitaflow.app.common.BASE_URL
import com.vitaflow.app.common.SPOONACULAR_API
import com.vitaflow.app.data.remote.SpoonacularAPI
import com.vitaflow.app.data.remote.WorkoutAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton



@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class RapidApiClient

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class SpoonacularClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

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
}