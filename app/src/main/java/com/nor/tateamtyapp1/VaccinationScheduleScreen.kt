@file:OptIn(ExperimentalFoundationApi::class)

package com.nor.tateamtyapp1

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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

private val scheduleBgColor = Color(0xFF1E6FAA)
private val categoryHeaderColor = Color(0xFFFBC02D)

data class VaccineEntry(
    val name: String,
    val category: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccinationScheduleScreen(
    onBackPressed: () -> Unit
) {
    val groupedSchedule = remember {
        listOf(
            VaccineEntry("BCG", "At Birth", "Protects against Tuberculosis"),
            VaccineEntry("Hepatitis B (Birth)", "At Birth", "First dose for Hep-B prevention"),
            VaccineEntry("Polio (IPV) - Dose 1", "2 Months", "First dose for Polio immunization"),
            VaccineEntry("DPT-HepB-Hib 1", "2 Months", "Pentavalent vaccine dose 1"),
            VaccineEntry("Rotavirus 1", "2 Months", "Prevention of severe diarrhea"),
            VaccineEntry("Polio (IPV) - Dose 2", "4 Months", "Second dose for Polio"),
            VaccineEntry("DPT-HepB-Hib 2", "4 Months", "Pentavalent vaccine dose 2"),
            VaccineEntry("Rotavirus 2", "4 Months", "Second dose for Rota"),
            VaccineEntry("Polio (IPV) - Dose 3", "6 Months", "Third dose for Polio"),
            VaccineEntry("DPT-HepB-Hib 3", "6 Months", "Pentavalent vaccine dose 3"),
            VaccineEntry("Measles - Dose 1", "9 Months", "First protection against Measles"),
            VaccineEntry("MMR - Dose 1", "12 Months", "Measles, Mumps, and Rubella"),
            VaccineEntry("MMR - Dose 2", "18 Months", "Final booster for MMR")
        ).groupBy { it.category }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Immunization Protocol", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(painterResource(id = R.drawable.ic_arrowback), "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = scheduleBgColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(scheduleBgColor)
                .padding(paddingValues)
        ) {
            // 💡 اللمسة الإرشادية بالعربي
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("📋", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "يعرض هذا الجدول بروتوكول التطعيم الوطني المعتمد؛ يرجى الالتزام بهذه المواعيد عند تسجيل الأطفال الجدد وتحديث سجلاتهم الصحية.",
                        color = Color.White,
                        fontSize = 20.sp,
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                groupedSchedule.forEach { (category, vaccines) ->
                    stickyHeader {
                        ScheduleHeader(category)
                    }
                    items(vaccines) { vaccine ->
                        ScheduleItemCard(vaccine)
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D47A1))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "📍 $title",
            color = categoryHeaderColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp
        )
    }
}

@Composable
fun ScheduleItemCard(vaccine: VaccineEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(scheduleBgColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("💉", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(vaccine.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                Text(vaccine.description, fontSize = 18.sp, color = Color.Gray)
            }
        }
    }
}
