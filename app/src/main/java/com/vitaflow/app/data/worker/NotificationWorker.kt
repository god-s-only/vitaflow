package com.vitaflow.app.data.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vitaflow.app.R
import com.vitaflow.app.common.CHANNEL_ID
import com.vitaflow.app.presentation.ui.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters
): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO){
            val targetCalories = inputData.getInt("TARGET_CALORIES", 0)
            showTargetUpdateNotification(targetCalories)
            Result.success()
        }
    }

    private fun showTargetUpdateNotification(targetCalories: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            Intent(
                context,
                MainActivity::class.java
            ),
            PendingIntent.FLAG_IMMUTABLE
        )
        val targetUpdateNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle("Target Update")
            .setContentText("Remember your target is $targetCalories calories, log your meals!")
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(1, targetUpdateNotification)
    }
}