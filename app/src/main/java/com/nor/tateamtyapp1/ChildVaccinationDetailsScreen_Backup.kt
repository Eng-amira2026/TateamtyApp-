/*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

// --- الألوان ---
private val detailsBgColor = Color(0xFF4A8DBA)
private val detailsCardBgColor = Color.White.copy(alpha = 0.9f)
private val givenStatusColor = Color(0xFF4CAF50)
private val pendingStatusColor = Color(0xFFFFA000)
private val overdueStatusColor = Color(0xFFD32F2F)

enum class VaccinationStatus(val displayText: String) {
    GIVEN("Given"),
    PENDING("Pending"),
    OVERDUE("Overdue"),
    DUE_TODAY("Due Today"),
    UNKNOWN("Unknown")
}

@Composable
fun ChildVaccinationDetailsScreen_Backup(
    onBackPressed: () -> Unit,
    isReadOnly: Boolean = false,
    detailsViewModel: ChildVaccinationDetailsViewModel = viewModel()
) {
    val state by detailsViewModel.childDetailsState.collectAsState()
    val vaccineToConfirmState = remember { mutableStateOf<VaccinationRecord?>(null) }

    when (val currentState = state) {
        is ChildDetailsState.Loading -> LoadingScreen_Backup(onBackPressed)
        is ChildDetailsState.Error -> ErrorScreen_Backup(currentState.message, onBackPressed)
        is ChildDetailsState.Success -> {
            // ملاحظة: هذا التبسيط سيعطل العرض القديم لأن الـ ViewModel تغير
            // سنقوم بتحديث الشاشة الأساسية فوراً لتدعم التقسيم الجديد
            Text("Updating to new grouped view...", color = Color.White)
        }
    }
}

@Composable
fun LoadingScreen_Backup(onBackPressed: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Loading...") }, navigationIcon = { IconButton(onClick = onBackPressed) { Icon(painterResource(id = R.drawable.ic_arrowback), "Back") } }) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    }
}

@Composable
fun ErrorScreen_Backup(message: String, onBackPressed: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Error") }, navigationIcon = { IconButton(onClick = onBackPressed) { Icon(painterResource(id = R.drawable.ic_arrowback), "Back") } }) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { Text(text = message, color = MaterialTheme.colorScheme.error) }
    }
}
*/
