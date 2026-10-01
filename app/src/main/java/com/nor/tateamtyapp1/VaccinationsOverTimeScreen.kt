@file:OptIn(ExperimentalMaterial3Api::class)

package com.nor.tateamtyapp1

import android.content.Context
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.core.entry.entryModelOf

private val chartBgColor = Color(0xFF1E6FAA)

@Composable
fun VaccinationsOverTimeScreen(
    onBackPressed: () -> Unit,
    viewModel: VaccinationsOverTimeViewModel = viewModel()
) {
    val state by viewModel.chartState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } 
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Performance Reports", fontWeight = FontWeight.Black, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(painterResource(id = R.drawable.ic_arrowback), "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        if (state is ChartState.Success) {
                            val successState = state as ChartState.Success
                            val dataToShow = if (selectedTab == 0) successState.monthlyData else successState.yearlyData
                            shareReport(context, dataToShow, selectedTab == 0)
                        }
                    }) {
                        Icon(painterResource(id = android.R.drawable.ic_menu_share), "Share", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = chartBgColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(chartBgColor)
                .padding(paddingValues)
        ) {
            // 1. Arabic Guidance Card
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("📈", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "راقب اتجاهات التطعيم الشهرية والسنوية لاكتشاف فترات الذروة وضمان توفر اللقاحات الكافية في المركز الصحي بانتظام.",
                        color = Color.White,
                        fontSize = 20.sp,
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 2. Modern Tabs
            SecondaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color.White,
                indicator = { 
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTab), 
                        color = Color.White 
                    ) 
                },
                divider = {}
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Monthly", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Yearly", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) })
            }

            Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                when (val currentState = state) {
                    is ChartState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
                    is ChartState.Error -> Text(currentState.message, color = Color.White, modifier = Modifier.align(Alignment.Center))
                    is ChartState.Success -> {
                        val dataToShow = if (selectedTab == 0) currentState.monthlyData else currentState.yearlyData
                        
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
                        ) {
                            // 3. Animated Visual Chart Card
                            item {
                                var visible by remember { mutableStateOf(false) }
                                LaunchedEffect(Unit) { visible = true }
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = visible,
                                    enter = expandVertically(animationSpec = tween(1000)) + fadeIn()
                                ) {
                                    VisualChartCardImageStyle(data = dataToShow)
                                }
                            }
                            
                            // 4. Section Header
                            item {
                                Text(
                                    "Numerical Data List", 
                                    color = Color.White, 
                                    fontWeight = FontWeight.Black, 
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }

                            // 5. Staggered Modern Data Rows
                            itemsIndexed(dataToShow.filter { it.value > 0 }) { index, item ->
                                var visible by remember { mutableStateOf(false) }
                                LaunchedEffect(Unit) {
                                    delay(100L * index) // Staggered delay
                                    visible = true
                                }
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = visible,
                                    enter = slideInVertically(initialOffsetY = { 50 }) + fadeIn()
                                ) {
                                    ModernDataRow(label = item.label, value = item.value.toInt())
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
fun VisualChartCardImageStyle(data: List<ChartData>) {
    val entryModel = entryModelOf(*data.map { it.value }.toTypedArray())
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Chart(
                chart = columnChart(),
                model = entryModel,
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(
                    valueFormatter = { value, _ -> data.getOrNull(value.toInt())?.label ?: "" }
                ),
                modifier = Modifier.fillMaxWidth().height(220.dp)
            )
        }
    }
}

@Composable
fun ModernDataRow(label: String, value: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label, 
                fontWeight = FontWeight.Black, 
                color = Color.Black,
                fontSize = 18.sp
            )
            Text(
                text = "$value Vaccinations", 
                fontWeight = FontWeight.Bold, 
                color = Color(0xFF1E6FAA), 
                fontSize = 20.sp 
            )
        }
    }
}

private fun shareReport(context: Context, data: List<ChartData>, isMonthly: Boolean) {
    val reportType = if (isMonthly) "Monthly" else "Annual"
    val reportText = StringBuilder()
    reportText.append("📊 Tateamty System Performance Report ($reportType)\n")
    reportText.append("Generated on: ${java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())}\n")
    reportText.append("--------------------------\n")
    data.filter { it.value > 0 }.forEach { item ->
        reportText.append("${item.label}: ${item.value.toInt()} vaccinations\n")
    }
    val total = data.sumOf { it.value.toInt() }
    reportText.append("--------------------------\n")
    reportText.append("Total Vaccinations: $total\n")
    
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, reportText.toString())
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Send Report to Management:"))
}

@Preview(showBackground = true)
@Composable
fun PerformanceReportsPreview() {
    val dummyData = listOf(
        ChartData("Jan", 42f),
        ChartData("Feb", 2f),
        ChartData("Mar", 10f),
        ChartData("Apr", 0f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E6FAA))
    ) {
        // Mocking the guidance card
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("📈", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "راقب اتجاهات التطعيم الشهرية والسنوية لاكتشاف فترات الذروة وضمان توفر اللقاحات الكافية في المركز الصحي بانتظام.",
                    color = Color.White,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Mocking Tabs
        SecondaryTabRow(
            selectedTabIndex = 0, 
            containerColor = Color.Transparent, 
            contentColor = Color.White,
            indicator = { 
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(0), 
                    color = Color.White 
                ) 
            },
            divider = {}
        ) {
            Tab(selected = true, onClick = {}, text = { Text("Monthly", color = Color.White, fontWeight = FontWeight.Bold) })
            Tab(selected = false, onClick = {}, text = { Text("Yearly", color = Color.White, fontWeight = FontWeight.Bold) })
        }

        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                VisualChartCardImageStyle(data = dummyData)
            }
            item {
                Text("Numerical Data List", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
            }
            itemsIndexed(dummyData.filter { it.value > 0 }) { _, item ->
                ModernDataRow(label = item.label, value = item.value.toInt())
            }
        }
    }
}
