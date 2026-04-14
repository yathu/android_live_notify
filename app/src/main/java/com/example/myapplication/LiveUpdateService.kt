package com.example.myapplication // Make sure this matches your actual package name

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

class LiveUpdateService : Service() {

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "live_update_channel"
        const val EXTRA_TITLE = "EXTRA_TITLE"
        const val EXTRA_MINUTES = "EXTRA_MINUTES"

        // Helper to start this service from anywhere
        fun startService(context: Context, title: String, minutes: Int) {
            val intent = Intent(context, LiveUpdateService::class.java).apply {
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_MINUTES, minutes)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val title = intent?.getStringExtra(EXTRA_TITLE) ?: "Task in Progress"
        val minutes = intent?.getIntExtra(EXTRA_MINUTES, 10) ?: 10

        // Create the notification and start foreground mode
        val notification = createNotification(title, minutes)
        startForeground(NOTIFICATION_ID, notification)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(title: String, minutes: Int): Notification {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 1. Create Channel (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Live Task Updates",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }

        // 2. Setup Button Intents
        val doneIntent = Intent(this, NotificationReceiver::class.java).apply { action = "ACTION_DONE" }
        val pDone = PendingIntent.getBroadcast(this, 0, doneIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val addIntent = Intent(this, NotificationReceiver::class.java).apply { action = "ACTION_ADD_TIME" }
        val pAdd = PendingIntent.getBroadcast(this, 1, addIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        // 3. Setup Custom Layout (RemoteViews)
        val remoteViews = RemoteViews(packageName, R.layout.livenotification_view)
        remoteViews.setTextViewText(R.id.notification_title, title)

        // Set the countdown timer
        val targetTime = SystemClock.elapsedRealtime() + (minutes * 60 * 1000)
        remoteViews.setChronometerCountDown(R.id.notification_timer, true)
        remoteViews.setChronometer(R.id.notification_timer, targetTime, null, true)

        // Link buttons in XML to the Intents
        remoteViews.setOnClickPendingIntent(R.id.btn_done, pDone)
        remoteViews.setOnClickPendingIntent(R.id.btn_add_time, pAdd)

        // 4. Build the Notification
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app icon
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setOngoing(true) // Makes it a Live Update (unswippable)
            .setOnlyAlertOnce(true) // Stops it from vibrating every update
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }
}