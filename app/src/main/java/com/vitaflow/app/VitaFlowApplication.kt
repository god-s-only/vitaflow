package com.vitaflow.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import com.vitaflow.app.common.CHANNEL_ID
import com.vitaflow.app.data.worker.NotificationWorker
import com.vitaflow.app.data.worker.StepsSyncWorker
import com.vitaflow.app.domain.repository.getTodayDate
import com.vitaflow.app.domain.usecase.nutrition.GetCalorieTargetUseCase
import com.vitaflow.app.domain.usecase.nutrition.GetDailyNutritionUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class VitaFlowApplication : Application(),
    SingletonImageLoader.Factory,
    Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    @Inject
    private lateinit var getCalorieTargetUseCase: GetCalorieTargetUseCase
    @Inject
    private lateinit var getDailyNutritionUseCase: GetDailyNutritionUseCase

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return ImageLoader.Builder(context)
            .components {
                // Add network fetcher first
                add(OkHttpNetworkFetcherFactory(callFactory = { okHttpClient }))

                // Add GIF decoders based on Android version
                if (Build.VERSION.SDK_INT >= 28) {
                    add(AnimatedImageDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, 0.25)
                    .build()
            }
            .crossfade(true)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        StepsSyncWorker.schedulePeriodicSync(this)
        createNotificationChannel()
    }
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

    private fun createNotificationChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Target Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "This channel is for reminding the user about his/her target calories set"
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleNotification() {
        val workManager = WorkManager.getInstance(this)

        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            val targetCalories = getCalorieTargetUseCase.invoke()
            val todayNutrition = getDailyNutritionUseCase.invoke(getTodayDate()).firstOrNull()

            todayNutrition?.let { nutrition ->
                if (nutrition.calories >= targetCalories) {
                    val inputData = Data.Builder()
                        .putInt("TARGET_CALORIES", nutrition.calories.toInt())
                        .build()

                    val periodicRequest = PeriodicWorkRequestBuilder<NotificationWorker>(30, TimeUnit.MINUTES)
                        .setInputData(inputData)
                        .build()

                    workManager.enqueueUniquePeriodicWork(
                        "NutritionNotification",
                        ExistingPeriodicWorkPolicy.KEEP,
                        periodicRequest
                    )
                }
            }
        }
    }

}