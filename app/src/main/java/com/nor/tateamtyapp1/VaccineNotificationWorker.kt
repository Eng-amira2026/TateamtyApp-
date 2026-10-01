package com.nor.tateamtyapp1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlinx.coroutines.tasks.await

class VaccineNotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val db = Firebase.firestore
        
        // حساب نطاق "يوم الغد"
        val startOfTomorrow = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.time

        val endOfTomorrow = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.time

        try {
            val snapshot = db.collection("children")
                .whereGreaterThanOrEqualTo("nextDoseDate", Timestamp(startOfTomorrow))
                .whereLessThanOrEqualTo("nextDoseDate", Timestamp(endOfTomorrow))
                .get().await()

            for (doc in snapshot.documents) {
                val lastNotified = doc.getTimestamp("lastNotificationDate")?.toDate()
                
                if ((lastNotified == null) || (!isSameDay(lastNotified, Date()))) {
                    val name = doc.getString("name") ?: "طفلك"
                    val vaccine = doc.getString("nextDoseName") ?: "الجرعة المقررة"
                    val dob = doc.getTimestamp("dateOfBirth")?.toDate()
                    
                    // حساب العمر بالأشهر ليوم غد
                    val ageInMonths = dob?.let { calculateAgeInMonths(it, startOfTomorrow) } ?: 0

                    sendPersonalizedNotification(name, ageInMonths, vaccine)

                    db.collection("children").document(doc.id)
                        .update("lastNotificationDate", Timestamp(Date()))
                }
            }
            return Result.success()
        } catch (_: Exception) {
            return Result.retry()
        }
    }

    private fun calculateAgeInMonths(dob: Date, targetDate: Date): Int {
        val birth = Calendar.getInstance().apply { time = dob }
        val target = Calendar.getInstance().apply { time = targetDate }
        val years = target[Calendar.YEAR] - birth[Calendar.YEAR]
        val months = target[Calendar.MONTH] - birth[Calendar.MONTH]
        return (years * 12) + months
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return (cal1[Calendar.YEAR] == cal2[Calendar.YEAR]) &&
               (cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR])
    }

    private fun sendPersonalizedNotification(childName: String, age: Int, vaccineName: String) {
        val channelId = "personalized_vaccine_channel"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Personalized Notifications", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // الصيغة الشخصية والمؤثرة المحدثة
        val ageText = if (age == 0) "أيام" else "$age شهر"
        val styledText = "عزيزي ولي أمر الطفل/ة <b>$childName</b>، غداً يكمل طفلك <b>$ageText</b> من العمر. نذكركم بضرورة التوجه للمركز الصحي لأخذ جرعة: <b>$vaccineName</b>. حضوركم يحمي طفلكم ويقوي مناعته. الرجاء التوجه إلى أقرب مركز صحي."

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("💉 تنبيه هام من تطعيمات")
            .setContentText(Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setStyle(NotificationCompat.BigTextStyle().bigText(Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY)))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(childName.hashCode(), notification)
    }
}
