@file:OptIn(ExperimentalMaterial3Api::class)

package com.nor.tateamtyapp1

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

private val reportBgColor = Color(0xFF1E6FAA)
private val successGreen = Color(0xFF4CAF50)
private val errorRed = Color(0xFFD32F2F)

@Composable
fun CompletionReportScreen(onBackPressed: () -> Unit) {
    var totalChildren by remember { mutableIntStateOf(0) }
    var fullyVaccinatedCount by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val db = Firebase.firestore
            val snapshot = db.collection("children").get().await()
            totalChildren = snapshot.size()
            fullyVaccinatedCount = snapshot.documents.count { it.getString("nextDoseName") == null }
        } catch (_: Exception) {
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Completion Statistics", fontWeight = FontWeight.Black, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(painterResource(id = R.drawable.ic_arrowback), "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = reportBgColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(reportBgColor)
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
            } else {
                val completionRate = if (totalChildren > 0) (fullyVaccinatedCount.toFloat() / totalChildren.toFloat()) else 0f
                val incompleteCount = totalChildren - fullyVaccinatedCount

                val statsList = listOf(
                    "Total Children Registered" to totalChildren.toString() to Color.Black,
                    "Fully Vaccinated" to fullyVaccinatedCount.toString() to successGreen,
                    "Incomplete Vaccinations" to incompleteCount.toString() to errorRed,
                    "Completion Rate" to "${(completionRate * 100).toInt()}%" to reportBgColor
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Arabic Guidance Card
                    item {
                        ReportGuidanceCard()
                    }

                    // 2. Animated Circular Chart
                    item {
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) { visible = true }
                        androidx.compose.animation.AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(animationSpec = tween(1000)) + scaleIn(initialScale = 0.8f)
                        ) {
                            CompletionChart(rate = completionRate, total = totalChildren, completed = fullyVaccinatedCount)
                        }
                    }

                    // 3. Staggered Modern Stat Cards
                    itemsIndexed(statsList) { index, itemData ->
                        val (pair, color) = itemData
                        val (label, value) = pair
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            delay(150L * index)
                            visible = true
                        }
                        androidx.compose.animation.AnimatedVisibility(
                            visible = visible,
                            enter = slideInHorizontally(initialOffsetX = { -100 }) + fadeIn()
                        ) {
                            ModernStatCard(
                                label = label,
                                value = value,
                                valueColor = color
                            )
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
fun ModernStatCard(label: String, value: String, valueColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color.DarkGray,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                color = valueColor,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun ReportGuidanceCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("📊", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "استخدم هذه المؤشرات لمراقبة أداء المركز الصحي؛ تشير نسب الإكمال المرتفعة إلى نجاح خطة الوصول المجتمعي وضمان تحصين كافة الأطفال.",
                color = Color.White,
                fontSize = 20.sp,
                lineHeight = 28.sp,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CompletionChart(rate: Float, total: Int, completed: Int) {
    val animatedRate by animateFloatAsState(
        targetValue = rate * 360f, 
        animationSpec = tween(2000, easing = FastOutSlowInEasing)
    )
    
    // Animated counter for the percentage text
    val animatedPercent by animateIntAsState(
        targetValue = (rate * 100).toInt(),
        animationSpec = tween(2000, easing = FastOutSlowInEasing)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(140.dp)) {
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = successGreen,
                        startAngle = -90f,
                        sweepAngle = animatedRate,
                        useCenter = false,
                        style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$animatedPercent%", fontSize = 28.sp, fontWeight = FontWeight.Black, color = reportBgColor)
                    Text("Overall", fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "$completed out of $total children have completed the national schedule.",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
