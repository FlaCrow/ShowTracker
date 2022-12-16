package com.flacrow.showtracker.updatefeature

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.flacrow.core.utils.ConstantValues.SERIES_ID_EXTRA

class CheckUpdateNotificationService(private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val childNotificationsIntent =
        Intent(
            context,
            Class.forName("com.flacrow.showtracker.presentation.MainActivity")
        ).setAction(Intent.ACTION_VIEW)

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        val channel = NotificationChannel(
            UPDATE_NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = context.getString(R.string.notification_channel_desc)
        notificationManager.createNotificationChannel(channel)
    }

    fun showSummary() {
        val activityIntent =
            Intent(context, Class.forName("com.flacrow.showtracker.presentation.MainActivity"))

        val notification = NotificationCompat.Builder(context, UPDATE_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(com.flacrow.core.R.drawable.ic_baseline_watching_24)
            .setContentTitle(context.getString(R.string.notification_summary_title))
            .setContentText(context.getString(R.string.notification_summary_text)).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) it.setGroup(GROUP_KEY)
                    .setGroupSummary(true)
            }.setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentIntent(activityIntent.toPendingIntent(context, 0)).build()
        notificationManager.notify(SUMMARY_ID, notification)
    }

    fun showNotification(showIdAsNotificationId: Int, showName: String) {


        childNotificationsIntent.putExtra(SERIES_ID_EXTRA, showIdAsNotificationId)

        val notification = NotificationCompat.Builder(context, UPDATE_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(com.flacrow.core.R.drawable.ic_baseline_watching_24)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_text, showName))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setContentIntent(
                childNotificationsIntent.toPendingIntent(
                    context,
                    showIdAsNotificationId
                )
            )
            .also { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) it.setGroup(GROUP_KEY) }
            .build()
        notificationManager.notify(showIdAsNotificationId, notification)
    }


    companion object {
        const val UPDATE_NOTIFICATION_CHANNEL_ID = "update_channel"
        const val GROUP_KEY = "updates_group_key"
        const val SUMMARY_ID = 0
    }
}

private fun Intent.toPendingIntent(context: Context, id: Int): PendingIntent {
    return PendingIntent.getActivity(
        context,
        id,
        this,
        PendingIntent.FLAG_IMMUTABLE
    )
}