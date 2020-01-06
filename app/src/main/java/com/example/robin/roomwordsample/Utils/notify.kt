package com.example.robin.roomwordsample.Utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.robin.roomwordsample.Activity.MainActivity
import com.example.robin.roomwordsample.R
import com.example.robin.roomwordsample.localdb.LocalDB

class notify(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val b = "420"
    var task = " "

    override fun doWork(): Result {
        Looper.prepare()
        val appSharedPrefs = LocalDB(this.applicationContext)
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(b, "Default Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        task = appSharedPrefs.getTask()

        val intent = Intent(applicationContext, MainActivity::class.java)
        val pi = PendingIntent.getActivity(
            applicationContext,
            333,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(applicationContext, b)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(Color.rgb(30, 136, 229))
            .setContentTitle("Reminder")
            .setContentText(task)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()
        notificationManager.notify(1112, notification)
        Looper.loop()
        return Result.success()

    }

}