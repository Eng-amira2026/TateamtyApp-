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
import java.text.SimpleDateFormat
import java.util.*

private val detailsBgColor = Color(0xFF1E6FAA)
private val detailsCardBgColor = Color.White.copy(alpha = 0.95f)
private val givenStatusColor = Color(0xFF4CAF50)
private val overdueStatusColor = Color(0xFFD32F2F)

@Composable
fun ChildVaccinationDetailsScreen(
    onBackPressed: () -> Unit,
    isReadOnly: Boolean = false,
    detailsViewModel: ChildVaccinationDetailsViewModel = viewModel()
) {
    val state by detailsViewModel.childDetailsState.collectAsState()
    val vaccineToConfirmState = remember { mutableStateOf<VaccinationRecord?>(null) }

    when (val currentState = state) {
        is ChildDetailsState.Loading -> LoadingScreen(onBackPressed, isReadOnly)
        is ChildDetailsState.Error -> ErrorScreen(currentState.message, onBackPressed, isReadOnly)
        is ChildDetailsState.Success -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { 
                            Text(
                                if (isReadOnly) "Immunization Status" else "Manage Child Doses", 
                                fontWeight = FontWeight.Bold
                            ) 
                        },
                        navigationIcon = {
                            IconButton(onClick = onBackPressed) {
                                Icon(painterResource(id = R.drawable.ic_arrowback), "Back")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = detailsBgColor,
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White
                        )
                    )
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize().background(detailsBgColor).padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Guidance Card in Arabic (Only for Staff)
                    if (!isReadOnly) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                            ) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text("💡", fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "تنبيه: لتسجيل إعطاء جرعة، اضغط على اسم اللقاح الذي تظهر حالته (اليوم) أو (متأخر) وقم بالتأكيد. سيقوم النظام آلياً بتحديث موعد الجرعة التالية.",
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
                    }

                    item {
                        ChildHeaderInfo(currentState.childDetails, isReadOnly)
                    }

                    currentState.groupedRecords.toSortedMap().forEach { (age, vaccines) ->
                        item {
                            val ageText = if (isReadOnly) {
                                when(age) {
                                    0 -> "عند الولادة"
                                    else -> "عمر $age شهر"
                                }
                            } else {
                                when(age) {
                                    0 -> "At Birth"
                                    else -> "Age: $age Months"
                                }
                            }
                            SectionHeader(ageText, Color.Yellow, isReadOnly)
                        }

                        items(vaccines) { vaccine ->
                            VaccineStatusCard(
                                vaccine = vaccine,
                                isReadOnly = isReadOnly,
                                onClick = {
                                    if (!isReadOnly && (vaccine.status == "OVERDUE" || vaccine.status == "DUE_TODAY")) {
                                        vaccineToConfirmState.value = vaccine
                                    }
                                }
                            )
                        }
                    }
                }
            }

            if (vaccineToConfirmState.value != null) {
                ConfirmDialog(
                    vaccineName = vaccineToConfirmState.value!!.vaccineName,
                    onConfirm = {
                        currentState.childDetails.dateOfBirth?.let { dob ->
                            detailsViewModel.confirmVaccination(vaccineToConfirmState.value!!, dob)
                        }
                        vaccineToConfirmState.value = null
                    },
                    onDismiss = { vaccineToConfirmState.value = null }
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, color: Color, isReadOnly: Boolean) {
    Text(
        text = title,
        color = color,
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp).fillMaxWidth(),
        textAlign = if (isReadOnly) TextAlign.Right else TextAlign.Left
    )
}

@Composable
fun ChildHeaderInfo(child: ChildVaxInfo, isReadOnly: Boolean) {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    Column(modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(), horizontalAlignment = if (isReadOnly) Alignment.End else Alignment.Start) {
        Text(child.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        child.dateOfBirth?.let {
            Text(
                if (isReadOnly) "تاريخ الميلاد: ${formatter.format(it)}" else "Born on: ${formatter.format(it)}",
                fontSize = 16.sp, 
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
    HorizontalDivider(color = Color.White.copy(alpha = 0.3f))
}

@Composable
fun VaccineStatusCard(vaccine: VaccinationRecord, isReadOnly: Boolean, onClick: () -> Unit) {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val isClickable = !isReadOnly && (vaccine.status == "OVERDUE" || vaccine.status == "DUE_TODAY")

    Card(
        modifier = Modifier.fillMaxWidth().clickable(enabled = isClickable, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = detailsCardBgColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isReadOnly) Arrangement.End else Arrangement.Start
        ) {
            if (!isReadOnly) StatusBadge(vaccine.status, false)
            
            Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), horizontalAlignment = if (isReadOnly) Alignment.End else Alignment.Start) {
                Text(vaccine.vaccineName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                
                val dateInfo = when(vaccine.status) {
                    "GIVEN" -> if (isReadOnly) "تم أخذه في: ${vaccine.administeredDate?.let { formatter.format(it) }}" else "Taken on: ${vaccine.administeredDate?.let { formatter.format(it) }}"
                    "DUE_TODAY" -> if (isReadOnly) "موعده اليوم!" else "Due Today!"
                    else -> if (isReadOnly) "الموعد المقرر: ${vaccine.dueDate?.let { formatter.format(it) }}" else "Scheduled: ${vaccine.dueDate?.let { formatter.format(it) }}"
                }
                
                Text(
                    text = dateInfo,
                    fontSize = 18.sp,
                    color = if (vaccine.status == "OVERDUE") overdueStatusColor else Color.DarkGray
                )
            }
            if (isReadOnly) StatusBadge(vaccine.status, true)
        }
    }
}

@Composable
fun StatusBadge(status: String, isArabic: Boolean) {
    val (color, text) = when(status) {
        "GIVEN" -> givenStatusColor to (if (isArabic) "مكتمل" else "Done")
        "OVERDUE" -> overdueStatusColor to (if (isArabic) "متأخر" else "Overdue")
        "DUE_TODAY" -> overdueStatusColor to (if (isArabic) "اليوم" else "Today")
        else -> Color.Gray to (if (isArabic) "قادم" else "Pending")
    }
    Surface(
        color = color,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(start = 4.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun ConfirmDialog(vaccineName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Vaccination") },
        text = { Text("Are you sure you want to document '$vaccineName' for this child?") },
        confirmButton = { Button(onClick = onConfirm) { Text("Confirm") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun LoadingScreen(onBackPressed: () -> Unit, isArabic: Boolean) {
    Scaffold(topBar = { TopAppBar(title = { Text(if (isArabic) "جاري التحميل..." else "Loading...") }, navigationIcon = { IconButton(onClick = onBackPressed) { Icon(painterResource(id = R.drawable.ic_arrowback), "Back") } }) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color.White) }
    }
}

@Composable
fun ErrorScreen(message: String, onBackPressed: () -> Unit, isArabic: Boolean) {
    Scaffold(topBar = { TopAppBar(title = { Text(if (isArabic) "خطأ" else "Error") }, navigationIcon = { IconButton(onClick = onBackPressed) { Icon(painterResource(id = R.drawable.ic_arrowback), "Back") } }) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text(text = message, color = MaterialTheme.colorScheme.error) }
    }
}
