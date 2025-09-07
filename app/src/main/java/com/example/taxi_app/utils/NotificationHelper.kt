package com.example.taxi_app.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.taxi_app.MainActivity
import com.example.taxi_app.R

class NotificationHelper(private val context: Context) {
    
    companion object {
        private const val CHANNEL_ID = "taxi_requests_channel"
        private const val CHANNEL_NAME = "Taxi Request Updates"
        private const val CHANNEL_DESCRIPTION = "Notifications for taxi request status updates"
        private const val NOTIFICATION_ID_ACCEPTED = 1001
        private const val NOTIFICATION_ID_REJECTED = 1002
    }
    
    init {
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 250, 250)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun showRequestAcceptedNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("open_requests", true)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // You can create a better icon
            .setContentTitle("Հայտը ընդունվել է!")
            .setContentText("Ձեր տաքսի հայտը ընդունվել է վարորդի կողմից 🎉")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_ACCEPTED, notification)
            android.util.Log.d("TaxiApp", "Accepted notification sent successfully")
        } catch (e: SecurityException) {
            android.util.Log.e("TaxiApp", "Failed to send notification: ${e.message}")
        }
    }
    
    fun showRequestRejectedNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("open_requests", true)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Հայտը մերժվել է")
            .setContentText("Ձեր տաքսի հայտը մերժվել է վարորդի կողմից 😔")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500))
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .build()
        
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_REJECTED, notification)
            android.util.Log.d("TaxiApp", "Rejected notification sent successfully")
        } catch (e: SecurityException) {
            android.util.Log.e("TaxiApp", "Failed to send notification: ${e.message}")
        }
    }
    
    fun areNotificationsEnabled(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}
