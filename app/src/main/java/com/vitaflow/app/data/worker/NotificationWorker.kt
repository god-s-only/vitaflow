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
import com.vitaflow.app.domain.usecase.nutrition.CalculateAndSaveDailyNutritionUseCase
import com.vitaflow.app.domain.usecase.nutrition.GetCalorieTargetUseCase
import com.vitaflow.app.domain.usecase.nutrition.GetDailyNutritionSyncUseCase
import com.vitaflow.app.presentation.ui.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val getCalorieTargetUseCase: GetCalorieTargetUseCase,
    private val getDailyNutritionSyncUseCase: GetDailyNutritionSyncUseCase,
    private val calculateAndSaveDailyNutritionUseCase: CalculateAndSaveDailyNutritionUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val targetCalories = getCalorieTargetUseCase()
            if (targetCalories <= 0) {
                Log.d(TAG, "No calorie target set, skipping notification")
                return@withContext Result.success()
            }

            val currentDate = getCurrentDate()
            calculateAndSaveDailyNutritionUseCase()

            val todayNutrition = getDailyNutritionSyncUseCase(currentDate)
            val currentCalories = todayNutrition?.calories?.toInt() ?: 0

            Log.d(TAG, """
                Notification check:
                - Date: $currentDate
                - Target: $targetCalories cal
                - Current: $currentCalories cal
                - Remaining: ${targetCalories - currentCalories} cal
            """.trimIndent())

            when {
                currentCalories >= targetCalories -> {
                    Log.d(TAG, "Target met or exceeded, no notification needed")
                }
                currentCalories == 0 -> {
                    Log.d(TAG, "No calories logged yet today")
                    showNoCaloriesLoggedNotification(targetCalories)
                }
                else -> {
                    val remaining = targetCalories - currentCalories
                    showCalorieReminderNotification(remaining, targetCalories, currentCalories)
                    Log.d(TAG, "Reminder notification sent")
                }
            }

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error in NotificationWorker", e)
            handleFailure()
        }
    }

    private fun showCalorieReminderNotification(
        remaining: Int,
        target: Int,
        current: Int
    ) {
        if (!canShowNotification()) return

        val pendingIntent = createPendingIntent()
        val progressPercentage = (current.toFloat() / target * 100).toInt()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle(getNotificationTitle(remaining, progressPercentage))
            .setContentText(getNotificationText(remaining, target, current))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(getExpandedNotificationText(remaining, target, current, progressPercentage)))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setProgress(target, current, false)
            .build()

        getNotificationManager()?.notify(NOTIFICATION_ID, notification)
    }

    private fun showNoCaloriesLoggedNotification(target: Int) {
        if (!canShowNotification()) return

        val pendingIntent = createPendingIntent()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentTitle("Ready to start tracking?")
            .setContentText("You haven't logged any meals yet. Tap to track your first meal!")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Your daily goal is $target calories. Start logging your meals to stay on track!"))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        getNotificationManager()?.notify(NOTIFICATION_ID, notification)
    }

    private fun canShowNotification(): Boolean {
        val notificationManager = getNotificationManager() ?: return false

        if (!notificationManager.areNotificationsEnabled()) {
            Log.d(TAG, "Notifications are disabled by user")
            return false
        }

        return true
    }

    private fun getNotificationManager(): NotificationManager? {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        return PendingIntent.getActivity(
            context,
            NOTIFICATION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getNotificationTitle(remaining: Int, progress: Int): String {
        return when {
            progress >= 80 -> "Almost there! 🎯"
            progress >= 50 -> "Keep it up! 💪"
            progress >= 25 -> "You're on track! 📈"
            else -> "Track your meals 🍽️"
        }
    }

    private fun getNotificationText(remaining: Int, target: Int, current: Int): String {
        return "$remaining calories left to reach your goal"
    }

    private fun getExpandedNotificationText(
        remaining: Int,
        target: Int,
        current: Int,
        progress: Int
    ): String {
        return buildString {
            append("Current: $current / $target calories ($progress%)")
            append("\n")
            append("Remaining: $remaining calories")
            append("\n\n")
            append("Keep logging your meals to stay on track!")
        }
    }

    private fun handleFailure(): Result {
        return if (runAttemptCount < MAX_RETRY_ATTEMPTS) {
            Log.w(TAG, "Retrying... Attempt ${runAttemptCount + 1} of $MAX_RETRY_ATTEMPTS")
            Result.retry()
        } else {
            Log.e(TAG, "Max retry attempts reached, marking as failure")
            Result.failure()
        }
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
    }

    companion object {
        private const val TAG = "NotificationWorker"
        private const val NOTIFICATION_ID = 1001
        private const val NOTIFICATION_REQUEST_CODE = 1002
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val DATE_FORMAT = "yyyy-MM-dd"
    }
}