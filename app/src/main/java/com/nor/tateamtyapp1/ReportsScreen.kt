@file:OptIn(ExperimentalMaterial3Api::class)

package com.nor.tateamtyapp1

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val reportsBgColor = Color(0xFF1E6FAA)

data class ReportOption(
    val title: String,
    val description: String,
    val emoji: String,
    val onClick: () -> Unit
)

@Composable
fun ReportsScreen(
    onBackPressed: () -> Unit,
    onNavigateToVaccinationsOverTime: () -> Unit,
    onNavigateToCompletionReport: () -> Unit 
) {
    val options = remember {
        listOf(
            ReportOption(
                title = "Performance Trends",
                description = "View monthly and annual vaccination charts and growth rates.",
                emoji = "📈",
                onClick = onNavigateToVaccinationsOverTime
            ),
            ReportOption(
                title = "Completion Registry",
                description = "Detailed stats on fully immunized vs pending cases.",
                emoji = "📊",
                onClick = onNavigateToCompletionReport
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Intelligence & Reports", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(painterResource(id = R.drawable.ic_arrowback), "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = reportsBgColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(reportsBgColor)
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 💡 اللمسة الإرشادية بالعربي
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("💡", fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "القرارات الذكية تبدأ من هنا؛ يمكنك مراقبة تغطية التطعيمات وكفاءة العمل في المركز الصحي من خلال التحليلات اللحظية المتاحة في هذا القسم.",
                        color = Color.White,
                        fontSize = 20.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(options) { option ->
                    ReportHubCard(option = option)
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = "Tateamty Analytics Engine v1.0",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun ReportHubCard(option: ReportOption) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = option.onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(reportsBgColor.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = option.emoji, fontSize = 28.sp)
            }
            
            Spacer(modifier = Modifier.width(20.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    color = Color(0xFF0D47A1),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = option.description,
                    color = Color.Gray,
                    fontSize = 18.sp,
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
