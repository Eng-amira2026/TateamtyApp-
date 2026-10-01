@file:OptIn(ExperimentalMaterial3Api::class)

package com.nor.tateamtyapp1

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.*

private val parentDbBgColor = Color(0xFF002D5A)

data class ParentChildSummary(
    val id: String,
    val name: String,
    val nextDoseName: String?,
    val nextDoseDate: Date?,
    val progress: Float = 0f
)

@Composable
fun ParentDashboardScreen(
    parentPhone: String,
    onLogout: () -> Unit,
    onViewChildRecords: (childId: String) -> Unit,
    onNavigateToVaccineInfo: () -> Unit = {}
) {
    var childrenList by remember { mutableStateOf<List<ParentChildSummary>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(parentPhone) {
        try {
            val snapshot = Firebase.firestore.collection("children")
                .whereEqualTo("guardianPhone", parentPhone)
                .get().await()
            
            val tempChildren = mutableListOf<ParentChildSummary>()
            
            for (doc in snapshot.documents) {
                val childId = doc.id
                // حساب التقدم لكل طفل
                val recordsSnapshot = Firebase.firestore.collection("vaccinationRecords")
                    .whereEqualTo("childId", childId)
                    .whereEqualTo("status", "GIVEN")
                    .get().await()
                
                val completedCount = recordsSnapshot.size()
                val totalVaccines = 8
                val progress = completedCount.toFloat() / totalVaccines.toFloat()

                tempChildren.add(
                    ParentChildSummary(
                        id = childId,
                        name = doc.getString("name") ?: "غير معروف",
                        nextDoseName = doc.getString("nextDoseName"),
                        nextDoseDate = (doc["nextDoseDate"] as? Timestamp)?.toDate(),
                        progress = progress
                    )
                )
            }
            childrenList = tempChildren
        } catch (_: Exception) {
        } finally {
            isLoading = false
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(parentDbBgColor)
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
            } else if (childrenList.isEmpty()) {
                NoChildrenView()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "أطفالي", 
                                color = Color.White, 
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            IconButton(onClick = onLogout) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_lock_power_off), 
                                    contentDescription = "خروج", 
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "مرحباً بك! إليك قائمة أطفالك المسجلين في النظام:",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp)
                        )
                    }

                    item {
                        VaccineAwarenessBannerCard(onClick = onNavigateToVaccineInfo)
                    }

                    items(childrenList) { child ->
                        ChildCardWithProgress(child = child, onClick = { onViewChildRecords(child.id) })
                    }

                    item {
                        SimpleSupportFooter()
                    }
                    
                    item { Spacer(modifier = Modifier.height(30.dp)) }
                }
            }
        }
    }
}

@Composable
fun SimpleSupportFooter() {
    val phoneNumber = "0123456789" // رقم المركز

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "للاستفسار يرجى التواصل مع رقم المركز: $phoneNumber",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Right
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text("🎧", fontSize = 26.sp)
    }
}

@Composable
fun ChildCardWithProgress(child: ParentChildSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Arrow
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_media_previous),
                    contentDescription = null,
                    tint = parentDbBgColor,
                    modifier = Modifier.size(20.dp)
                )

                // Name and Subtitle
                Column(
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = child.name, 
                        fontWeight = FontWeight.Black, 
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "اضغط لعرض حالة التطعيمات",
                        fontSize = 20.sp,
                        color = Color(0xFF005A9C),
                        fontWeight = FontWeight.Bold
                    )
                }

                // Baby Icon
                Box(
                    modifier = Modifier
                        .size(65.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE8EAF6)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👶", fontSize = 36.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Progress Section
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(), 
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${(child.progress * 100).toInt()}%", 
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black, 
                        color = parentDbBgColor
                    )
                    Text(
                        text = "نسبة إكمال التحصينات", 
                        fontSize = 22.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { child.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = if (child.progress >= 1f) Color(0xFF4CAF50) else parentDbBgColor,
                    trackColor = Color(0xFFEEEEEE)
                )
            }
        }
    }
}

@Composable
fun NoChildrenView() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp), 
        verticalArrangement = Arrangement.Center, 
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("👋", fontSize = 60.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("لا يوجد أطفال مسجلون بهذا الرقم", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun VaccineAwarenessBannerCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF004080)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = Color(0xFFFFD700),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "عرض ➔",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF002D5A),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "دليل التطعيمات والتوعية الصحية 🛡️",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    textAlign = TextAlign.Right
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "تعرف على أهمية اللقاحات والأمراض التي تحمي منها طفلك",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Right
                )
            }
        }
    }
}

