package com.nor.tateamtyapp1

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val registerFormBgColor = Color(0xFF1E6FAA)
private val textFieldBgColor = Color(0xFF0052A2)
private val saveButtonColor = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterChildScreen(
    onCancel: () -> Unit,
    registerViewModel: RegisterChildViewModel = viewModel()
) {
    var childName by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Male") }
    var dateOfBirth by remember { mutableStateOf("") }
    var nationalId by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    val registrationState = registerViewModel.registrationState
    val context = LocalContext.current

    LaunchedEffect(registrationState) {
        when (registrationState) {
            is RegistrationState.Success -> {
                Toast.makeText(context, "Child registered successfully!", Toast.LENGTH_SHORT).show()
                registerViewModel.resetState()
                onCancel()
            }
            is RegistrationState.Error -> {
                Toast.makeText(context, registrationState.message, Toast.LENGTH_LONG).show()
                registerViewModel.resetState()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(registerFormBgColor)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Register New Child 👶",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // --- Guideline in Arabic ---
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("💡", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "تأكد من إدخال رقم الهاتف الصحيح المكون من 10 أرقام؛ لضمان وصول رسائل التذكير التلقائية لولي الأمر في المواعيد المحددة.",
                        color = Color.White,
                        fontSize = 20.sp, 
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                FormLabel("Full Name")
                OutlinedTextField(
                    value = childName,
                    onValueChange = { childName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. Rayan Ahmed Ali", color = Color.White.copy(alpha = 0.4f)) },
                    shape = RoundedCornerShape(10.dp),
                    colors = formTextFieldColors(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormLabel("Gender")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    GenderButton(text = "Male", isSelected = selectedGender == "Male", onClick = { selectedGender = "Male" }, modifier = Modifier.weight(1f))
                    GenderButton(text = "Female", isSelected = selectedGender == "Female", onClick = { selectedGender = "Female" }, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                FormLabel("Date of Birth")
                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = { dateOfBirth = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("YYYY-MM-DD (e.g. 2024-05-15)", color = Color.White.copy(alpha = 0.4f)) },
                    shape = RoundedCornerShape(10.dp),
                    colors = formTextFieldColors(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormLabel("National ID")
                OutlinedTextField(
                    value = nationalId,
                    onValueChange = { nationalId = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = formTextFieldColors(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormLabel("Guardian Phone")
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. 0912345678", color = Color.White.copy(alpha = 0.4f)) },
                    shape = RoundedCornerShape(10.dp),
                    colors = formTextFieldColors(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                    modifier = Modifier.weight(1f).height(55.dp).border(1.dp, Color.White, RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Cancel", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        registerViewModel.registerChild(
                            childName = childName,
                            nationalId = nationalId,
                            dateOfBirthStr = dateOfBirth,
                            guardianPhone = phoneNumber
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = saveButtonColor),
                    modifier = Modifier.weight(1.5f).height(55.dp),
                    shape = RoundedCornerShape(10.dp),
                    enabled = registrationState !is RegistrationState.Loading
                ) {
                    if (registrationState is RegistrationState.Loading) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save Data", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun GenderButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val backgroundColor = if (isSelected) Color.White else Color.Transparent
    val contentColor = if (isSelected) registerFormBgColor else Color.White
    val borderColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = contentColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FormLabel(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier.padding(bottom = 6.dp, top = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun formTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.White,
    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
    cursorColor = Color.White,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedContainerColor = textFieldBgColor.copy(alpha = 0.3f),
    unfocusedContainerColor = textFieldBgColor.copy(alpha = 0.3f)
)
