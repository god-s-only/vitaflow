package com.vitaflow.app.data.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vitaflow.app.R
import com.vitaflow.app.common.CHANNEL_ID
import com.vitaflow.app.domain.usecase.nutrition.GetCalorieTargetUseCase
import com.vitaflow.app.domain.usecase.nutrition.GetDailyNutritionSyncUseCase
import com.vitaflow.app.domain.usecase.nutrition.GetDailyNutritionUseCase
import com.vitaflow.app.presentation.ui.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val getCalorieTargetUseCase: GetCalorieTargetUseCase,
    private val getDailyNutritionSyncUseCase: GetDailyNutritionSyncUseCase
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val targetCalories = getCalorieTargetUseCase.invoke()
                if (targetCalories <= 0) {
                    return@withContext Result.success()
                }

                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date())
                val todayNutrition = getDailyNutritionSyncUseCase.invoke(currentDate)

                Log.d("NotificationWorker", "Date: $currentDate")
                Log.d("NotificationWorker", "Calories: ${todayNutrition?.calories}")

                val currentCalories = todayNutrition?.calories?.toInt() ?: 0

                if (currentCalories < targetCalories) {
                    showTargetUpdateNotification(targetCalories, currentCalories)
                }

                Result.success()
            } catch (e: Exception) {
                Log.e("NotificationWorker", "Error", e)
                if (runAttemptCount < 3) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        }
    }

    private fun showTargetUpdateNotification(targetCalories: Int, currentCalories: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (!notificationManager.areNotificationsEnabled()) {
            return
        }
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val remaining = targetCalories - currentCalories
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle("Target Calories")
            .setContentText("You have $remaining calories left. Keep logging your meals!")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Date())
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val NOTIFICATION_REQUEST_CODE = 1002
    }
}