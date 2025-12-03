package com.vitaflow.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import com.vitaflow.app.common.CHANNEL_ID
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

private const val TAG = "STEP_COUNT_LISTENER"
@HiltAndroidApp
class VitaFlowApplication : Application(), SingletonImageLoader.Factory {
    private val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

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
        createNotificationChannel()
    }
    private fun createNotificationChannel(){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Target Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "This channel is for reminding the user about his/her target calories set"
            notificationManager.createNotificationChannel(channel)
        }

    }
    suspend fun steps() = suspendCancellableCoroutine { continuation ->
        Log.d(TAG, "Registering sensor listener... ")
        val listener: SensorEventListener by lazy {
            object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if(event == null) return
                    val stepsSinceLastReboot = event.values[0].toLong()
                    Log.d(TAG, "Steps since last reboot: $stepsSinceLastReboot")
                    if(continuation.isActive){
                        continuation.resume(stepsSinceLastReboot)
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    Log.d(TAG, "Accuracy changed: $accuracy")
                }
            }
        }
        val supportedAndEnabled = sensorManager.registerListener(listener,
            sensor, SensorManager.SENSOR_DELAY_UI)
        Log.d(TAG, "Sensor listener registered: $supportedAndEnabled")
    }
}