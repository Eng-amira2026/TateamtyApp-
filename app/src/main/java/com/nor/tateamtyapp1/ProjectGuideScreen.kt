@file:OptIn(ExperimentalMaterial3Api::class)

package com.nor.tateamtyapp1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val guideBgColor = Color(0xFF1E6FAA)

@Composable
fun ProjectGuideScreen(onBackPressed: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("دليل المنظومة التقني", fontWeight = FontWeight.Black, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(painterResource(id = R.drawable.ic_arrowback), "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = guideBgColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(guideBgColor)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📖", fontSize = 64.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "دليل منصة تطعيماتي",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Text(
                "مشروع البنية التحتية للصحة الرقمية v1.0",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Guide Sections in Arabic
            GuideFeatureCard(
                title = "إدارة البيانات السحابية",
                description = "تسجيل المواليد الجدد وحفظ تاريخ التطعيمات مدى الحياة بمزامنة كاملة مع قاعدة بيانات سحابية مركزية.",
                emoji = "☁️"
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            GuideFeatureCard(
                title = "الجدولة الذكية للجرعات",
                description = "خوارزمية برمجية لحساب مواعيد 8 جرعات أساسية حسب البروتوكول الوطني بدقة تمنع الأخطاء البشرية.",
                emoji = "⚙️"
            )

            Spacer(modifier = Modifier.height(16.dp))

            GuideFeatureCard(
                title = "نظام التنبيهات الاستباقي",
                description = "نظام إشعارات يرسل تذكيرات تلقائية لولي الأمر قبل 24 ساعة من الموعد، مخصصة باسم الطفل ونوع اللقاح.",
                emoji = "🔔"
            )

            Spacer(modifier = Modifier.height(16.dp))

            GuideFeatureCard(
                title = "التوثيق والشهادات الرقمية",
                description = "توليد شهادات إتمام التطعيم الرسمية بصيغة PDF مع فحص آلي لكافة الجرعات المطلوبة قبل الإصدار.",
                emoji = "📜"
            )

            Spacer(modifier = Modifier.height(40.dp))
            
            // Author Credits
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "تصميم وتطوير المهندسة",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "أميرة إدريس",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "تقنية المعلومات - مشروع التخرج 2024",
                        color = Color.Yellow.copy(alpha = 0.8f),
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun GuideFeatureCard(title: String, description: String, emoji: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(20.dp), 
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(title, color = Color(0xFF0D47A1), fontWeight = FontWeight.Black, fontSize = 20.sp, textAlign = TextAlign.Right)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = Color.Black,
                    fontSize = 20.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Right,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(Color(0xFF1E6FAA).copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 24.sp)
            }
        }
    }
}
