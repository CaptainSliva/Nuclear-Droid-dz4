package com.example.dz4

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object FunctionsApp {
    fun createNotify(context: Context, channelID: String, nextStart: LocalDateTime) {
        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = manager.getNotificationChannel(channelID) ?:
        NotificationChannel(
        channelID,
        "myChan",
        NotificationManager.IMPORTANCE_LOW
        )
        val builder = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Время следующего запуска")
            .setContentText(nextStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
        val notification = builder.build()


        manager.createNotificationChannel(channel)
        manager.notify(1, notification)
    }

}