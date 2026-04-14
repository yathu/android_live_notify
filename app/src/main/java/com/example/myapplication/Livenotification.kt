// ... existing imports
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.myapplication.R

fun showLiveNotification(context: Context, title: String) {
    val channelId = "live_notifications"
    val notificationId = 1
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Changed to DEFAULT to ensure it's visible
        val channel =
            NotificationChannel(channelId, "Live Updates", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
    }

    val remoteViews = RemoteViews(context.packageName, R.layout.livenotification_view)
    remoteViews.setTextViewText(R.id.notification_title, title)

    // Chronometer Setup
    remoteViews.setChronometer(R.id.notification_timer, SystemClock.elapsedRealtime(), null, true)

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info) // Using system icon for testing
        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        .setCustomContentView(remoteViews)
        .setCustomBigContentView(remoteViews)
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    notificationManager.notify(notificationId, builder.build())
}