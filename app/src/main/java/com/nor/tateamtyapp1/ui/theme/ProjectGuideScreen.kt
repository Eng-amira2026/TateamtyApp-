package com.nor.tateamtyapp1.ui.theme

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
import com.nor.tateamtyapp1.R

private val guideBgColor = Color(0xFF005A9C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectGuideScreen(onBackPressed: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("دليل المنظومة الذكية", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(painterResource(id = R.drawable.ic_arrowback), "Back")
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
            Text("📖", fontSize = 60.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "منظومة تطعيماتي الذكية",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            GuideCard("بوابة الموظف الصحي", "تتيح تسجيل الأطفال الجدد، البحث في السجلات السحابية، وتوثيق إعطاء الجرعات الـ 8 الأساسية بدقة.")
            Spacer(modifier = Modifier.height(16.dp))
            GuideCard("نظام التنبيهات الذكي", "يقوم النظام آلياً بفحص المواعيد وإرسال إشعارات تذكيرية لهواتف أولياء الأمور قبل الموعد بـ 24 ساعة.")
            Spacer(modifier = Modifier.height(16.dp))
            GuideCard("الشهادة الرقمية (PDF)", "إمكانية توليد شهادة إتمام تحصين رسمية وموثقة من واقع البيانات السحابية.")
            Spacer(modifier = Modifier.height(16.dp))
            GuideCard("التقارير الإحصائية", "رسوم بيانية لحظية لمراقبة معدلات التطعيم الشهرية والسنوية لدعم اتخاذ القرار الصحي.")

            Spacer(modifier = Modifier.height(40.dp))
            Text(
                "إعداد المهندسة: أميرة إدريس\nمشروع تخرج - تقنية معلومات 2024",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun GuideCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, color = Color.Yellow, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(description, color = Color.White, fontSize = 15.sp, textAlign = TextAlign.Right, lineHeight = 22.sp)
        }
    }
}
