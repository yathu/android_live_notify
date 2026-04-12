package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

fun showLiveNotification(context: Context, title: String) {

    val channelId = "live_notifications"
    val notificationId = 1

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel =
            NotificationChannel(channelId, "Live Updates", NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

    val packageName = context.packageName
    val remoteViews = RemoteViews(packageName, R.layout.livenotification_view)

    remoteViews.setTextViewText(R.id.notification_title, title)

    // --- CHRONOMETER LOGIC START ---
    // SystemClock.elapsedRealtime() is the current time.
    // To start from 00:00, we set the base to "now".
    remoteViews.setChronometer(
        R.id.notification_timer,
        SystemClock.elapsedRealtime(),
        null,
        true // starts the timer
    )

    // Optional: If you want it to COUNT DOWN from 10 minutes (API 24+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val tenMinutesInMs = 10 * 60 * 1000
        remoteViews.setChronometerCountDown(R.id.notification_timer, true)
        remoteViews.setChronometer(
            R.id.notification_timer,
            SystemClock.elapsedRealtime() + tenMinutesInMs,
            null,
            true
        )
    }
    // --- CHRONOMETER LOGIC END ---

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        .setCustomContentView(remoteViews)
        .setCustomBigContentView(remoteViews)
        .setOngoing(true)
        .setOnlyAlertOnce(true)

    notificationManager.notify(notificationId, builder.build())
}