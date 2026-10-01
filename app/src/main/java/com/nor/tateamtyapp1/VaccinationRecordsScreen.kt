@file:OptIn(ExperimentalMaterial3Api::class)

package com.nor.tateamtyapp1

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val recordsBgColor = Color(0xFF1E6FAA)

@Composable
fun VaccinationRecordsScreen(
    onBackPressed: () -> Unit,
    onChildClicked: (childId: String) -> Unit,
    recordsViewModel: VaccinationRecordsViewModel = viewModel()
) {
    val state by recordsViewModel.recordsState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cloud Records Registry", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(painterResource(id = R.drawable.ic_arrowback), "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = recordsBgColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(recordsBgColor)
                .padding(paddingValues)
        ) {
            // 💡 اللمسة الإرشادية بالعربي
            Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.End) {
                    Text("نظرة عامة على السجل الرقمي", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "معاينة كافة الأطفال المسجلين في قاعدة البيانات المركزية؛ يمكنك الضغط على أي سجل للوصول إلى تفاصيل الجرعات وتوثيق التطعيمات.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 20.sp,
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Right
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (val currentState = state) {
                    is RecordsState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
                    }
                    is RecordsState.Error -> {
                        Text(text = currentState.message, color = Color.White, modifier = Modifier.align(Alignment.Center))
                    }
                    is RecordsState.Success -> {
                        if (currentState.children.isEmpty()) {
                            Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📭", fontSize = 50.sp)
                                Text("No children found in the registry.", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item {
                                    Text(
                                        text = "Total Records: ${currentState.children.size}",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                items(currentState.children) { child ->
                                    ChildRecordCardEnhanced(child = child, onClick = { onChildClicked(child.id) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChildRecordCardEnhanced(child: Child, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(recordsBgColor.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("👶", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(child.name, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = Color.Black)
                Text("National ID: ${child.nationalId.ifBlank { "N/A" }}", fontSize = 12.sp, color = Color.Gray)
            }
            Icon(
                painter = painterResource(id = android.R.drawable.ic_media_play),
                contentDescription = null,
                tint = recordsBgColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
