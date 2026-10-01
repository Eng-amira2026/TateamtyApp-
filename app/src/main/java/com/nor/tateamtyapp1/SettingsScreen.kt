@file:OptIn(ExperimentalMaterial3Api::class)

package com.nor.tateamtyapp1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Html
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private val settingsBgColor = Color(0xFF1E6FAA)
private val cardBgColor = Color.White
private val accentBlue = Color(0xFF0D47A1)
private val testGreen = Color(0xFF4CAF50)

@Composable
fun SettingsScreen(onBackPressed: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إعدادات المنظومة", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(painterResource(id = R.drawable.ic_arrowback), "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = settingsBgColor)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(settingsBgColor)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Spacer(modifier = Modifier.height(24.dp))

            // 1. Main Credits Card (Wrapped in RTL)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBgColor)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.Start // Start from right in RTL
                    ) {
                        Text(
                            "مشروع التخرج",
                            color = accentBlue,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "تطعيماتي: المنظومة الذكية لإدارة التحصينات الرقمية",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Start,
                            color = Color.Black,
                            lineHeight = 32.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(modifier = Modifier.width(150.dp), color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "إعداد / الطالبة:", 
                            color = Color.Gray, 
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        Text(
                            text = "أميرة إدريس",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Black,
                            color = accentBlue,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Test Notification Button (Green)
            Button(
                onClick = { triggerDynamicTestNotification(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = testGreen)
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("(Test) اختبار نظام التنبيهات", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. About the App Card (Already in RTL)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
                ) {
                    Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                        Text(
                            "عن التطبيق", 
                            fontWeight = FontWeight.Black, 
                            fontSize = 20.sp,
                            color = accentBlue,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "يهدف هذا النظام إلى أتمتة سجلات التطعيمات الوطنية، وضمان وصول التنبيهات لأولياء الأمور في المواعيد المحددة، مع توفير وسيلة تقنية لتوثيق اكتمال الجرعات عبر الشهادات الرقمية.",
                            fontSize = 20.sp,
                            color = Color.Black,
                            lineHeight = 28.sp,
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // 4. Footer Info
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Version 1.0.0 (Stable)", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Built with Kotlin & Jetpack Compose", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Text("Firebase Cloud Backend", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun triggerDynamicTestNotification(context: Context) {
    val db = Firebase.firestore
    db.collection("children")
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .limit(1)
        .get()
        .addOnSuccessListener { snapshot ->
            val childName = if (!snapshot.isEmpty) snapshot.documents[0].getString("name") ?: "ريان" else "ريان"
            val vaccineName = if (!snapshot.isEmpty) snapshot.documents[0].getString("nextDoseName") ?: "Polio" else "Polio"
            showPersonalizedTestNotification(context, childName, vaccineName)
        }
        .addOnFailureListener { showPersonalizedTestNotification(context, "ريان", "Polio") }
}

private fun showPersonalizedTestNotification(context: Context, childName: String, vaccineName: String) {
    val channelId = "personalized_test_channel"
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, "اختبارات النظام", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
    }

    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

    val styledText = "عزيزي ولي أمر الطفل/ة <b>$childName</b>، غداً موعد جرعة <b>$vaccineName</b>. حضوركم يحمي طفلكم ويقوي مناعته. الرجاء التوجه إلى أقرب مركز صحي."
    
    val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle("💉 تنبيه هام من تطعيمات")
        .setContentText(Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY)) 
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setStyle(NotificationCompat.BigTextStyle().bigText(Html.fromHtml(styledText, Html.FROM_HTML_MODE_LEGACY))) 
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(303, notification)
}
