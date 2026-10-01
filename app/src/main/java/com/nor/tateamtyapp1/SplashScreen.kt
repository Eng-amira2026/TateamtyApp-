package com.nor.tateamtyapp1

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    val scale = remember { Animatable(0f) }
    var textVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        // حركة تكبير الدائرة
        scale.animateTo(
            targetValue = 1f,
            animationSpec = sprungAnimation()
        )
        // إظهار النص بعد تكبير الدائرة
        textVisible = true
        delay(2500) // تقليل زمن الانتظار لزيادة السرعة
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E6FAA)), // الخلفية الزرقاء الملكية
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. الشعار داخل دائرة مع إطار
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(scale.value)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(6.dp, Color.White.copy(alpha = 0.3f), CircleShape) // خط خارجي شفاف
                    .border(2.dp, Color.White, CircleShape), // خط داخلي ناصع
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.welcome_image),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. نص "تطعيماتي" بحركة ظهور احترافية
            AnimatedVisibility(
                visible = textVisible,
                enter = fadeIn(animationSpec = tween(1000)) + expandVertically()
            ) {
                Text(
                    text = "تطعيماتي",
                    color = Color.White,
                    fontSize = 45.sp, // خط ضخم واحترافي
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
            }
            
            // 3. نص فرعي صغير لإتمام الشكل
            if (textVisible) {
                Text(
                    text = "TATEAMTY APP",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

// دالة لحركة الارتداد المرنة (Spring) لجعلها أكثر حيوية
fun sprungAnimation() = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)
