package com.nor.tateamtyapp1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM", "Message received from: ${remoteMessage.from}")

        // 1. التعامل مع الإشعار القادم من وحدة تحكم Firebase (Notification Payload)
        remoteMessage.notification?.let {
            Log.d("FCM", "Notification Body: ${it.body}")
            sendNotification(it.title ?: "Tateamty", it.body ?: "New Notification")
        }

        // 2. التعامل مع البيانات (Data Payload) - احتياطي
        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"] ?: "Tateamty"
            val body = remoteMessage.data["body"] ?: "New Data Notification"
            sendNotification(title, body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token: $token")
        // هنا يمكنك إرسال الرمز الجديد إلى الخادم
    }

    private fun sendNotification(title: String, messageBody: String) {
        val channelId = "vaccine_channel_id"

        // النقر على الإشعار يفتح التطبيق
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // أيقونة افتراضية مضمونة
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // أولوية عالية للظهور

        // *** التعديل هنا: تمت إزالة Context. لأنها زائدة ***
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // إنشاء قناة الإشعارات (ضروري لأندرويد 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Vaccine Notifications",
                NotificationManager.IMPORTANCE_HIGH // أهمية عالية
            ).apply {
                description = "Channel for vaccine reminders"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = Random.nextInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}