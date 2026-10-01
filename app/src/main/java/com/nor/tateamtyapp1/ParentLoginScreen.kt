@file:OptIn(ExperimentalMaterial3Api::class)

package com.nor.tateamtyapp1

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private val parentPortalBgColor = Color(0xFF002D5A)

@Composable
fun ParentLoginScreen(
    onLoginSuccess: (phone: String) -> Unit,
    onBackPressed: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        Text(
                    text = "بوابة ولي الأمر", 
                    fontWeight = FontWeight.ExtraBold, 
                    fontSize = 30.sp
                )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(painterResource(id = R.drawable.ic_arrowback), "رجوع")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = parentPortalBgColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(parentPortalBgColor)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "يرجى إدخال رقم الهاتف المسجل لعرض سجلات أطفالك",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp,
                    modifier = Modifier.padding(bottom = 40.dp)
                )

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { 
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            Text("رقم الهاتف", color = Color.White, fontWeight = FontWeight.Bold) 
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(65.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedContainerColor = Color.White.copy(alpha = 0.1f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.1f)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Right, fontSize = 18.sp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val cleanedPhone = phoneNumber.trim()
                        if (cleanedPhone.isNotEmpty()) {
                            isLoading = true
                            Firebase.firestore.collection("children")
                                .whereEqualTo("guardianPhone", cleanedPhone)
                                .get()
                                .addOnSuccessListener { snapshot ->
                                    isLoading = false
                                    if (!snapshot.isEmpty) {
                                        onLoginSuccess(cleanedPhone)
                                    } else {
                                        Toast.makeText(context, "لم يتم العثور على أطفال بهذا الرقم، يرجى مراجعة المركز الصحي لتحديث البيانات.", Toast.LENGTH_LONG).show()
                                    }
                                }
                                .addOnFailureListener {
                                    isLoading = false
                                    Toast.makeText(context, "خطأ في الاتصال، يرجى التحقق من الإنترنت.", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(context, "يرجى إدخال رقم الهاتف أولاً.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = parentPortalBgColor),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = parentPortalBgColor, modifier = Modifier.size(24.dp))
                    } else {
                        Text("دخول للسجلات", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
