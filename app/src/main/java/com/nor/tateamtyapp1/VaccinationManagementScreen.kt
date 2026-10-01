@file:OptIn(ExperimentalMaterial3Api::class)

package com.nor.tateamtyapp1

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

private val managementBgColor = Color(0xFF1E6FAA)

@Composable
fun VaccinationManagementScreen(
    onBackPressed: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToRecords: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Operations Management", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(painterResource(id = R.drawable.ic_arrowback), "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = managementBgColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(managementBgColor)
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 💡 اللمسة الإرشادية بالعربي
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("⚙️", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "إدارة بيانات النظام الأساسية؛ يمكنك هنا الاطلاع على بروتوكول التطعيم الوطني المعتمد أو الوصول إلى السجل المركزي لكافة الأطفال المسجلين في المنظومة.",
                        color = Color.White,
                        fontSize = 20.sp,
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            ManagementActionCard(
                title = "National Protocol",
                description = "View the unified vaccination schedule and ages.",
                emoji = "🗓️",
                onClick = onNavigateToSchedule
            )

            Spacer(modifier = Modifier.height(20.dp))

            ManagementActionCard(
                title = "Central Registry",
                description = "Access and update children vaccination files.",
                emoji = "📁",
                onClick = onNavigateToRecords
            )

            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "Tateamty Secure Administration v1.0",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}

@Composable
fun ManagementActionCard(
    title: String,
    description: String,
    emoji: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(managementBgColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 32.sp)
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color(0xFF0D47A1),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = description,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    lineHeight = 16.sp
                )
            }
            
            Icon(
                painter = painterResource(id = android.R.drawable.ic_media_play),
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
