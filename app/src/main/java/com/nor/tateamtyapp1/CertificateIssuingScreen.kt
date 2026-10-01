@file:OptIn(ExperimentalMaterial3Api::class)

package com.nor.tateamtyapp1

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

private val certificateBgColor = Color(0xFF1E6FAA) 
private val fieldContainerColor = Color(0xFF4A8DBA).copy(alpha = 0.4f) 
private val successGreen = Color(0xFF4CAF50)
private val warningOrange = Color(0xFFFF9800)

@Composable
fun CertificateIssuingScreen(
    nationalId: String,
    onBackPressed: () -> Unit,
    certificateViewModel: CertificateIssuingViewModel = viewModel()
) {
    val state by certificateViewModel.state.collectAsState()
    var signerName by remember { mutableStateOf("") }
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val context = LocalContext.current

    LaunchedEffect(nationalId) {
        certificateViewModel.fetchChildByNationalId(nationalId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vaccination Certificate", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(painterResource(id = R.drawable.ic_arrowback), "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = certificateBgColor)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(certificateBgColor)
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                is CertificateState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
                }
                is CertificateState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("❌", fontSize = 50.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(currentState.message, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                        Button(onClick = onBackPressed, modifier = Modifier.padding(top = 24.dp)) {
                            Text("Try Another ID", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                is CertificateState.Success -> {
                    CertificateContentFinal(
                        nationalId = nationalId,
                        childName = currentState.childName,
                        dob = currentState.dob?.let { dateFormatter.format(it) } ?: "N/A",
                        completedVaccines = currentState.completedVaccines,
                        isEligible = currentState.isFullyVaccinated,
                        signerName = signerName,
                        onSignerNameChange = { signerName = it },
                        onPrintClicked = {
                            PdfGenerator.generateCertificatePdf(
                                context = context,
                                childName = currentState.childName,
                                nationalId = nationalId,
                                dob = currentState.dob?.let { dateFormatter.format(it) } ?: "N/A",
                                vaccines = currentState.completedVaccines,
                                signerName = signerName
                            )
                        },
                        onBackPressed = onBackPressed
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
fun CertificateContentFinal(
    nationalId: String,
    childName: String,
    dob: String,
    completedVaccines: List<String>,
    isEligible: Boolean,
    signerName: String,
    onSignerNameChange: (String) -> Unit,
    onPrintClicked: () -> Unit,
    onBackPressed: () -> Unit
) {
    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // 1. Banner
        EligibilityBannerLarge(isEligible)

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Information Rows (Labels and Values close together)
        CompactRowStyled(label = "Child Name", value = childName, emoji = "👶")
        Spacer(modifier = Modifier.height(12.dp))
        CompactRowStyled(label = "National ID", value = nationalId, emoji = "💳")
        Spacer(modifier = Modifier.height(12.dp))
        CompactRowStyled(label = "Date of Birth", value = dob, emoji = "🎂")
        Spacer(modifier = Modifier.height(12.dp))
        CompactRowStyled(label = "Certificate Date", value = currentDate, emoji = "📅")

        Spacer(modifier = Modifier.height(30.dp))

        // 3. Completed Vaccines Section
        Text(
            text = "Completed Vaccines",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0D47A1).copy(alpha = 0.4f))
                .padding(16.dp)
        ) {
            Column {
                completedVaccines.forEach { vaccine ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "•", color = successGreen, fontSize = 28.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = vaccine,
                            color = Color.White,
                            fontSize = 20.sp, 
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // 4. Signer Row (Labels and Input close together)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text("✍️", fontSize = 28.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Signer's Name",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Black
            )
            
            Spacer(modifier = Modifier.width(16.dp)) // Moderate space
            
            TextField(
                value = signerName,
                onValueChange = onSignerNameChange,
                modifier = Modifier
                    .width(180.dp)
                    .clip(RoundedCornerShape(10.dp)),
                placeholder = { Text("NAME...", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                singleLine = true
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp)) // Small gap between Signer and Print button

        // 5. Scrollable Action Buttons with Pulse Effect
        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = if (isEligible && signerName.isNotBlank()) 1.05f else 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(60.dp)
                    .clickable { onBackPressed() },
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 6.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(painterResource(id = R.drawable.ic_arrowback), "Back", tint = Color.Black)
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Button(
                onClick = onPrintClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .scale(scale), // Applying the pulse animation
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEligible && signerName.isNotBlank()) successGreen else Color.Gray,
                    contentColor = Color.White
                ),
                enabled = isEligible && signerName.isNotBlank(),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = "Print / View as PDF",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun CompactRowStyled(label: String, value: String, emoji: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // Label Section (Fixed width to align boxes)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(155.dp)
        ) {
            Text(emoji, fontSize = 26.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp)) // Fixed small gap

        // Data Box
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(55.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(fieldContainerColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                color = Color.White,
                fontSize = 20.sp, 
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EligibilityBannerLarge(isEligible: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isEligible) successGreen.copy(alpha = 0.25f) else warningOrange.copy(alpha = 0.25f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isEligible) "✅ الطفل مؤهل لاستلام الشهادة" else "⚠️ الجدول غير مكتمل - غير مؤهل للإصدار",
            color = if (isEligible) successGreen else warningOrange,
            fontWeight = FontWeight.Black,
            fontSize = 28.sp,
            textAlign = TextAlign.Center
        )
    }
}
