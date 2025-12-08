package com.vitaflow.app.di

import android.content.Context
import com.vitaflow.app.data.remote.HealthConnectService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HealthConnectModule {

    @Provides
    @Singleton
    fun provideHealthConnectService(
        @ApplicationContext context: Context
    ): HealthConnectService {
        return HealthConnectService(context)
    }
}