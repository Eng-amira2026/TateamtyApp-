package com.nor.tateamtyapp1

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

private val dashboardBgColor = Color(0xFF1E6FAA) // Reverted to Original Dark Blue
private val cardBgColor = Color(0x33000000) // Reverted to Semi-transparent

data class DashboardItem(val emoji: String, val text: String, val hint: String, val onClick: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    onRegisterChildClicked: () -> Unit,
    onVaccinationManagementClicked: () -> Unit,
    onIssueCertificateClicked: () -> Unit,
    onNavigateToReports: () -> Unit,
    onSearchChildClicked: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToUserManual: () -> Unit,
    onChildDueTodayClicked: (String) -> Unit,
    dashboardViewModel: DashboardViewModel = viewModel()
) {
    val stats by dashboardViewModel.stats.collectAsState()
    var showDueTodayDialog by remember { mutableStateOf(false) }

    val dashboardItems = listOf(
        DashboardItem("👶", "Register New Child", "تسجيل مولود جديد", onRegisterChildClicked),
        DashboardItem("🔍", "Search Registered Child", "البحث عن السجلات", onSearchChildClicked),
        DashboardItem("📜", "Issue Certificate", "إصدار الشهادات", onIssueCertificateClicked),
        DashboardItem("💉", "Vaccinations Management", "إدارة التطعيمات", onVaccinationManagementClicked),
        DashboardItem("📊", "Reports & Statistics", "التقارير والإحصائيات", onNavigateToReports),
        DashboardItem("⚙️", "Setting", "الإعدادات ", onNavigateToSettings),
        DashboardItem("📖", "User Manual", "دليل تشغيل المنظومة", onNavigateToUserManual)
    )

    Box(modifier = Modifier.fillMaxSize().background(dashboardBgColor)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Health Staff Dashboard", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatSummaryCard(label = "Registered Children", value = stats.totalChildren.toString())
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StatSummaryCard(
                            label = "Due Today", 
                            value = stats.dueTodayCount.toString(), 
                            highlight = stats.dueTodayCount > 0,
                            onClick = { if (stats.dueTodayCount > 0) showDueTodayDialog = true }
                        )
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(horizontal = 16.dp).weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                content = {
                    itemsIndexed(dashboardItems) { index, item ->
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            delay(index * 100L)
                            visible = true
                        }
                        androidx.compose.animation.AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(animationSpec = tween(600)) + scaleIn(initialScale = 0.8f)
                        ) {
                            DashboardCardEnhanced(item = item)
                        }
                    }
                    item {
                        LogoutCard(onClick = onLogout)
                    }
                }
            )
        }

        if (showDueTodayDialog) {
            AlertDialog(
                onDismissRequest = { showDueTodayDialog = false },
                title = { 
                    Text(
                        " الاطفال لديهم جرعات مستحقة اليوم او متاخرة",
                        fontWeight = FontWeight.Black, 
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Black,
                        fontSize = 24.sp
                    ) 
                },
                text = {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(stats.childrenDueToday) { child ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { 
                                    showDueTodayDialog = false
                                    onChildDueTodayClicked(child.id) 
                                },
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp), 
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Column(horizontalAlignment = Alignment.End, modifier = Modifier.weight(1f)) {
                                        Text(child.name, fontWeight = FontWeight.Black, fontSize = 20.sp)
                                        Text("اللقاح: ${child.vaccineName}", fontSize = 20.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("💉", fontSize = 24.sp)
                                }
                            }
                        }
                    }
                },
                confirmButton = { 
                    TextButton(onClick = { showDueTodayDialog = false }) { 
                        Text("إغلاق", fontWeight = FontWeight.Black, fontSize = 18.sp) 
                    } 
                }
            )
        }
    }
}

@Composable
fun DashboardCardEnhanced(item: DashboardItem) {
    Card(
        modifier = Modifier
            .height(130.dp) // صغرنا الحجم من 150 إلى 130
            .clickable { item.onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = item.emoji, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.text, 
                color = Color.White, 
                fontWeight = FontWeight.Black, 
                fontSize = 18.sp // حجم واضح
            )
            Text(
                text = item.hint, 
                color = Color.White, 
                fontSize = 16.sp, 
                textAlign = TextAlign.Center, 
                lineHeight = 20.sp, // Increased
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun LogoutCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .height(130.dp) // صغرنا الحجم ليتناسق مع البقية
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.6f)) // تم تغيير اللون للأخضر الاحترافي
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "🚪", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "Log Out", color = Color.White, fontWeight = FontWeight.Black, fontSize = 17.sp)
            Text(
                text = "تسجيل الخروج", 
                color = Color.White, 
                fontSize = 14.sp, 
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatSummaryCard(label: String, value: String, highlight: Boolean = false, onClick: (() -> Unit)? = null) {
    Card(
        modifier = Modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = Color.White, fontSize = 12.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            Text(value, color = if (highlight) Color.Yellow else Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)
        }
    }
}
