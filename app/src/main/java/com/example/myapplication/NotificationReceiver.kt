package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action

        when (action) {
            "ACTION_DONE" -> {
                val serviceIntent = Intent(context, LiveUpdateService::class.java)
                context.stopService(serviceIntent)
            }
            "ACTION_ADD_TIME" -> {
                // Explicitly call the static helper function
                LiveUpdateService.startService(context, "Extended Task", 5)
            }
        }
    }
}